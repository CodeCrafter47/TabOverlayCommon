package de.codecrafter47.taboverlay.config.dsl.yaml;

import org.yaml.snakeyaml.error.Mark;

public interface MarkedProperty {
    Mark getStartMark();

    void setStartMark(Mark startMark);
}
