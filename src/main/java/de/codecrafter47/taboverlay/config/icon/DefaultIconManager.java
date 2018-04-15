package de.codecrafter47.taboverlay.config.icon;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.ProfileProperty;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewConstant;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class DefaultIconManager implements IconManager {

    private final ScheduledExecutorService asyncExecutor;
    private final ScheduledExecutorService tabEventQueue;
    private final Path iconFolder;
    private final Logger logger;
    private final Cache<String, IconEntry> cache = CacheBuilder.newBuilder().weakValues().build();
    private final Map<IconImageData, Icon> iconCache = new ConcurrentHashMap<>();

    private final static Pattern PATTERN_VALID_USERNAME = Pattern.compile("(?:\\p{Alnum}|_){1,16}");
    private final static Pattern PATTERN_VALID_UUID = Pattern.compile("(?i)[a-f0-9]{8}-?[a-f0-9]{4}-?4[a-f0-9]{3}-?[89ab][a-f0-9]{3}-?[a-f0-9]{12}");
    private final static Gson gson = new Gson();


    public DefaultIconManager(ScheduledExecutorService asyncExecutor, ScheduledExecutorService tabEventQueue, Path iconFolder, Logger logger) {
        this.asyncExecutor = asyncExecutor;
        this.tabEventQueue = tabEventQueue;
        this.iconFolder = iconFolder;
        this.logger = logger;
        loadIconCache();
    }

    private void loadIconCache() {
        Path file = iconFolder.resolve("cache.txt");
        if (Files.exists(file)) {
            try {
                new BufferedReader(Files.newBufferedReader(file)).lines()
                        .filter(line -> !line.isEmpty())
                        .map(line -> line.split(" "))
                        .forEach(entry -> iconCache.put(
                                IconImageData.of(Base64.getDecoder().decode(entry[0])),
                                new Icon(new ProfileProperty("textures", entry[1], entry[2]))
                        ));
            } catch (Throwable th) {
                logger.log(Level.WARNING, "Failed to load icons/cache.txt", th);
            }
        }
    }

    @Override
    public synchronized IconTemplate createIconTemplate(String s, TemplateCreationContext tcc) {
        if (s.contains("\\$\\{")) {
            // todo contains a placeholder
        } else {
            IconEntry entry = cache.getIfPresent(s);

            if (entry != null) {
                return entry;
            }

            if (PATTERN_VALID_USERNAME.matcher(s).matches()) {
                CompletableFuture<Icon> future = fetchUuid(s).thenCompose(this::fetchIcon);
                entry = new IconEntry(future);
            } else if (PATTERN_VALID_UUID.matcher(s).matches()) {
                CompletableFuture<Icon> future = fetchIcon(UUID.fromString(s));
                entry = new IconEntry(future);
            } else if (s.endsWith(".png")) {
                CompletableFuture<Icon> future = fetchIconFromImage(iconFolder.resolve(s));
                entry = new IconEntry(future);
            } else {
                // todo
            }

            if (entry != null) {
                cache.put(s, entry);
                return entry;
            } else {
                // todo
            }
        }
        // todo log and return error
        return IconTemplate.STEVE;
    }

    private CompletableFuture<Icon> fetchIconFromImage(Path path) {
        CompletableFuture<Icon> future = new CompletableFuture<>();

        asyncExecutor.submit(() -> fetchIconFromImage(path, future));

        return future;
    }

    private void fetchIconFromImage(Path path, CompletableFuture<Icon> future) {

        try {
            BufferedImage head = ImageIO.read(Files.newInputStream(path));
            if (head.getWidth() != 8 || head.getHeight() != 8) {
                logger.warning("Image " + path.toString() + " has the wrong size. Required 8x8 actual " + head.getWidth() + "x" + head.getHeight()); // todo better error handling
                future.completeExceptionally(new Exception("wrong image size"));
                return;
            }

            int[] rgb = head.getRGB(0, 0, 8, 8, null, 0, 8);
            ByteBuffer byteBuffer = ByteBuffer.allocate(rgb.length * 4);
            byteBuffer.asIntBuffer().put(rgb);
            byte[] headArray = byteBuffer.array();

            IconImageData imageData = IconImageData.of(headArray);
            if (iconCache.containsKey(imageData)) {
                future.complete(iconCache.get(imageData));
            } else {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL("http://skinservice.codecrafter47.dyndns.eu/api/customhead").openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    try (DataOutputStream out = new DataOutputStream(connection.
                            getOutputStream())) {
                        out.write((Base64.getEncoder().encodeToString(headArray)).getBytes(Charsets.UTF_8));
                        out.flush();
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
                    LinkedHashTreeMap map = gson.fromJson(reader, LinkedHashTreeMap.class);
                    if (map.get("state").equals("ERROR")) {
                        logger.warning("An server side error occurred while preparing head " + path.toString()); // todo better error handling
                        future.completeExceptionally(new Exception()); // todo retry or fall back to a different service in this case
                    } else if (map.get("state").equals("QUEUED")) {
                        logger.info("Preparing head " + path.toString() + " approx. " + map.get("timeLeft") + " minutes remaining."); // todo silence this message
                        asyncExecutor.schedule(() -> fetchIconFromImage(path, future), 5, TimeUnit.SECONDS); // todo make reading the file a separate step to avoid reading it many times
                    } else if (map.get("state").equals("SUCCESS")) {
                        Icon icon = new Icon(new ProfileProperty("textures", (String) map.get("skin"), (String) map.get("signature")));
                        iconCache.put(imageData, icon);
                        logger.info("Head " + path.toString() + " is now ready for use."); // todo silence message

                        future.complete(icon);

                        // save to cache
                        // todo lock
                        Path cacheFile = iconFolder.resolve("cache.txt");
                        BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(cacheFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
                        writer.write(Base64.getEncoder().encodeToString(headArray));
                        writer.write(' ');
                        writer.write(icon.getTextureProperty().getValue());
                        writer.write(' ');
                        writer.write(icon.getTextureProperty().getSignature());
                        writer.newLine();
                        writer.close();
                    } else {
                        logger.severe("Unexpected response from server: " + map.get("state")); // todo better error handling
                        future.completeExceptionally(new Exception()); // todo retry or fallback to a different service in this case
                    }
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "An error occurred while trying to contact skinservice.codecrafter47.dyndns.eu", ex);
                    logger.warning("Unable to prepare head " + path.toString()); // todo better error handling
                    // retry after 5 minutes
                    // todo limit retries/ switch to a different service
                    asyncExecutor.schedule(() -> fetchIconFromImage(path, future), 5, TimeUnit.MINUTES); // todo make reading the file a separate step to avoid reading it many times
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        } catch (IOException ex) {
            logger.warning("Failed to load file " + path.toString()); // todo better error handling
            future.completeExceptionally(ex);
        }
    }

    private CompletableFuture<Icon> fetchIcon(UUID uuid) {
        CompletableFuture<Icon> future = new CompletableFuture<>();

        asyncExecutor.submit(() -> fetchIconFromMojang(uuid, future));

        return future;
    }

    private void fetchIconFromMojang(UUID uuid, CompletableFuture<Icon> future) {
        if (future.isCancelled())
            return;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false").openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
            SkinProfile skin = gson.fromJson(reader, SkinProfile.class);
            if (skin != null && skin.properties != null && !skin.properties.isEmpty()) {
                future.complete(new Icon(new ProfileProperty("textures", skin.properties.get(0).value, skin.properties.get(0).signature)));
            } else {
                future.completeExceptionally(new Exception("No skin associated with uuid '" + uuid + "'"));
            }
        } catch (Throwable e) {
            if (e instanceof IOException && e.getMessage().contains("429")) {
                // Mojang rate limit; try again later
                logger.info("Hit Mojang rate limits while fetching skin for " + uuid + ". Will retry in 1 minute. (This is not an error)");
                asyncExecutor.schedule(() -> fetchIconFromMojang(uuid, future), 1, TimeUnit.MINUTES);
            } else if (e instanceof IOException) {
                // generic connection error
                logger.log(Level.WARNING, "An error occurred while connecting to Mojang servers. Couldn't fetch skin for " + uuid + ". Will retry in 5 minutes.", e);
                asyncExecutor.schedule(() -> fetchIconFromMojang(uuid, future), 5, TimeUnit.MINUTES);
            } else {
                logger.log(Level.SEVERE, "Unexpected error.", e);
                future.completeExceptionally(e);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private CompletableFuture<UUID> fetchUuid(String username) {
        CompletableFuture<UUID> future = new CompletableFuture<>();

        asyncExecutor.submit(() -> fetchUuidFromMojang(username, future));

        return future;
    }

    private void fetchUuidFromMojang(String username, CompletableFuture<UUID> future) {
        if (future.isCancelled())
            return;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(
                    "https://api.mojang.com/profiles/minecraft").
                    openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            try (DataOutputStream out = new DataOutputStream(connection.
                    getOutputStream())) {
                out.write(("[\"" + username + "\"]").getBytes(Charsets.UTF_8));
                out.flush();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), Charsets.UTF_8));
            Profile[] profiles = gson.fromJson(reader, Profile[].class);
            if (profiles != null && profiles.length >= 1) {
                future.complete(UUID.fromString(profiles[0].id.substring(0, 8) + "-" + profiles[0].id.substring(8, 12) + "-" + profiles[0].id.substring(12, 16) + "-" + profiles[0].id.substring(16, 20) + "-" + profiles[0].id.substring(20)));
            } else {
                future.completeExceptionally(new Exception("No uuid associated with username '" + username + "'"));
            }
        } catch (Throwable e) {
            if (e instanceof IOException && e.getMessage().contains("429")) {
                // mojang rate limit; try again later
                logger.warning("Hit Mojang rate limits while fetching uuid for " + username + ".");
                String headerField = connection.getHeaderField("Retry-After");
                asyncExecutor.schedule(() -> fetchUuidFromMojang(username, future), headerField == null ? 300 : Integer.valueOf(headerField), TimeUnit.SECONDS);
            } else if (e instanceof IOException) {
                // generic connection error, retry in 5 minutes
                logger.warning("An error occurred while connecting to Mojang servers: " + e.getMessage() + ". Will retry in 5 minutes.");
                asyncExecutor.schedule(() -> fetchUuidFromMojang(username, future), 5, TimeUnit.MINUTES);
            } else {
                logger.log(Level.SEVERE, "Unexpected error.", e);
                future.completeExceptionally(e);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private class IconEntry implements IconTemplate {
        private Supplier<IconView> factory;
        private List<Runnable> listeners = new ArrayList<>();

        public IconEntry(CompletableFuture<Icon> iconProvider) {
            factory = IconViewDelegate::new;
            iconProvider.exceptionally(th -> Icon.DEFAULT_ALEX /* todo error icon */)
                    .thenAcceptAsync(icon -> {
                        IconViewConstant iconView = new IconViewConstant(icon);
                        factory = () -> iconView;
                        for (Runnable listener : listeners) {
                            listener.run();
                        }
                        listeners = null;
                    }, tabEventQueue);
        }

        public IconView createIconView() {
            return factory.get();
        }

        @Override
        public IconView instantiate() {
            return factory.get();
        }

        private class IconViewDelegate extends AbstractActiveElement<IconViewUpdateListener> implements IconView, Runnable {

            private IconView delegate = new IconViewLoading();

            @Override
            protected void onActivation() {
                if (listeners != null) {
                    listeners.add(this);
                }
                delegate.activate(getContext(), getListener());
            }

            @Override
            protected void onDeactivation() {
                if (listeners != null) {
                    listeners.remove(this);
                }
                delegate.deactivate();
            }

            @Override
            public Icon getIcon() {
                return delegate.getIcon();
            }

            @Override
            public void run() {
                delegate.deactivate();
                delegate = factory.get();
                delegate.activate(getContext(), getListener());
                if (hasListener()) {
                    getListener().onIconUpdated();
                }
            }
        }
    }

    private static class IconViewLoading extends AbstractActiveElement<IconViewUpdateListener> implements IconView, Runnable {
        private static final long ANIMATION_INTERVAL_MS = 500;
        private static final Icon[] ANIMATION_ELEMENTS = new Icon[]{Icon.DEFAULT_STEVE};// todo loading icons
        private ScheduledFuture<?> animationUpdateTask;
        private int currentElement = 0;

        @Override
        protected void onActivation() {
            animationUpdateTask = getContext().getTabEventQueue().scheduleWithFixedDelay(this, ANIMATION_INTERVAL_MS, ANIMATION_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }

        @Override
        protected void onDeactivation() {
            animationUpdateTask.cancel(false);
        }

        @Override
        public void run() {
            currentElement += 1;
            if (currentElement >= ANIMATION_ELEMENTS.length) {
                currentElement = 0;
            }
            if (hasListener()) {
                getListener().onIconUpdated();
            }
        }

        @Override
        public Icon getIcon() {
            return ANIMATION_ELEMENTS[currentElement];
        }
    }

    private static final class IconImageData {
        private final byte[] bytes;

        private IconImageData(byte[] bytes) {
            this.bytes = bytes;
        }

        static IconImageData of(byte[] bytes) {
            return new IconImageData(bytes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IconImageData && Arrays.equals(bytes, ((IconImageData) obj).bytes);
        }
    }

    private static class Profile {

        private String id;
        private String name;
    }

    private static class SkinProfile {

        private String id;
        private String name;

        final List<Property> properties = new ArrayList<>();

        private static class Property {

            private String name, value, signature;
        }
    }
}
