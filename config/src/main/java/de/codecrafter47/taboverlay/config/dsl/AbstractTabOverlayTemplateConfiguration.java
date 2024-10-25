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

package de.codecrafter47.taboverlay.config.dsl;

import com.google.common.collect.ImmutableList;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.dsl.components.BasicComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.customplaceholder.CustomPlaceholderConfiguration;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedFloatProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderResolver;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractTabOverlayTemplateConfiguration<T extends AbstractTabOverlayTemplate> extends MarkedPropertyBase {

    private transient Path path;

    private transient ErrorHandler errorHandler;

    private MarkedStringProperty showTo = new MarkedStringProperty("all");

    private MarkedIntegerProperty priority = new MarkedIntegerProperty(0);

    private boolean showHeaderFooter = false;

    private TextTemplateConfigurationList<TextTemplateConfiguration> header;

    private MarkedFloatProperty headerAnimationUpdateInterval = new MarkedFloatProperty(1.0f);

    private TextTemplateConfigurationList<TextTemplateConfiguration> footer;

    private MarkedFloatProperty footerAnimationUpdateInterval = new MarkedFloatProperty(1.0f);

    private PlayerSetConfiguration.Visibility hiddenPlayers = PlayerSetConfiguration.Visibility.VISIBLE_TO_ADMINS;

    private Map<MarkedStringProperty, CustomPlaceholderConfiguration> customPlaceholders = new HashMap<>();

    private Map<MarkedStringProperty, PlayerSetConfiguration> playerSets = new HashMap<>();

    private BasicComponentConfiguration.LongTextBehaviour longText = BasicComponentConfiguration.LongTextBehaviour.DISPLAY_ALL;
    
    private String customLongText = "";

    public T toTemplate(TemplateCreationContext tcc) {
        T template = createTemplate();
        populateTemplate(template, tcc);
        return template;
    }

    protected abstract T createTemplate();

    protected void populateTemplate(T template, TemplateCreationContext tcc) {

        if (ConfigValidationUtil.checkNotNull(tcc, "tab overlay", "hiddenPlayers", hiddenPlayers, null)) {
            tcc.setDefaultHiddenPlayerVisibility(hiddenPlayers);
        }

        // playerSets
        Map<String, PlayerSetTemplate> playerSetTemplates = new HashMap<>();
        if (this.playerSets != null) {
            for (val entry : this.playerSets.entrySet()) {
                if (entry.getKey() != null && entry.getKey().getValue() != null && entry.getValue() != null) {
                    playerSetTemplates.put(entry.getKey().getValue(), entry.getValue().toTemplate(tcc));
                } else {
                    if (entry.getKey() == null) {
                        tcc.getErrorHandler().addWarning("Player set with missing name", entry.getValue() != null ? entry.getValue().getStartMark() : null);
                    } else if (entry.getKey().getValue() == null) {
                        tcc.getErrorHandler().addWarning("Player set with missing name", entry.getKey().getStartMark());
                    } else if (entry.getValue() == null) {
                        tcc.getErrorHandler().addWarning("Incomplete player set definition", entry.getKey().getStartMark());
                    }
                }
            }
        }
        template.setPlayerSets(playerSetTemplates);
        tcc.setPlayerSets(playerSetTemplates);

        // custom placeholders
        if (customPlaceholders != null) {

            for (val entry : customPlaceholders.entrySet()) {
                if (entry.getKey() != null && entry.getKey().getValue() != null && entry.getValue() != null) {
                    tcc.getCustomPlaceholders().put(entry.getKey().getValue(), entry.getValue());
                } else {
                    if (entry.getKey() == null) {
                        tcc.getErrorHandler().addWarning("Custom placeholder definition with missing name", entry.getValue() != null ? entry.getValue().getStartMark() : null);
                    } else if (entry.getKey().getValue() == null) {
                        tcc.getErrorHandler().addWarning("Custom placeholder definition with missing name", entry.getKey().getStartMark());
                    } else if (entry.getValue() == null) {
                        tcc.getErrorHandler().addWarning("Incomplete custom placeholder definition", entry.getKey().getStartMark());
                    }
                }
            }

            tcc.addPlaceholderResolver(new CustomPlaceholderResolver(tcc.getCustomPlaceholders()));
        }

        // showTo
        if (ConfigValidationUtil.checkNotNull(tcc, "tab overlay", "showTo", showTo, null)) {
            try {
                template.setViewerPredicate(tcc.getExpressionEngine().compile(tcc, showTo.getValue(), showTo.getStartMark()));
            } catch (Throwable th) {
                tcc.getErrorHandler().addError("Invalid Expression. " + th.toString(), showTo.getStartMark());
            }
        }

        // priority
        if (ConfigValidationUtil.checkNotNull(tcc, "tab overlay", "priority", priority, null)) {
            if (priority.getValue() > 10000) {
                tcc.getErrorHandler().addError("Priority must not be larger than 10000.", priority.getStartMark());
            }
            if (priority.getValue() < -10000) {
                tcc.getErrorHandler().addError("Priority must not be smaller than -10000.", priority.getStartMark());
            }
            template.setPriority(priority.getValue());
        }

        // header & footer
        if (showHeaderFooter) {
            if (header == null || header.isEmpty()) {
                template.setHeader(Collections.singletonList(TextTemplate.EMPTY));
            } else if (header.size() == 1) {
                TextTemplateConfiguration t;
                template.setHeader(Collections.singletonList(null != (t = header.get(0)) ? t.toTemplate(tcc) : TextTemplate.EMPTY));
            } else {
                val headerTemplates = ImmutableList.<TextTemplate>builder();

                for (val textConfiguration : header) {
                    if (textConfiguration != null) {
                        headerTemplates.add(textConfiguration.toTemplate(tcc));
                    } else {
                        headerTemplates.add(TextTemplate.EMPTY);
                    }
                }

                template.setHeader(headerTemplates.build());

                if (ConfigValidationUtil.checkNotNull(tcc, "tab overlay", "headerAnimationUpdateInterval", headerAnimationUpdateInterval, null)
                        && ConfigValidationUtil.checkRange(tcc, "tab overlay", "headerAnimationUpdateInterval", headerAnimationUpdateInterval.getValue(), 0.01f, 9999.0f, headerAnimationUpdateInterval.getStartMark())) {
                    template.setHeaderAnimationUpdateInterval(headerAnimationUpdateInterval.getValue());
                }
            }

            if (footer == null || footer.isEmpty()) {
                template.setFooter(Collections.singletonList(TextTemplate.EMPTY));
            } else if (footer.size() == 1) {
                TextTemplateConfiguration t;
                template.setFooter(Collections.singletonList(null != (t = footer.get(0)) ? t.toTemplate(tcc) : TextTemplate.EMPTY));
            } else {
                val footerTemplates = ImmutableList.<TextTemplate>builder();

                for (val textConfiguration : footer) {
                    if (textConfiguration != null) {
                        footerTemplates.add(textConfiguration.toTemplate(tcc));
                    } else {
                        footerTemplates.add(TextTemplate.EMPTY);
                    }
                }

                template.setFooter(footerTemplates.build());

                if (ConfigValidationUtil.checkNotNull(tcc, "tab overlay", "footerAnimationUpdateInterval", footerAnimationUpdateInterval, null)
                        && ConfigValidationUtil.checkRange(tcc, "tab overlay", "footerAnimationUpdateInterval", footerAnimationUpdateInterval.getValue(), 0.01f, 9999.0f, footerAnimationUpdateInterval.getStartMark())) {
                    template.setFooterAnimationUpdateInterval(footerAnimationUpdateInterval.getValue());
                }
            }
        }

        tcc.setDefaultLongTextBehaviour(longText);
        tcc.setCustomLongText(customLongText);

    }
}
