package de.codecrafter47.taboverlay.handler;

public interface TabOverlayHandler {

    /**
     * Change the operation mode of the tab list content.
     *
     * @param operationMode new operation mode
     * @param <R> type representing the tab list for the new operation mode
     * @return representation of the tab list
     * @throws UnsupportedOperationException if the operationMode is not supported.
     */
    <R> R enterContentOperationMode(ContentOperationMode<R> operationMode);

    /**
     * Change the operation mode of the tab list header and footer.
     *
     * @param operationMode new operation mode
     * @param <R> type representing the tab list for the new operation mode
     * @return representation of the tab list
     * @throws UnsupportedOperationException if the operationMode is not supported.
     */
    <R> R enterHeaderAndFooterOperationMode(HeaderAndFooterOperationMode<R> operationMode);
}
