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

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CustomPlaceholderColorAnimationUniformRainbow extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

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

    public CustomPlaceholderColorAnimationUniformRainbow(TextTemplate textTemplate, List<TextColor> colors, OptionalInt distance, float speed) {
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
        StringBuilder sb = new StringBuilder(text.length() + 9);
        double d = pos;
        double sd = d / effectiveDistance;
        int ia = (int) sd;
        TextColor a = colors.get(ia % colors.size());
        TextColor b = colors.get((ia + 1) % colors.size());
        TextColor c = TextColor.interpolateSine(a, b, sd - ia);
        sb.append(c.getFormatCode());
        sb.append(text);
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
