package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.misc.ChatFormat;
import de.codecrafter47.taboverlay.config.misc.TextColor;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CustomPlaceholderColorAnimation extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private Future<?> task;
    private TextView textView;
    private final List<TextColor> colors;
    private final OptionalInt distance;
    private final float speed;
    private String text;
    private float effectiveDistance;
    private float pos = 0.0f;
    private float period;
    private String replacement;

    public CustomPlaceholderColorAnimation(TextTemplate textTemplate, List<TextColor> colors, OptionalInt distance, float speed) {
        this.textView = textTemplate.instantiate();
        this.colors = colors;
        this.distance = distance;
        this.speed = speed;
    }

    void updateText() {
        text = ChatFormat.stripFormat(textView.getText());
        if (distance.isPresent()) {
            effectiveDistance = distance.getAsInt();
        } else {
            effectiveDistance = ChatFormat.formattedTextLength(text) / (colors.size() - 1);
        }
        period = effectiveDistance * colors.size();
        updateReplacement();
    }

    void updateAnimation() {
        updateReplacement();

        pos += speed;
        if (this.pos < 0.0) {
            this.pos += period;
        }
        if (this.pos > period) {
            this.pos -= period;
        }

        if (hasListener()) {
            getListener().run();
        }
    }

    private void updateReplacement() {
        StringBuilder sb = new StringBuilder(text.length() * 9);
        double d = pos;
        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            double sd = d / effectiveDistance;
            int ia = (int) sd;
            TextColor a = colors.get(ia % colors.size());
            TextColor b = colors.get((ia + 1) % colors.size());
            TextColor c = TextColor.interpolateSine(a, b, sd - ia);
            sb.append(c.getFormatCode());
            sb.appendCodePoint(text.codePointAt(i));
            d += ChatFormat.getCharWidth(text.codePointAt(i));
        }
        replacement = sb.toString();
    }

    @Override
    public String getData() {
        return replacement;
    }

    @Override
    protected void onActivation() {
        textView.activate(getContext(), this);
        updateText();
        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::updateAnimation, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDeactivation() {
        task.cancel(false);
        textView.deactivate();
    }

    @Override
    public void onTextUpdated() {
        updateText();
        if (hasListener()) {
            getListener().run();
        }
    }
}
