package de.codecrafter47.taboverlay.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.dsl.AbstractTabOverlayTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.config.dsl.DynamicSizeTabOverlayTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.RectangularTabOverlayTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.AnimatedComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.ConditionalComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.ContainerComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.PlayersComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.SpacerComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.components.TableComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.yaml.ComponentConfigurationInheritanceHandler;
import de.codecrafter47.taboverlay.config.dsl.yaml.CustomYamlConstructor;
import de.codecrafter47.taboverlay.config.dsl.yaml.InheritanceHandler;
import de.codecrafter47.taboverlay.config.dsl.yaml.TagInheritanceHandler;
import de.codecrafter47.taboverlay.config.dsl.yaml.TypeFieldInheritanceHandler;
import de.codecrafter47.taboverlay.config.expression.DefaultExpressionEngine;
import de.codecrafter47.taboverlay.config.expression.ExpressionEngine;
import de.codecrafter47.taboverlay.config.icon.IconManager;
import de.codecrafter47.taboverlay.config.placeholder.*;
import de.codecrafter47.taboverlay.config.platform.EventListener;
import de.codecrafter47.taboverlay.config.platform.Platform;
import de.codecrafter47.taboverlay.config.player.GlobalPlayerSetFactory;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerProvider;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import lombok.val;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigTabOverlayManager {
    private final Platform platform;
    private final PlayerProvider playerProvider;
    private final AbstractPlayerPlaceholderResolver playerPlaceholderResolver;
    private final Yaml yaml;
    private final Logger logger;
    private final ScheduledExecutorService tabEventQueue;
    @Getter
    private final ExpressionEngine expressionEngine;
    private final IconManager iconManager;
    private final GlobalPlayerSetFactory globalPlayerSetFactory;

    private final List<AbstractTabOverlayTemplateConfiguration<?>> configurations = new ArrayList<>();
    private final List<AbstractTabOverlayTemplate> templates = new ArrayList<>();

    private final DataKey<Icon> playerIconDataKey;
    private final DataKey<Integer> playerPingDataKey;

    private final Map<TabView, Player> tabViews = new HashMap<>();

    public ConfigTabOverlayManager(Platform platform, PlayerProvider playerProvider, AbstractPlayerPlaceholderResolver playerPlaceholderResolver, Options options, Logger logger, ScheduledExecutorService tabEventQueue, IconManager iconManager) {
        this.platform = platform;
        this.playerProvider = playerProvider;
        this.playerPlaceholderResolver = playerPlaceholderResolver;
        this.yaml = constructYamlInstance(options);
        this.logger = logger;
        this.expressionEngine = constructExpressionEngine(options);
        this.tabEventQueue = tabEventQueue;
        this.iconManager = iconManager;
        this.globalPlayerSetFactory = new GlobalPlayerSetFactory(playerProvider, tabEventQueue, logger, options.playerInvisibleDataKey, options.playerCanSeeInvisibleDataKey);
        this.playerIconDataKey = options.playerIconDataKey;
        this.playerPingDataKey = options.playerPingDataKey;

        this.platform.addEventListener(new Listener());
    }

    private static ExpressionEngine constructExpressionEngine(Options options) {
        val expressionEngineOptions = DefaultExpressionEngine.Options.builder()
                .withDefaultValueReaders()
                .withDefaultTokenReaders()
                .withDefaultOperators()
                .build();
        return new DefaultExpressionEngine(expressionEngineOptions);
    }

    static Yaml constructYamlInstance(Options options) {
        val inheritanceHandlerMap = ImmutableMap.<Class<?>, InheritanceHandler>builder();

        // different types of tab overlays
        val tabOverlayTypeMap = ImmutableMap.<String, Class<?>>builder();
        for (TabOverlayTypeSpec tabOverlayTypeSpec : options.getTabOverlayTypes()) {
            tabOverlayTypeMap.put(tabOverlayTypeSpec.getId(), tabOverlayTypeSpec.getConfigurationClass());
        }
        inheritanceHandlerMap.put(AbstractTabOverlayTemplateConfiguration.class, new TypeFieldInheritanceHandler("type", tabOverlayTypeMap.build(), null));

        // components
        val componentMap = ImmutableMap.<String, Class<?>>builder();
        for (ComponentSpec component : options.getComponents()) {
            componentMap.put(component.getTag(), component.getConfigurationClass());
        }
        inheritanceHandlerMap.put(ComponentConfiguration.class, new ComponentConfigurationInheritanceHandler(componentMap.build()));

        // custom placeholders
        val customPlaceholderMap = ImmutableMap.<String, Class<?>>builder()
                .put("!conditional", CustomPlaceholderConfiguration.Conditional.class)
                .put("!switch", CustomPlaceholderConfiguration.Switch.class)
                .build();
        inheritanceHandlerMap.put(CustomPlaceholderConfiguration.class, new TagInheritanceHandler(customPlaceholderMap, null));

        val constructor = new CustomYamlConstructor(inheritanceHandlerMap.build());
        return new Yaml(constructor);
    }

    private void loadConfig(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            ErrorHandler.set(new ErrorHandler());
            AbstractTabOverlayTemplateConfiguration<?> configuration;
            try {
                configuration = (AbstractTabOverlayTemplateConfiguration<?>) yaml.loadAs(reader, AbstractTabOverlayTemplateConfiguration.class);
            } catch (Throwable th) {
                if (ErrorHandler.get().hasErrors()) {
                    configuration = new RectangularTabOverlayTemplateConfiguration();
                } else {
                    throw th;
                }
            }
            configuration.setPath(path);
            configuration.setErrorHandler(ErrorHandler.get());
            ErrorHandler.set(null);
            configurations.add(configuration);
        } catch (Throwable th) {
            logger.log(Level.WARNING, "Failed to load tab list configuration file " + path + ".\n" + th.toString());
        }
    }

    private boolean load(AbstractTabOverlayTemplateConfiguration<?> configuration) {
        ErrorHandler errorHandler = configuration.getErrorHandler();
        AbstractTabOverlayTemplate template = null;
        try {
            TemplateCreationContext tcc = new TemplateCreationContext(expressionEngine, iconManager, playerIconDataKey, playerPingDataKey, errorHandler);
            tcc.setPlayerPlaceholderResolver(playerPlaceholderResolver);
            tcc.setCustomPlaceholders(new HashMap<>()); // TODO _copy_ of global custom placeholder map
            tcc.setPlayerSets(new HashMap<>()); // todo maybe set to null and throw on get?
            tcc.setDefaultIcon(IconTemplate.STEVE);
            tcc.setDefaultPing(PingTemplate.ZERO);
            tcc.setDefaultText(TextTemplate.EMPTY);
            tcc.setViewerAvailable(true);
            PlaceholderResolverChain placeholderResolverChain = new PlaceholderResolverChain();
            placeholderResolverChain.addResolver(new PlayerPlaceholderResolver(playerPlaceholderResolver, PlayerPlaceholder.BindPoint.VIEWER));
            placeholderResolverChain.addResolver(new PlayerPlaceholderResolver(playerPlaceholderResolver, PlayerPlaceholder.BindPoint.PLAYER));
            placeholderResolverChain.addResolver(new TimePlaceholderResolver());
            placeholderResolverChain.addResolver(new PlayerSetPlaceholderResolver());
            tcc.setPlaceholderResolverChain(placeholderResolverChain);

            template = configuration.toTemplate(tcc);
            template.setPath(configuration.getPath());
        } catch (MarkedYAMLException e) {
            errorHandler.addError("Unexpected exception. " + e.getMessage() + e.getProblem(), e.getProblemMark());
        } catch (Throwable th) {
            StringWriter writer = new StringWriter();
            th.printStackTrace(new PrintWriter(writer));
            errorHandler.addError("Unexpected exception. " + writer.toString(), null);
        }
        boolean success = false;
        if (template != null && !errorHandler.hasErrors()) {
            templates.add(template);
            // TODO notify our tab overlay providers of change in available templates. - can be done by caller!
            success = true;
        }
        List<ErrorHandler.Entry> errors = errorHandler.getEntries();
        if (errors.size() > 0) {
            StringBuilder message = new StringBuilder();
            int errCnt = 0;
            int warnCnt = 0;
            for (ErrorHandler.Entry error : errors) {
                message.append("\n");
                if (error.getSeverity() == ErrorHandler.Severity.WARNING) {
                    message.append("WARNING: ").append(error.getMessage());
                    warnCnt += 1;
                } else if (error.getSeverity() == ErrorHandler.Severity.ERROR) {
                    message.append("ERROR: ").append(error.getMessage());
                    errCnt += 1;
                } else {
                    throw new AssertionError("Unknown error severity");
                }
                Mark position = error.getPosition();
                if (position != null) {
                    message.append("\n").append(position.toString());
                }
            }
            if (success) {
                logger.log(Level.WARNING, "There have been " + warnCnt + " warnings while loading " + configuration.getPath() + message + "\n");
            } else {

                logger.log(Level.WARNING, "Failed to load tab list configuration file " + configuration.getPath() + ".\n" + errCnt + " errors and " + warnCnt + " warnings" + message + "\n");
            }
        }
        return success;
    }

    private void loadConfigs(Path path) {
        // TODO think about synchronization here
        if (Files.isDirectory(path)) {
            try {
                Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".yml") && attr.isRegularFile()).forEach(this::loadConfig);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to search directory " + path + " for tab list configuration files.", e);
            }
        }
    }

    public void reloadConfigs(Iterable<Path> paths) {
        // TODO think about synchronization here

        // clean up old ones
        templates.clear();
        configurations.clear();

        for (Path path : paths) {
            loadConfigs(path);
        }

        // refresh
        refreshConfigs();
    }

    public void refreshConfigs() {
        // TODO think about synchronization here

        // load all configuration files
        val iterator = configurations.iterator();
        while (iterator.hasNext()) {
            val configuration = iterator.next();
            if (!load(configuration)) {
                iterator.remove();
            }
        }


        // update tab views
        for (val entry : tabViews.entrySet()) {
            val tabView = entry.getKey();
            val viewer = entry.getValue();
            // remove old ones
            tabView.getTabOverlayProviders().removeProviders(ConfigTabOverlayProvider.class);
            // add new ones
            for (AbstractTabOverlayTemplate template : templates) {
                tabView.getTabOverlayProviders().addProvider(new ConfigTabOverlayProvider(tabView, template, viewer, tabEventQueue, playerProvider, globalPlayerSetFactory));
            }
        }
    }

    public List<AbstractTabOverlayTemplate> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    private class Listener implements EventListener {

        @Override
        public void onTabViewAdded(TabView tabView, Player viewer) {
            // todo concurrency????
            tabViews.put(tabView, viewer);
            for (AbstractTabOverlayTemplate template : templates) {
                tabView.getTabOverlayProviders().addProvider(new ConfigTabOverlayProvider(tabView, template, viewer, tabEventQueue, playerProvider, globalPlayerSetFactory));
            }
        }

        @Override
        public void onTabViewRemoved(TabView tabView) {
            tabViews.remove(tabView);
            tabView.getTabOverlayProviders().removeProviders(ConfigTabOverlayProvider.class);
        }
    }

    @Builder
    @Value
    public static class Options {
        @Singular
        ImmutableList<TabOverlayTypeSpec> tabOverlayTypes;

        @Singular
        ImmutableList<ComponentSpec> components;

        public static OptionsBuilder createBuilderWithDefaults() {
            return builder()
                    .tabOverlayType(new TabOverlayTypeSpec("FIXED_SIZE", RectangularTabOverlayTemplateConfiguration.class)) // todo deprecate the first two ???
                    .tabOverlayType(new TabOverlayTypeSpec("DYNAMIC_SIZE_FIXED_COLUMNS", RectangularTabOverlayTemplateConfiguration.class))
                    .tabOverlayType(new TabOverlayTypeSpec("RECTANGULAR", RectangularTabOverlayTemplateConfiguration.class))
                    .tabOverlayType(new TabOverlayTypeSpec("DYNAMIC_SIZE", DynamicSizeTabOverlayTemplateConfiguration.class))
                    .component(new ComponentSpec("!animated", AnimatedComponentConfiguration.class))
                    .component(new ComponentSpec("!conditional", ConditionalComponentConfiguration.class))
                    .component(new ComponentSpec("!container", ContainerComponentConfiguration.class))
                    .component(new ComponentSpec("!players", PlayersComponentConfiguration.class))
                    .component(new ComponentSpec("!spacer", SpacerComponentConfiguration.class))
                    .component(new ComponentSpec("!table", TableComponentConfiguration.class));
        }

        DataKey<Icon> playerIconDataKey;

        DataKey<Integer> playerPingDataKey;

        DataKey<Boolean> playerInvisibleDataKey;
        DataKey<Boolean> playerCanSeeInvisibleDataKey;
    }
}
