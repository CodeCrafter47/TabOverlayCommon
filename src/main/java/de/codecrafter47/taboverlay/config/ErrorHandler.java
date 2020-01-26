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

    public ErrorHandler copy() {
        ErrorHandler copy = new ErrorHandler();
        copy.accepting = accepting;
        copy.entries.addAll(entries);
        copy.contextStack.addAll(contextStack);
        return copy;
    }

    public String formatErrors(String fileName) {
        StringBuilder message = new StringBuilder();
        int errCnt = 0;
        int warnCnt = 0;
        for (ErrorHandler.Entry error : getEntries()) {
            message.append("\n");
            if (error.getSeverity() == ErrorHandler.Severity.WARNING) {
                message.append("WARNING: ").append(error.getMessage());
                warnCnt += 1;
            } else if (error.getSeverity() == ErrorHandler.Severity.ERROR) {
                message.append("ERROR: ").append(error.getMessage());
                errCnt += 1;
            } else {
                throw new AssertionError("Unknown error severity");
            }
            Mark position = error.getPosition();
            if (position != null) {
                message.append("\n").append(position.toString());
            }
            List<ErrorHandler.Context> context = error.getContext();
            if (context != null) {
                for (ErrorHandler.Context contextElement : context) {
                    message.append("\n ").append(contextElement.getMessage());
                    position = contextElement.getPosition();
                    if (position != null) {
                        message.append("\n").append(position.toString());
                    }
                }
            }
        }
        String msg;
        if (errCnt == 0) {
            msg = "There have been " + warnCnt + " warnings while loading " + fileName + message + "\n";
        } else {
            msg = "Failed to load " + fileName + ".\n" + errCnt + " errors and " + warnCnt + " warnings" + message + "\n";
        }
        return msg;
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
