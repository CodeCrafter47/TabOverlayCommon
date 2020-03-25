package de.codecrafter47.taboverlay.config.dsl.yaml;

import org.yaml.snakeyaml.error.Mark;

import java.beans.Transient;

public class MarkedPropertyBase implements MarkedProperty {
    private Mark startMark = null;

    @Override
    @Transient
    public Mark getStartMark() {
        return startMark;
    }

    @Override
    public void setStartMark(Mark startMark) {
        this.startMark = startMark;
    }
}
