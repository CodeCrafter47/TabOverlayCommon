package de.codecrafter47.taboverlay.handler;

public interface TabOverlayHandle {
    /**
     * Checks whether this {@link TabOverlayHandle} is still valid, i.e. whether it is still displayed to a player.
     * <p>
     * Once invalidated a {@link TabOverlayHandle} will remain invalid. So if a call this method returns false, then all
     * subsequent calls will return false.
     *
     * @return true if this {@link TabOverlayHandle} is still valid
     */
    boolean isValid();

    /**
     * Allows multiple changes to a tab overlay to be grouped efficiently.
     * <p>
     * When a batch modification is in progress the implementation may delay updating the tab list to the client, until
     * the batch operation is completed.
     */
    interface BatchModifiable extends TabOverlayHandle {

        /**
         * Marks the begin of a batch modification.
         */
        void beginBatchModification();

        /**
         * Marks the end of a batch modifications.
         */
        void completeBatchModification();
    }
}
