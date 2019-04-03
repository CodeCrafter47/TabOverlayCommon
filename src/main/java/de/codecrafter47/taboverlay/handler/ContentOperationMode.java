package de.codecrafter47.taboverlay.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContentOperationMode<T> {
    String name;

    public static ContentOperationMode<TabOverlayHandle> PASS_TROUGH = new ContentOperationMode<>("PASS_TROUGH");
    public static ContentOperationMode<RectangularTabOverlay> RECTANGULAR = new ContentOperationMode<>("RECTANGULAR");
    public static ContentOperationMode<SimpleTabOverlay> SIMPLE = new ContentOperationMode<>("SIMPLE");
}
