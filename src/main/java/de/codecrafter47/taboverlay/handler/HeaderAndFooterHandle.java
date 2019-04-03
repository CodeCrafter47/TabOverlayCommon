package de.codecrafter47.taboverlay.handler;

import javax.annotation.Nullable;

/**
 * Allows modifying the header and footer of the tab overlay.
 */
public interface HeaderAndFooterHandle extends TabOverlayHandle, TabOverlayHandle.BatchModifiable {

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
