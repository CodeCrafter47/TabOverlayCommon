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
    private boolean accepting = true;

    public void addError(@Nonnull @NonNull String message, @Nullable Mark position) {
        if (!accepting) {
            return;
        }
        entries.add(new Entry(Severity.ERROR, message, position));
    }

    public void addWarning(@Nonnull @NonNull String message, @Nullable Mark position) {
        if (!accepting) {
            return;
        }
        entries.add(new Entry(Severity.WARNING, message, position));
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
    }

    public enum Severity {
        WARNING, ERROR
    }
}
