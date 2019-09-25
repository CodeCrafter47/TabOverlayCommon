package de.codecrafter47.taboverlay.config.placeholder;

import java.util.function.Function;

public interface PlaceholderDataProvider<C, D> {

    void activate(C context, Runnable listener);

    void deactivate();

    D getData();

    default <D2> PlaceholderDataProvider<C, D2> transformData(Function<D, D2> transormation) {
        return new PlaceholderDataProvider<C, D2>() {
            @Override
            public void activate(C context, Runnable listener) {
                PlaceholderDataProvider.this.activate(context, listener);
            }

            @Override
            public void deactivate() {
                PlaceholderDataProvider.this.deactivate();
            }

            @Override
            public D2 getData() {
                return transormation.apply(PlaceholderDataProvider.this.getData());
            }
        };
    }
}
