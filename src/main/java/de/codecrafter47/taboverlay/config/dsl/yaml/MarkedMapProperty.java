package de.codecrafter47.taboverlay.config.dsl.yaml;

import org.yaml.snakeyaml.error.Mark;

import java.util.LinkedHashMap;

public class MarkedMapProperty<K, V> extends LinkedHashMap<K, V> implements MarkedProperty {
    private final MarkedPropertyBase delegate = new MarkedPropertyBase();

    @Override
    public Mark getStartMark() {
        return delegate.getStartMark();
    }

    @Override
    public void setStartMark(Mark startMark) {
        delegate.setStartMark(startMark);
    }
}
