package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.misc.TextColor;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CustomPlaceholderAnimated extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private Future<?> task;
    private final List<TextView> elements;
    private TextView activeElement;
    private int nextElementIndex;
    private final long intervalMS;
    private final boolean randomize;

    public CustomPlaceholderAnimated(List<TextTemplate> elements, float interval, boolean randomize) {
        this.elements = elements.stream().map(TextTemplate::instantiate).collect(Collectors.toList());
        this.intervalMS = (long) (interval * 1000);
        this.randomize = randomize;
        if (randomize) {
            Collections.shuffle(elements);
        }
    }

    @Override
    public String getData() {
        return activeElement.getText();
    }

    private void switchActiveElement() {
        activeElement.deactivate();
        if (nextElementIndex >= elements.size()) {
            nextElementIndex = 0;
            if (randomize) {
                Collections.shuffle(elements);
            }
        }
        activeElement = elements.get(nextElementIndex++);
        activeElement.activate(getContext(), this);
        if (hasListener()) {
            getListener().run();
        }
    }

    @Override
    protected void onActivation() {
        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::switchActiveElement, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
        activeElement = elements.get(0);
        activeElement.activate(getContext(), this);
        nextElementIndex = 1;
    }

    @Override
    protected void onDeactivation() {
        task.cancel(false);
        activeElement.deactivate();
    }

    @Override
    public void onTextUpdated() {
        if (hasListener()) {
            getListener().run();
        }
    }
}
