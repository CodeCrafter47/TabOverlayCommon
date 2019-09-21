package de.codecrafter47.taboverlay.config;

import lombok.NonNull;
import lombok.Value;
import org.yaml.snakeyaml.error.Mark;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorHandler {

    private static ErrorHandler current = null;

    public static void set(ErrorHandler errorHandler) {
        current = errorHandler;
    }

    public static ErrorHandler get() {
        if (current == null) {
            current = new ErrorHandler();
        }
        return current;
    }

    private final List<Entry> entries = new ArrayList<>();
    private final List<Context> contextStack = new ArrayList<>();
    private boolean accepting = true;

    public void addError(@Nonnull @NonNull String message, @Nullable Mark position) {
        if (!accepting) {
            return;
        }
        entries.add(new Entry(Severity.ERROR, message, position, contextStack.isEmpty() ? null : new ArrayList<>(contextStack)));
    }

    public void addWarning(@Nonnull @NonNull String message, @Nullable Mark position) {
        if (!accepting) {
            return;
        }
        entries.add(new Entry(Severity.WARNING, message, position, contextStack.isEmpty() ? null : new ArrayList<>(contextStack)));
    }

    public boolean hasErrors() {
        return entries.stream()
                .anyMatch(entry -> entry.getSeverity() == Severity.ERROR);
    }

    public void stopAccepting() {
        accepting = false;
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void enterContext(String message, Mark mark) {
        if (accepting) {
            contextStack.add(0, new Context(message, mark));
        }
    }

    public void leaveContext() {
        if (accepting && !contextStack.isEmpty()) {
            contextStack.remove(0);
        }
    }

    @Value
    public static class Entry {
        @Nonnull
        @NonNull
        Severity severity;
        @Nonnull
        @NonNull
        String message;
        @Nullable
        Mark position;
        @Nullable
        List<Context> context;
    }

    public enum Severity {
        WARNING, ERROR
    }

    @Value
    public static class Context {
        @Nonnull
        @NonNull
        String message;
        @Nullable
        Mark position;
    }
}
