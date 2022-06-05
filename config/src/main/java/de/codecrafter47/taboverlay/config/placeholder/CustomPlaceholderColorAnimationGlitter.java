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

public class CustomPlaceholderColorAnimationGlitter extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private Future<?> task;
    private final TextView textView;
    private final TextColor baseColor;
    private final TextColor effectColor;
    private String text;
    private String replacement;

    public CustomPlaceholderColorAnimationGlitter(TextTemplate textTemplate, TextColor baseColor, TextColor effectColor) {
        this.textView = textTemplate.instantiate();
        this.baseColor = baseColor;
        this.effectColor = effectColor;
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
        StringBuilder sb = new StringBuilder(text.length() * 4);
        sb.append(baseColor.getFormatCode());
        boolean hasBaseColor = true;
        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            if (!hasBaseColor) {
                sb.append(baseColor.getFormatCode());
                hasBaseColor = true;
            } else if (Math.random() < 0.05) {
                sb.append(effectColor.getFormatCode());
                hasBaseColor = false;
            }
            sb.appendCodePoint(text.codePointAt(i));
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
        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::updateAnimation, 150, 150, TimeUnit.MILLISECONDS);
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
