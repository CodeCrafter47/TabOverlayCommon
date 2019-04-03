package de.codecrafter47.taboverlay.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderAndFooterOperationMode<T> {
    String name;

    public static HeaderAndFooterOperationMode<TabOverlayHandle> PASS_TROUGH = new HeaderAndFooterOperationMode<>("PASS_TROUGH");
    public static HeaderAndFooterOperationMode<HeaderAndFooterHandle> CUSTOM = new HeaderAndFooterOperationMode<>("CUSTOM");
}
