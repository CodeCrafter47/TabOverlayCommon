package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.IconTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.PingTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.TextTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.exception.ConfigurationException;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.BasicComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.yaml.snakeyaml.error.Mark;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class BasicComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    private TextTemplateConfiguration text = TextTemplateConfiguration.DEFAULT;
    private IconTemplateConfiguration icon = IconTemplateConfiguration.DEFAULT;
    private PingTemplateConfiguration ping = PingTemplateConfiguration.DEFAULT;
    private Alignment alignment = Alignment.LEFT;
    private LongTextBehaviour longText = null;

    private transient boolean needToFixMark = false;

    public BasicComponentConfiguration(String text) {
        if (text != null) {
            this.text = new TextTemplateConfiguration(text);
        }
        this.needToFixMark = true;
    }

    @Override
    public void setStartMark(Mark startMark) {
        super.setStartMark(startMark);
        if (needToFixMark) {
            this.text.setStartMark(startMark);
        }
    }

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) throws ConfigurationException {
        if (alignment != null && alignment != Alignment.LEFT && !tcc.getSlotWidth().isPresent()) {
            tcc.getErrorHandler().addWarning("Option `alignment: " + alignment + "` is not supported in this configuration.", getStartMark());
        }
        if (longText != null && longText != LongTextBehaviour.DISPLAY_ALL && !tcc.getSlotWidth().isPresent()){
            tcc.getErrorHandler().addWarning("Option `longText: " + longText + "` is not supported in this configuration.", getStartMark());
        }
        return BasicComponentTemplate.builder()
                .icon(icon != null ? icon.toTemplate(tcc) : tcc.getDefaultIcon())
                .text(text != null ? text.toTemplate(tcc) : tcc.getDefaultText())
                .ping(ping != null ? ping.toTemplate(tcc) : tcc.getDefaultPing())
                .alignment(alignment != null ? alignment : Alignment.LEFT)
                .longText(Optional.ofNullable(longText).orElse(tcc.getDefaultLongTextBehaviour().orElse(LongTextBehaviour.DISPLAY_ALL)))
                .slotWidth(tcc.getSlotWidth().orElse(80))
                .build();
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public enum LongTextBehaviour {
        DISPLAY_ALL, CROP, CROP_2DOTS, CROP_3DOTS;
    }
}
