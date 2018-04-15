package de.codecrafter47.taboverlay.config.view.text;


import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TextViewAnimated extends AbstractActiveElement<TextViewUpdateListener> implements TextView {

    private Future<?> task;
    private final List<TextTemplate> elements; // todo using text views instead of templates here might improve performance
    private TextView activeElement;
    private int nextElementIndex;
    private final long intervalMS;

    public TextViewAnimated(float interval, List<TextTemplate> elements) {
        this.elements = elements;
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
        activeElement = elements.get(nextElementIndex++).instantiate();
        activeElement.activate(getContext(), hasListener() ? getListener() : null);
        if (hasListener()) {
            getListener().onTextUpdated();
        }
    }

    @Override
    protected void onActivation() {
        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::switchActiveElement, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
        activeElement = elements.get(0).instantiate();
        activeElement.activate(getContext(), hasListener() ? getListener() : null);
        nextElementIndex = 1;
    }

    @Override
    protected void onDeactivation() {
        task.cancel(false);
        activeElement.deactivate();
    }
}
