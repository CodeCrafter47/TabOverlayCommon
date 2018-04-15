package de.codecrafter47.taboverlay.util;

public final class Unchecked {

    /**
     * Prevents instance creation
     */
    private Unchecked() {
    }

    /**
     * Performes an unchecked cast
     * @param o object to cast
     * @param <T> target type
     * @return o
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }
}
