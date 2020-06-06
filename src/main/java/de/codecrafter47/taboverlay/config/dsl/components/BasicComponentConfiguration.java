package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.IconTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.PingTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.TextTemplateConfiguration;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.BasicComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.yaml.snakeyaml.error.Mark;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class BasicComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    private TextTemplateConfiguration text = null;
    private TextTemplateConfiguration left = null;
    private TextTemplateConfiguration center = null;
    private TextTemplateConfiguration right = null;
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
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {
        if (alignment == null) {
            alignment = Alignment.LEFT;
        }
        if (text != null && alignment == Alignment.LEFT && left != null) {
            tcc.getErrorHandler().addWarning("Cannot use `text: \"...\", alignment: LEFT` and `left: \"...\"` at the same time", getStartMark());
        }
        if (text != null && alignment == Alignment.CENTER && center != null) {
            tcc.getErrorHandler().addWarning("Cannot use `text: \"...\", alignment: LEFT` and `left: \"...\"` at the same time", getStartMark());
        }
        if (text != null && alignment == Alignment.RIGHT && right != null) {
            tcc.getErrorHandler().addWarning("Cannot use `text: \"...\", alignment: LEFT` and `left: \"...\"` at the same time", getStartMark());
        }
        TextTemplate leftTemplate = left != null ? left.toTemplate(tcc) : alignment == Alignment.LEFT && text != null ? text.toTemplate(tcc) : null;
        TextTemplate centerTemplate = center != null ? center.toTemplate(tcc) : alignment == Alignment.CENTER && text != null ? text.toTemplate(tcc) : null;
        TextTemplate rightTemplate = right != null ? right.toTemplate(tcc) : alignment == Alignment.RIGHT && text != null ? text.toTemplate(tcc) : null;
        return BasicComponentTemplate.builder()
                .icon(icon != null ? icon.toTemplate(tcc) : tcc.getDefaultIcon())
                .leftText(leftTemplate)
                .centerText(centerTemplate)
                .rightText(rightTemplate)
                .ping(ping != null ? ping.toTemplate(tcc) : tcc.getDefaultPing())
                .longText(Optional.ofNullable(longText).orElse(tcc.getDefaultLongTextBehaviour().orElse(LongTextBehaviour.DISPLAY_ALL)))
                .build();
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }

    public enum LongTextBehaviour {
        DISPLAY_ALL, CROP, CROP_2DOTS, CROP_3DOTS;
    }
}
