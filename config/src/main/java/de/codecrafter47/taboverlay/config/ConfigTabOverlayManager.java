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

package de.codecrafter47.taboverlay.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.TabOverlayProvider;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.*;
import de.codecrafter47.taboverlay.config.dsl.components.*;
import de.codecrafter47.taboverlay.config.dsl.customplaceholder.*;
import de.codecrafter47.taboverlay.config.dsl.yaml.*;
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
import lombok.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigTabOverlayManager {
    private final PlayerProvider playerProvider;
    private final AbstractPlayerPlaceholderResolver playerPlaceholderResolver;
    private final Collection<PlaceholderResolver<Context>> additionalGlobalPlaceholderResolvers;
    private final Yaml yaml;
    private final Logger logger;
    private final ScheduledExecutorService tabEventQueue;
    @Getter
    private final ExpressionEngine expressionEngine;
    private final IconManager iconManager;
    @Nullable
    @Setter
    @Getter
    private TimeZone timeZone = null;
    private final GlobalPlayerSetFactory globalPlayerSetFactory;

    private final List<AbstractTabOverlayTemplateConfiguration<?>> configurations = new ArrayList<>();
    private final List<AbstractTabOverlayTemplate> templates = new ArrayList<>();

    private final DataKey<Icon> playerIconDataKey;
    private final DataKey<Integer> playerPingDataKey;

    private final SortingRulePreprocessor sortingRulePreprocessor;

    private final Map<TabView, Player> tabViews = new HashMap<>();
    @NonNull
    @Nonnull
    @Setter
    @Getter
    private Map<String, CustomPlaceholderConfiguration> globalCustomPlaceholders = new HashMap<>();

    public ConfigTabOverlayManager(Platform platform, PlayerProvider playerProvider, AbstractPlayerPlaceholderResolver playerPlaceholderResolver, Collection<PlaceholderResolver<Context>> additionalGlobalPlaceholderResolvers, Yaml yaml, Options options, Logger logger, ScheduledExecutorService tabEventQueue, IconManager iconManager) {
        this.playerProvider = playerProvider;
        this.playerPlaceholderResolver = playerPlaceholderResolver;
        this.additionalGlobalPlaceholderResolvers = additionalGlobalPlaceholderResolvers;
        this.yaml = yaml;
        this.logger = logger;
        this.expressionEngine = constructExpressionEngine(options);
        this.tabEventQueue = tabEventQueue;
        this.iconManager = iconManager;
        this.globalPlayerSetFactory = new GlobalPlayerSetFactory(playerProvider, tabEventQueue, logger, options.playerInvisibleDataKey, options.playerCanSeeInvisibleDataKey);
        this.playerIconDataKey = options.playerIconDataKey;
        this.playerPingDataKey = options.playerPingDataKey;
        this.sortingRulePreprocessor = options.sortingRulePreprocessor;

        platform.addEventListener(new Listener());
    }

    private static ExpressionEngine constructExpressionEngine(Options options) {
        val expressionEngineOptions = DefaultExpressionEngine.Options.builder()
                .withDefaultValueReaders()
                .withDefaultTokenReaders()
                .withDefaultOperators()
                .build();
        return new DefaultExpressionEngine(expressionEngineOptions);
    }

    public static Yaml constructYamlInstance(Options options) {
        val inheritanceHandlerMap = ImmutableMap.<Class<?>, InheritanceHandler>builder();
        val representer = new CustomRepresenter();

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
            representer.addClassTag(component.getConfigurationClass(), new Tag(component.getTag()));
        }
        inheritanceHandlerMap.put(ComponentConfiguration.class, new ComponentConfigurationInheritanceHandler(componentMap.build()));

        // custom placeholders
        val customPlaceholderMap = ImmutableMap.<String, Class<?>>builder()
                .put("!conditional", CustomPlaceholderConditionalConfiguration.class)
                .put("!switch", CustomPlaceholderSwitchConfiguration.class)
                .put("!select", CustomPlaceholderSelectConfiguration.class)
                .put("!compute", CustomPlaceholderComputeConfiguration.class)
                .put("!animated", CustomPlaceholderAnimatedConfiguration.class)
                .put("!color_animation", CustomPlaceholderColorAnimationConfiguration.class)
                .put("!progress_bar", CustomPlaceholderProgressBarConfiguration.class)
                .build();
        inheritanceHandlerMap.put(CustomPlaceholderConfiguration.class, new TagInheritanceHandler(customPlaceholderMap, CustomPlaceholderAliasConfiguration.class));
        representer.addClassTag(CustomPlaceholderConditionalConfiguration.class, new Tag("!conditional"));
        representer.addClassTag(CustomPlaceholderSwitchConfiguration.class, new Tag("!switch"));
        representer.addClassTag(CustomPlaceholderSelectConfiguration.class, new Tag("!select"));
        representer.addClassTag(CustomPlaceholderComputeConfiguration.class, new Tag("!compute"));
        representer.addClassTag(CustomPlaceholderAnimatedConfiguration.class, new Tag("!animated"));
        representer.addClassTag(CustomPlaceholderColorAnimationConfiguration.class, new Tag("!color_animation"));
        representer.addClassTag(CustomPlaceholderProgressBarConfiguration.class, new Tag("!progress_bar"));

        val constructor = new CustomYamlConstructor(inheritanceHandlerMap.build());
        constructor.setPropertyUtils(new CustomPropertyUtils());
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(constructor, representer, dumperOptions);
        constructor.setAllowDuplicateKeys(false);
        return yaml;
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
        ErrorHandler errorHandler = configuration.getErrorHandler().copy();
        AbstractTabOverlayTemplate template = null;
        try {
            TemplateCreationContext tcc = new TemplateCreationContext(expressionEngine, iconManager, playerIconDataKey, playerPingDataKey, errorHandler, sortingRulePreprocessor);
            tcc.setPlayerPlaceholderResolver(playerPlaceholderResolver);
            tcc.setCustomPlaceholders(new HashMap<>(globalCustomPlaceholders));
            tcc.setPlayerSets(new HashMap<>());
            tcc.setDefaultIcon(IconTemplate.STEVE);
            tcc.setDefaultPing(PingTemplate.ZERO);
            tcc.setDefaultText(TextTemplate.EMPTY);
            tcc.setViewerAvailable(true);
            PlaceholderResolverChain placeholderResolverChain = new PlaceholderResolverChain();
            placeholderResolverChain.addResolver(new PlayerPlaceholderResolver(playerPlaceholderResolver, PlayerPlaceholderResolver.BindPoint.VIEWER));
            placeholderResolverChain.addResolver(new PlayerPlaceholderResolver(playerPlaceholderResolver, PlayerPlaceholderResolver.BindPoint.PLAYER));
            placeholderResolverChain.addResolver(new TimePlaceholderResolver(this));
            placeholderResolverChain.addResolver(new TextTransformPlaceholderResolver());
            placeholderResolverChain.addResolver(new PlayerSetPlaceholderResolver());
            for (PlaceholderResolver<Context> placeholderResolver : additionalGlobalPlaceholderResolvers) {
                placeholderResolverChain.addResolver(placeholderResolver);
            }
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
            success = true;
        }

        if (errorHandler.getEntries().size() > 0) {
            String fileName = configuration.getPath().toString();
            String msg = errorHandler.formatErrors(fileName);
            logger.log(Level.WARNING, msg);
        }
        return success;
    }

    private synchronized void loadConfigs(Path path) {
        if (Files.isDirectory(path)) {
            try {
                Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".yml") && attr.isRegularFile()).forEach(this::loadConfig);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to search directory " + path + " for tab list configuration files.", e);
            }
        }
    }

    public synchronized void reloadConfigs(Iterable<Path> paths) {
        // clean up old ones
        configurations.clear();

        for (Path path : paths) {
            loadConfigs(path);
        }

        // refresh
        refreshConfigs();
    }

    public synchronized void refreshConfigs() {
        templates.clear();

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
            List<TabOverlayProvider> providers = new ArrayList<>();
            for (AbstractTabOverlayTemplate template : templates) {
                providers.add(new ConfigTabOverlayProvider(tabView, template, viewer, tabEventQueue, playerProvider, globalPlayerSetFactory, logger));
            }
            tabView.getTabOverlayProviders().addProviders(providers);
        }
    }

    public List<AbstractTabOverlayTemplate> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    private class Listener implements EventListener {

        @Override
        public void onTabViewAdded(TabView tabView, Player viewer) {
            synchronized (ConfigTabOverlayManager.this) {
                tabViews.put(tabView, viewer);
                List<TabOverlayProvider> providers = new ArrayList<>();
                for (AbstractTabOverlayTemplate template : templates) {
                    providers.add(new ConfigTabOverlayProvider(tabView, template, viewer, tabEventQueue, playerProvider, globalPlayerSetFactory, logger));
                }
                tabView.getTabOverlayProviders().addProviders(providers);
            }
        }

        @Override
        public void onTabViewRemoved(TabView tabView) {
            synchronized (ConfigTabOverlayManager.this) {
                tabViews.remove(tabView);
                tabView.getTabOverlayProviders().removeProviders(ConfigTabOverlayProvider.class);
            }
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
                    .tabOverlayType(new TabOverlayTypeSpec("HEADER_FOOTER", HeaderFooterOnlyTabOverlayTemplateConfiguration.class))
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

        @Nullable
        SortingRulePreprocessor sortingRulePreprocessor;
    }
}
