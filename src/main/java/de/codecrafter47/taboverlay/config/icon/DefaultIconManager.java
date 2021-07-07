/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay.config.icon;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedHashTreeMap;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.ProfileProperty;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.template.icon.ConstantIconTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewConstant;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import org.yaml.snakeyaml.error.Mark;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DefaultIconManager implements IconManager {

    private final ScheduledExecutorService asyncExecutor;
    private final ScheduledExecutorService tabEventQueue;
    private final Path iconFolder;
    private final Logger logger;
    private final Cache<UUID, CompletableFuture<Icon>> cacheUUID = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    private final Cache<String, IconTemplate> cache = CacheBuilder.newBuilder().weakValues().build();
    private final Map<IconImageData, Icon> iconCache = new ConcurrentHashMap<>();

    private final static Pattern PATTERN_VALID_USERNAME = Pattern.compile("(?:\\p{Alnum}|_){1,16}");
    private final static Pattern PATTERN_VALID_UUID = Pattern.compile("(?i)[a-f0-9]{8}-?[a-f0-9]{4}-?4[a-f0-9]{3}-?[89ab][a-f0-9]{3}-?[a-f0-9]{12}");
    private final static Gson gson = new Gson();

    private final static Icon ICON_ERROR = new Icon(new ProfileProperty("textures", "eyJ0aW1lc3RhbXAiOjE1MzI3MDEwODA1NDMsInByb2ZpbGVJZCI6ImIzYjE4MzQ1MzViZjRiNzU4ZTBjZGJmMGY4MjA2NTZlIiwicHJvZmlsZU5hbWUiOiIxMDExMTEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FkYzk4N2NmYmEyNDRjYzg1MGNjOGUxMmFhNGYyNzEwNzNmMTgwNWU0YjhlODliZjRmMjAxZjZkMDAzNGU0NzMifX19", "ctcSWWnrcwQusiZs3qhDComk1qmFyfls+ARVKDOW6DNGXgytqlArb1XpakDuLYGAS5WMYT0iVpuxBIO/nc7FNt/PmALDvio0PCrojokRKw4CDy14gMTf3X3fXKI0sdAoROz/K7kaodbxQxf8IUfB4BHaj81CNCvMehPfhlsPRNzk5Yb6nJwOqJQmmceMGd3Tho1OjggK56TPoLvdPePVpJZj3AovMDrrKl1WcsPjg8iSFgW3DbKvpbLlgOQ7SNvi1NTKWVrr+RmucDkHpPem2Uz7jzXJVeF40NN20lBc8Dur0q0PCx/HUdL3RuBYjgdJdG9cwhtYZlUdAOAopmSooRyNTT5Axma8pIqjkR9szKAXoTOlj1UQ5nEvSBZp10BJN2qLPdp8MDtXVLeFZZ/4uKOnyvdbjwbxQOBPjHF6Sde34hWiBAMLF977UqJMozphfD3guWuOUyb5EuGIDWxzuh8zdirNwUwrdnAs9TSmI7Lvpffc9VrzyW0xYHq2yOIMTkR5992H9KS/CfaE/oEIFinXO1rBWbKDG7PLbx5sRfclMQnLa5jEjmiDvl1mp5Id9FYOMWjgJx+LVXg1aiVJtXYtEW5jk2Y9H2123k05MT/yZGRMKli2fcy2XTxURaamZ/6sJ4g2vkg79jQEW5Si4syZu+W24xb/C+msjxUOSFQ="));
    private final static IconTemplate ICON_TEMPLATE_ERROR = new ConstantIconTemplate(ICON_ERROR);


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
    public CompletableFuture<Icon> createIcon(BufferedImage image) {
        if (image.getWidth() != 8 || image.getHeight() != 8) {
            throw new IllegalArgumentException("Image has the wrong size. Required 8x8 actual " + image.getWidth() + "x" + image.getHeight());
        }
        CompletableFuture<Icon> future = new CompletableFuture<>();
        asyncExecutor.execute(() -> {
            try {
                fetchIconFromImage(image, future);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Icon> createIconFromName(String name) {
        return fetchUuid(name).thenCompose(this::fetchIcon);
    }

    @Override
    public synchronized IconTemplate createIconTemplate(String s, Mark mark, ErrorHandler errorHandler) {
        if (s.contains("\\$\\{")) {
            // This error message is misleading. Actually placeholders in icon paths are supported. However they are
            // resolved before calling this method.
            errorHandler.addWarning("Icon definition contains placeholder. This is not supported yet.", mark);
        } else {
            IconTemplate entry = cache.getIfPresent(s);

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
                errorHandler.addWarning("Icon needs to be either\n1. A username,\n2. A UUID or\n3. A png image file.", mark);
            }

            if (entry != null) {
                cache.put(s, entry);
                return entry;
            }
        }

        cache.put(s, ICON_TEMPLATE_ERROR);
        return ICON_TEMPLATE_ERROR;
    }

    public Map<String, Icon> getIconCache() {
        try {
            return Files.find(iconFolder, Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.getFileName().toString().endsWith(".png"))
                    .collect(Collectors.toMap(
                            path -> iconFolder.relativize(path).toString(),
                            path -> {
                                BufferedImage head;
                                try {
                                    head = ImageIO.read(Files.newInputStream(path));
                                } catch (IOException e) {
                                    return new Icon(new ProfileProperty("error", "failed to read file", null));
                                }
                                if (head.getWidth() != 8 || head.getHeight() != 8) {
                                    return new Icon(new ProfileProperty("error", "wrong image dimensions", null));
                                }
                                byte[] headArray = getHeadArray(head);

                                IconImageData imageData = IconImageData.of(headArray);
                                Icon icon = iconCache.get(imageData);
                                if (icon == null) {
                                    return new Icon(new ProfileProperty("error", "not resolved yet", null));
                                }
                                return icon;
                            }
                    ));
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    private CompletableFuture<Icon> fetchIconFromImage(Path path) {
        CompletableFuture<Icon> future = new CompletableFuture<>();

        asyncExecutor.execute(() -> {
            try {
                BufferedImage image = ImageIO.read(Files.newInputStream(path));
                if (image.getWidth() != 8 || image.getHeight() != 8) {
                    logger.warning("Image " + path.toString() + " has the wrong size. Required 8x8 actual " + image.getWidth() + "x" + image.getHeight());
                    future.completeExceptionally(new Exception("wrong image size"));
                    return;
                }
                fetchIconFromImage(image, future);
            } catch (NoSuchFileException ex) {
                logger.log(Level.WARNING, "File does not exist: " + path);
                future.completeExceptionally(ex);
            } catch (Throwable ex) {
                logger.log(Level.WARNING, "Failed to load file " + path.toString() + ": " + ex.getMessage(), ex);
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    private void fetchIconFromImage(BufferedImage image, CompletableFuture<Icon> future) {
        byte[] headArray = getHeadArray(image);

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
                LinkedHashTreeMap<?, ?> map = gson.fromJson(reader, LinkedHashTreeMap.class);
                if (map.get("state").equals("ERROR")) {
                    future.completeExceptionally(new Exception("Server side error occurred. Try again later"));
                    // todo retry or fall back to a different service in this case
                } else if (map.get("state").equals("QUEUED")) {
                    asyncExecutor.schedule(() -> fetchIconFromImage(image, future), 5, TimeUnit.SECONDS);
                } else if (map.get("state").equals("SUCCESS")) {
                    Icon icon = new Icon(new ProfileProperty("textures", (String) map.get("skin"), (String) map.get("signature")));
                    iconCache.put(imageData, icon);

                    future.complete(icon);

                    synchronized (DefaultIconManager.this) {
                        // save to cache
                        Path cacheFile = iconFolder.resolve("cache.txt");
                        BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(cacheFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
                        writer.write(Base64.getEncoder().encodeToString(headArray));
                        writer.write(' ');
                        writer.write(icon.getTextureProperty().getValue());
                        writer.write(' ');
                        writer.write(icon.getTextureProperty().getSignature());
                        writer.newLine();
                        writer.close();
                    }
                } else {
                    future.completeExceptionally(new Exception("Server side error occurred. Unexpected response. Try again later"));
                }
            } catch (IOException ex) {
                logger.log(Level.WARNING, "An error occurred while trying to contact skinservice.codecrafter47.dyndns.eu", ex);
                // retry after 5 minutes
                // todo limit retries/ switch to a different service
                // todo exception handling during retries
                asyncExecutor.schedule(() -> {
                    try {
                        fetchIconFromImage(image, future);
                    } catch (Exception e) {
                        future.completeExceptionally(ex);
                    }
                }, 5, TimeUnit.MINUTES);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    private synchronized CompletableFuture<Icon> fetchIcon(UUID uuid) {
        CompletableFuture<Icon> future = cacheUUID.getIfPresent(uuid);

        if (future == null) {
            future = new CompletableFuture<>();
            CompletableFuture<Icon> finalFuture = future;
            asyncExecutor.execute(() -> fetchIconFromMojang(uuid, finalFuture));
            cacheUUID.put(uuid, future);
        }
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

        asyncExecutor.execute(() -> fetchUuidFromMojang(username, future));

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
                asyncExecutor.schedule(() -> fetchUuidFromMojang(username, future), headerField == null ? 300 : Integer.parseInt(headerField), TimeUnit.SECONDS);
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
    
    private byte[] getHeadArray(BufferedImage image){
        int[] rgb = image.getRGB(0, 0, 8, 8, null, 0, 8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(rgb.length * 4);
        byteBuffer.asIntBuffer().put(rgb);
        
        return byteBuffer.array();
    }

    private class IconEntry implements IconTemplate {
        private Supplier<IconView> factory;
        @Nullable
        private List<Runnable> listeners = new ArrayList<>();

        IconEntry(CompletableFuture<Icon> iconProvider) {
            factory = IconViewDelegate::new;
            iconProvider.exceptionally(th -> ICON_ERROR)
                    .thenAcceptAsync(icon -> {
                        IconViewConstant iconView = new IconViewConstant(icon);
                        factory = () -> iconView;
                        synchronized (IconEntry.this) {
                            for (Runnable listener : listeners) {
                                listener.run();
                            }
                            listeners = null;
                        }
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
                synchronized (IconEntry.this) {
                    if (listeners != null) {
                        listeners.add(this);
                    }
                }
                delegate.activate(getContext(), getListener());
            }

            @Override
            protected void onDeactivation() {
                synchronized (IconEntry.this) {
                    if (listeners != null) {
                        listeners.remove(this);
                    }
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
        private static final Icon[] ANIMATION_ELEMENTS = new Icon[]{
                new Icon(new ProfileProperty("textures", "eyJ0aW1lc3RhbXAiOjE1MzI3MDEyMDE3OTgsInByb2ZpbGVJZCI6ImIzYjE4MzQ1MzViZjRiNzU4ZTBjZGJmMGY4MjA2NTZlIiwicHJvZmlsZU5hbWUiOiIxMDExMTEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZiOWEwZWI4MDEwNjExMTU2MTI2OTgxOTNkY2Y1N2VmZjJmZWU1Y2NmZThkYmZjNGYxYzZlYjcyZWRlOWVjMTIifX19", "jpj9Nn3X+B3ck+v51RFi8he3Vd5M4liKmeh/xrvsAmoK4zMOkdtz92gLnbHq0j3Sym4PCn+IHahtWbD/PF7X1bP2R3Vx2OlI19jGUUG4U9lDQYTzo5tb3mE4Vlm4eLn2ji2HWTLTgKtMLlhXDHrStDmYRFxWtt3ut5NSCVFmHw4EDvfDlvm98m0C2WcfbVY1rgZrmMBX8BJgBr95HaImrMHCXkeEA4WzuYVfeYxGK0kCxuz4HM0GwIf7ZZgDIraejpVvwpmtLn9hOogJvVzw5LvYkYrPUzcLPnZ2buskuS6HXjXf4aay/1NBX1rgjYVh3fOs/t9G5O/1Xit7MiKXekhkg0z3zq5nUsr9xnGi0MJX8ahAnB8pZMAjJ8Cir87mL7wWWk3qh2GXLiCSgBgy1DTe++WaQ2kQ0iHgiQqrwD13P2t2AM9QAQMoauyDKRcw1WTxUF2v+fLu6LMRaA3v4cXU2BuVor4/VQSXJoYrh/Tf1Can1WuvJcIrVGnvKCNtVzrDFxahr/o5GW6/bxlMQQBQnOx/Uj5litOvU3GpZaQOq6nPE5ICYkdHmQnuF3yqsfU632I9KoUge9uiuu1Gu5P9Nt1cu3OubQeN0OVrCCos7kAhRoj51IrKcTWr7FbbfnAd8RJyIkNg77zkcEq2hbrpyxrSqbrFo3fIl8k+NSY=")),
                new Icon(new ProfileProperty("textures", "eyJ0aW1lc3RhbXAiOjE1MzI3MDEzMjMwNzksInByb2ZpbGVJZCI6ImIzYjE4MzQ1MzViZjRiNzU4ZTBjZGJmMGY4MjA2NTZlIiwicHJvZmlsZU5hbWUiOiIxMDExMTEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RmNjkxMDJiZjA5ZTFlNDEwMDEyZjQ4MmQ5NGVlNjk0MTEzZGU5NzhlYmI5ZDFiNDk1MmFjOGRhMTUwMDQzYmEifX19", "mm6LhFVCHUIPWxT49poJ0gX6Sv9kN5AjaU24dHg+eMOBV4xRrrPwnvlBF09uGpEjHYMlyP8jZblo3j2mlyo4N3KrwmD523XQytVLgWLl5zpgbsqckyx/cKvIbx/4Rg+NfyLktMeWISVfWgQL08lJKAjCEA3lQJmwQZtnaPgtch6tHRylK0XVHnZM4k6VVpqd2JcxJcU03cl4a9CFMu65gOFu3BHUD8M3Q8upbSDWvYx3xiUqytJbGXl8cVDbrkhhhcDlBE+SRBbHebA9tU8TzvyYekD3IGJpDgmpoafjgX8BgXi7w4KOIQDN4g5pZNAq1HHEdutz0rL4i6WAUj2r1WjBj1PAcVSIiMskAnUmABfCCNp35r0bYBQUB9er5PozLSC7M2ENby7RqWaHf/vZYfGwhJqNdxNkjIudRMoUvUlAq1Ho/43lwz6q21tiZ0ABrZ6zTYJZGtYT8sPBzMjq/zur53VrkrPOo7Rh0BqolgOEL/qoFbKOMwnaG61PUCuktmjKJ9aHd4APzcvI860WZOTz9nHMohq+QiU8ZRLqY+7rZKK4fT/Bp3uK6HXRDD3Uo1zEZ5h5cjH7kL7DO2BJpMmrLouAHEeCN/7tjn6fIXt2p2Tnlr17oR2bRr1QgaEY9ef3i+z3GzWjfXWAkhfTw8idPS/ODhjA0Pip3uDRPtg=")),
                new Icon(new ProfileProperty("textures", "eyJ0aW1lc3RhbXAiOjE1MzI3MDExNDExNjQsInByb2ZpbGVJZCI6ImIzYjE4MzQ1MzViZjRiNzU4ZTBjZGJmMGY4MjA2NTZlIiwicHJvZmlsZU5hbWUiOiIxMDExMTEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNlZjliZjU0NmU5YzhiYTFkNDAxZjU1NzA4NTRkNzRhYjViYzU3MWNjYjhiMzI4Mjc2MWE0ODZjMzdmYmU2ZjMifX19", "UsQFdQrtzXx3TW8uiTx2E0n7EieUPw7R55XWb5Sf+jGeAYlag9SohPGoEUt3kMFjJxanf4XN77B587vTW1GvV37tVFhF2TyBnXj+7ZKv1ly9QRV+nsq+w3MmtWfASgLAt+v4KH9R/glt/eeVa6tH9a0RSNrdWFPDggQUaM9dz2kjCuhdBFocB4jfoX9p7rJSqgRijiayLWET4PQ+KxQZ87rz0McJrZkOOoFoCvcQjZINO/YJam1YpRTgMR3l67MICn6azRRRIqlId87D0KAOltDrN15A2z1eUGktzUXxn198AhZz4l2oYXeq5a2wSx7BWPFqM/g7Q5fdV8yqAteFcFC0YUZfrO4S3uPZF2vlA1zAI3C3J5A1/SjuzNbIRLaS6h/e5sXZQNotFhY7nPPE8fN3neMFLgk+BMs3oEDozXI7y4wfep1Ss39wYF3EgIBlcaKZmc0CFyNrgD7b9q4DOYme6KagPlCas2ULU98tWmWad1mdBeoSgRXrXGdJfzlfRCnlk6aPkiOXbKfiFvxbWWAh6Nyc74z1Sm+/X68JzHs1i9Ni28rM/2hduDvTLl+bZW6Ooanuzr4IG9fh0/Y/1qd9UY0apYW+vRFTkNUMvyfvM30TO5OhDFrNHJJ5aZwOUE4Urz9dAQyzNR6DxZNR/vAAvxrzetz3poQOedM3HcU=")),
                new Icon(new ProfileProperty("textures", "eyJ0aW1lc3RhbXAiOjE1MzI3MDEyNjI0MzQsInByb2ZpbGVJZCI6ImIzYjE4MzQ1MzViZjRiNzU4ZTBjZGJmMGY4MjA2NTZlIiwicHJvZmlsZU5hbWUiOiIxMDExMTEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFlYThiMWRlNTIzOGJlOGNkZjljOTYwZWMzYzNlYThmMjU4MDE4NDgwMzlhYmE1ZTJmZGUzMWE1NjdjMGFlMTcifX19", "kUaH7sUPqaQ5UUjHG0v49JU1uRcptX3Lk+BGeapM9bOrGTQzWr1ZZVG+gKH+FaXN5Q6R0ZTE3hCSW15LLjPe1499Ku4UnFtEm1F5/pQREjPiaPLNosqKBdw4K2PnkzflrkU0hHShTEruSTiDS85sa8+70wcX3y30QDvPhENwd11kVr1/sUavhzTHGQUe/AgkpTQxtOe84UeDXRG8O8QES4VUZxgNMv0A9AoXie6HaxPw0aspQkO/shUJUHja92tThm/QpqH97FhNrSfeD7xeJEcts0Nb71MAtCTXrNNycb0Ujdu2nrYCHZhAWj8BBI31DMYzOzcmjtp45CyN6TgbQUCgQcZysRbepD8Bv8NTFAVTphw40wYMqF6N7i0+kSqlYHV9M3NraNIXTOBQl+qWnRSmU0oUw5xxK+vbsJvZB8aAHe3eamdwaVIIyfupdXy1WYH1Yi5VFhF8ISdYo40+tTErBdYQKEgvd1N99VhuMerEAdyu2foKilN6B5Z4a0fNxyLwTxTDCjLR+u9kF7JLhMrB78RMTlWpnXYbcx9mgn+OBkdAHKYXZRj75UjuvlsRTjPf29KpJwgowW2/ZrI/OSE7sFU5TJVGtVgyetSKbs4AqaYWaWUbM9ZutithmR00Oan2oY0R6VdDUEMqF1NzCEZ6tJdIeKNnxrUt3MwnhFE="))
        };
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
