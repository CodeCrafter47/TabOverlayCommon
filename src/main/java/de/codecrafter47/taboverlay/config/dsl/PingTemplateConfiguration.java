package de.codecrafter47.taboverlay.config.dsl;

import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.exception.MarkedConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.ping.ConstantPingTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PlayerPingTemplate;

public class PingTemplateConfiguration extends MarkedPropertyBase {

    public static final PingTemplateConfiguration DEFAULT = new PingTemplateConfiguration(TemplateCreationContext::getDefaultPing);
    public static final PingTemplateConfiguration ZERO = new PingTemplateConfiguration(tcc -> PingTemplate.ZERO);

    private final TemplateConstructor templateConstructor;

    private PingTemplateConfiguration(TemplateConstructor templateConstructor) {
        this.templateConstructor = templateConstructor;
    }

    public PingTemplateConfiguration(String value) {
        this.templateConstructor = tcc -> create(value, tcc);
    }

    // todo need clearer, simple control flow path

    private PingTemplate create(String value, TemplateCreationContext tcc) throws ConfigurationException {
        if (value.equals("${player ping}")) {
            if (!tcc.isPlayerAvailable()) {
                tcc.getErrorHandler().addWarning("${player ping} cannot be used here", getStartMark());
                return tcc.getDefaultPing();
            }
            return new PlayerPingTemplate(PlayerPlaceholder.BindPoint.PLAYER, tcc.getPlayerPingDataKey());
        } else if (value.equals("${viewer ping}")) {
            if (!tcc.isViewerAvailable()) {
                tcc.getErrorHandler().addWarning("${viewer ping} cannot be used here", getStartMark());
                return tcc.getDefaultPing();
            }
            return new PlayerPingTemplate(PlayerPlaceholder.BindPoint.VIEWER, tcc.getPlayerPingDataKey());
        } else {
            Integer ping;
            try {
                ping = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new MarkedConfigurationException("ping value not a number", getStartMark());
            }
            return new ConstantPingTemplate(ping);
        }
    }

    public PingTemplate toTemplate(TemplateCreationContext tcc) throws ConfigurationException {
        return templateConstructor.apply(tcc);
    }

    @FunctionalInterface
    private interface TemplateConstructor {

        PingTemplate apply(TemplateCreationContext tcc) throws ConfigurationException;
    }
}
