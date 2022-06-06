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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CustomPlaceholderColorAnimationRandom extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private Future<?> task;
    private final TextView textView;
    private final List<TextColor> colors;
    private final String formats;
    private String text;
    private String replacement;

    public CustomPlaceholderColorAnimationRandom(TextTemplate textTemplate, List<TextColor> colors, String formats) {
        this.textView = textTemplate.instantiate();
        this.colors = colors;
        this.formats = formats;
    }

    void updateText() {
        text = ChatFormat.stripFormat(textView.getText());
        updateReplacement();
    }

    void updateAnimation() {
        updateReplacement();

        if (hasListener()) {
            getListener().run();
        }
    }

    private void updateReplacement() {
        StringBuilder sb = new StringBuilder(text.length() * (9 + formats.length()) / 2);
        sb.append(randomColor().getFormatCode());
        sb.append(formats);
        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            if (Math.random() < 0.25) {
                sb.append(randomColor().getFormatCode());
                sb.append(formats);
            }
            sb.appendCodePoint(text.codePointAt(i));
        }
        replacement = sb.toString();
    }

    private TextColor randomColor() {
        double x = Math.random();
        x = x * colors.size();
        return colors.get((int) x);
    }

    @Override
    public String getData() {
        return replacement;
    }

    @Override
    protected void onActivation() {
        textView.activate(getContext(), this);
        updateText();
        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::updateAnimation, 250, 250, TimeUnit.MILLISECONDS);
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
