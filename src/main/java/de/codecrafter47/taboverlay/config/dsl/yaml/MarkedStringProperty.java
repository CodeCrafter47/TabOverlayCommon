package de.codecrafter47.taboverlay.config.dsl.yaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarkedStringProperty extends MarkedPropertyBase {

    @Getter
    private final String value;
}
