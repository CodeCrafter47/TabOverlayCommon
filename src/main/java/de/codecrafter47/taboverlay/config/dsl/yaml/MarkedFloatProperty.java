package de.codecrafter47.taboverlay.config.dsl.yaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarkedFloatProperty extends MarkedPropertyBase {

    @Getter
    private final float value;
}
