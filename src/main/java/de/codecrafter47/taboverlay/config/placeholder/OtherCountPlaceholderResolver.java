package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.util.List;

public class OtherCountPlaceholderResolver implements PlaceholderResolver<Context> {
    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException {
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "other_count".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            args.remove(0);
            return builder.acquireData(OtherCountDataProvider::new, TypeToken.INTEGER, false);
        }
        throw new UnknownPlaceholderException();
    }

    private static class OtherCountDataProvider implements PlaceholderDataProvider<Context, Integer> {

        private Context context;

        @Override
        public void activate(Context context, Runnable listener) {
            this.context = context;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public Integer getData() {
            return context.getCustomObject(ContextKeys.OTHER_COUNT);
        }
    }
}
