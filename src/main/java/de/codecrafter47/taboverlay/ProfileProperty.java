package de.codecrafter47.taboverlay;

import lombok.Value;

import java.io.Serializable;

@Value
public class ProfileProperty implements Serializable {
    private static final long serialVersionUID = 1726866040868239187L;

    String name;
    String value;
    String signature;

    public boolean isSigned() {
        return getSignature() != null;
    }
}
