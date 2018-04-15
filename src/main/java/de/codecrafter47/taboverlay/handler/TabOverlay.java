package de.codecrafter47.taboverlay.handler;

import javax.annotation.Nullable;

/**
 * A {@link TabOverlay} provides a way to modify the tab list.
 */
public interface TabOverlay {

    /**
     * Checks whether this {@link TabOverlay} is still valid, i.e. whether it is still displayed to a player.
     * <p>
     * Once invalidated a {@link TabOverlay} will remain invalid. So if a call this method returns false, then all
     * subsequent calls will return false.
     *
     * @return true if this {@link TabOverlay} is still valid
     */
    boolean isValid();

    /**
     * Allows multiple changes to a tab overlay to be grouped efficiently.
     * <p>
     * When a batch modification is in progress the implementation may delay updating the tab list to the client, until
     * the batch operation is completed.
     */
    interface BatchModifiable extends TabOverlay {

        /**
         * Marks the begin of a batch modification.
         */
        void beginBatchModification();

        /**
         * Marks the end of a batch modifications.
         */
        void completeBatchModification();
    }

    /**
     * Allows access to the tab list header and footer.
     */
    interface HeaderAndFooter {

        /**
         * Set the header and footer of the tab list.
         * <p>
         * The header and footer are provided as text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
         * <p>
         * A value of null removes the header or footer.
         *
         * @param header the header
         * @param footer the footer
         */
        void setHeaderFooter(@Nullable String header, @Nullable String footer);

        /**
         * Set the header and footer of the tab list.
         * <p>
         * The header and footer are provided as text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
         * <p>
         * A value of null removes the header or footer.
         *
         * @param header             the header
         * @param footer             the footer
         * @param alternateColorChar alternate color char used for text formatting
         */
        void setHeaderFooter(@Nullable String header, @Nullable String footer, char alternateColorChar);

        /**
         * Set the header of the tab list.
         * <p>
         * The header is provided as text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
         * <p>
         * A value of null removes the header.
         *
         * @param header the header
         */
        void setHeader(@Nullable String header);

        /**
         * Set the header of the tab list.
         * <p>
         * The header is provided as text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
         * <p>
         * A value of null removes the header.
         *
         * @param header             the header
         * @param alternateColorChar alternate color char used for text formatting
         */
        void setHeader(@Nullable String header, char alternateColorChar);

        /**
         * Set the footer of the tab list.
         * <p>
         * The footer is provided as text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
         * <p>
         * A value of null removes the footer.
         *
         * @param footer the footer
         */
        void setFooter(@Nullable String footer);

        /**
         * Set the footer of the tab list.
         * <p>
         * The footer is provided as text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
         * <p>
         * A value of null removes the footer.
         *
         * @param footer             the footer
         * @param alternateColorChar alternate color char used for text formatting
         */
        void setFooter(@Nullable String footer, char alternateColorChar);
    }
}
