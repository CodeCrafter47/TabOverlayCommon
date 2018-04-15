package de.codecrafter47.taboverlay.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class OperationMode<T> {
    String name;

    public static OperationMode<TabOverlay> PASS_TROUGH = new OperationMode<>("PASS_TROUGH");
    public static OperationMode<RectangularTabOverlay> RECTANGULAR = new OperationMode<>("RECTANGULAR");
    public static OperationMode<RectangularTabOverlayWithHeaderAndFooter> RECTANGULAR_WITH_HEADER_AND_FOOTER = new OperationMode<>("RECTANGULAR_WITH_HEADER_AND_FOOTER");
    public static OperationMode<SimpleTabOverlay> SIMPLE = new OperationMode<>("SIMPLE");
    public static OperationMode<SimpleTabOverlayWithHeaderAndFooter> SIMPLE_WITH_HEADER_AND_FOOTER = new OperationMode<>("SIMPLE_WITH_HEADER_AND_FOOTER");
}
