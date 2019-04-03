package de.codecrafter47.taboverlay.config.area;

import com.google.common.base.Preconditions;
import de.codecrafter47.taboverlay.Icon;

import java.util.UUID;

public interface Area {

    default void setSlot(int index, Icon icon, String text, char alternateColorChar, int ping) {
        setSlot(index, null, icon, text, alternateColorChar, ping);
    }

    default void setSlot(int index, Icon icon, String text, int ping) {
        setSlot(index, null, icon, text, ping);
    }

    void setSlot(int index, UUID uuid, Icon icon, String text, char alternateColorChar, int ping);

    void setSlot(int index, UUID uuid, Icon icon, String text, int ping);

    void setUuid(int index, UUID uuid);

    void setIcon(int index, Icon icon);

    void setText(int index, String text);

    void setText(int index, String text, char alternateColorChar);

    void setPing(int index, int ping);

    int getSize();

    default Area createChild(int firstIndex, int size) {
        Preconditions.checkArgument(firstIndex < getSize(), "firstIndex < getSize()");
        Preconditions.checkArgument(firstIndex + size <= getSize(), "firstIndex + size <= getSize()");
        return new AreaWithOffset(this, firstIndex, size);
    }

    RectangularArea asRectangularArea();
}
