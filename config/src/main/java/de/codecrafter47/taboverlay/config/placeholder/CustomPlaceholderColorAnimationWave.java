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

package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.misc.ChatFormat;
import de.codecrafter47.taboverlay.config.misc.TextColor;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CustomPlaceholderColorAnimationWave extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private static final float PULSE_RADIUS = 10;

    private Future<?> task;
    private final TextView textView;
    private final TextColor baseColor;
    private final TextColor effectColor;
    private final float speed;
    private final String formats;
    private String text;
    private float pos = 0.0f;
    private float period;
    private String replacement;

    public CustomPlaceholderColorAnimationWave(TextTemplate textTemplate, TextColor baseColor, TextColor effectColor, float speed, String formats) {
        this.textView = textTemplate.instantiate();
        this.baseColor = baseColor;
        this.effectColor = effectColor;
        this.speed = speed;
        this.formats = formats;
    }

    void updateText() {
        text = ChatFormat.stripFormat(textView.getText());
        float textLength = ChatFormat.formattedTextLength(text);
        period = Float.max(textLength * 2.5f, textLength + PULSE_RADIUS * 2f);
        updateReplacement();
    }

    void updateAnimation() {
        updateReplacement();

        pos += speed;
        if (this.pos < -PULSE_RADIUS) {
            this.pos += period;
        }
        if (this.pos > period - PULSE_RADIUS) {
            this.pos -= period;
        }

        if (hasListener()) {
            getListener().run();
        }
    }

    private void updateReplacement() {
        StringBuilder sb = new StringBuilder(text.length() * (9 + formats.length()));
        float min = this.pos - PULSE_RADIUS;
        float max = this.pos + PULSE_RADIUS;
        double d = 0;
        boolean hasBaseColor = false;
        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            if (d > min && d < max) {
                double factor = Math.abs(d - this.pos) / PULSE_RADIUS;
                TextColor color = TextColor.interpolateSine(this.effectColor, this.baseColor, factor);
                sb.append(color.getFormatCode());
                sb.append(formats);
                hasBaseColor = false;
            } else if (!hasBaseColor) {
                sb.append(baseColor.getFormatCode());
                sb.append(formats);
                hasBaseColor = true;
            }
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
        if (speed != 0) {
            task = getContext().getTabEventQueue().scheduleAtFixedRate(this::updateAnimation, 100, 100, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void onDeactivation() {
        if (task != null) {
            task.cancel(false);
        }
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
