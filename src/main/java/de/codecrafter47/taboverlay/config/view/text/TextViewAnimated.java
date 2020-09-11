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

package de.codecrafter47.taboverlay.config.view.text;


import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TextViewAnimated extends AbstractActiveElement<TextViewUpdateListener> implements TextView {

    private Future<?> task;
    private final List<TextView> elements;
    private TextView activeElement;
    private int nextElementIndex;
    private final long intervalMS;

    public TextViewAnimated(float interval, List<TextTemplate> elements) {
        this.elements = elements.stream().map(TextTemplate::instantiate).collect(Collectors.toList());
        this.intervalMS = (long) (interval * 1000);
    }

    @Override
    public String getText() {
        return activeElement.getText();
    }

    private void switchActiveElement() {
        activeElement.deactivate();
        if (nextElementIndex >= elements.size()) {
            nextElementIndex = 0;
        }
        activeElement = elements.get(nextElementIndex++);
        activeElement.activate(getContext(), hasListener() ? getListener() : null);
        if (hasListener()) {
            getListener().onTextUpdated();
        }
    }

    @Override
    protected void onActivation() {
        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::switchActiveElement, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
        activeElement = elements.get(0);
        activeElement.activate(getContext(), hasListener() ? getListener() : null);
        nextElementIndex = 1;
    }

    @Override
    protected void onDeactivation() {
        task.cancel(false);
        activeElement.deactivate();
    }
}
