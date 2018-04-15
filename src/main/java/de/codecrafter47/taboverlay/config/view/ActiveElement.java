package de.codecrafter47.taboverlay.config.view;

/**
 * An active element.
 * <p>
 * The methods of this class are not thread safe. User code must take care of synchronization where necessary.
 */
public interface ActiveElement {

    /**
     * Deactivated the element.
     * <p>
     * The element must deactivate all children.
     * <p>
     * May never throw an exception.
     */
    void deactivate();
}
