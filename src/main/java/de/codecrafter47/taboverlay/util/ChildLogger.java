package de.codecrafter47.taboverlay.util;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ChildLogger extends Logger {

    private final String prefix;

    public ChildLogger(Logger parent, String name) {
        super(parent.getName() + "." + name, null);
        this.prefix = "[" + name + "] ";
        setParent(parent);
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(prefix + record.getMessage());
        super.log(record);
    }
}
