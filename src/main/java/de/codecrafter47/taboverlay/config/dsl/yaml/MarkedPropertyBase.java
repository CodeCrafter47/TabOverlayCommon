package de.codecrafter47.taboverlay.config.dsl.yaml;

import org.yaml.snakeyaml.error.Mark;

public class MarkedPropertyBase implements MarkedProperty {
    private Mark startMark = null;

    @Override
    public Mark getStartMark() {
        return startMark;
    }

    @Override
    public void setStartMark(Mark startMark) {
        this.startMark = startMark;
    }
}
