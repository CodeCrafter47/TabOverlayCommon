package de.codecrafter47.taboverlay.config.dsl.yaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarkedIntegerProperty extends MarkedPropertyBase {

    @Getter
    private final int value;
}
