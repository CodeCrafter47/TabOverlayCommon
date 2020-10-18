/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.dsl.components.BasicComponentConfiguration;
import de.codecrafter47.taboverlay.config.misc.ChatFormat;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import javax.annotation.Nullable;
import java.util.UUID;

public final class BasicComponentView extends ComponentView implements TextViewUpdateListener, PingViewUpdateListener, IconViewUpdateListener {

    private final TextView leftTextView;
    private final TextView centerTextView;
    private final TextView rightTextView;
    private final PingView pingView;
    private final IconView iconView;
    private final BasicComponentConfiguration.LongTextBehaviour longText;
    private int slotWidth;
    @Nullable
    private UUID uuid;
    @Nullable
    private String textAfterAlignment;

    public BasicComponentView(TextView leftTextView, TextView centerTextView, TextView rightTextView, PingView pingView, IconView iconView, BasicComponentConfiguration.LongTextBehaviour longText) {
        this.leftTextView = leftTextView;
        this.centerTextView = centerTextView;
        this.rightTextView = rightTextView;
        this.pingView = pingView;
        this.iconView = iconView;
        this.longText = longText;
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        Player player = getContext().getPlayer();
        uuid = player != null ? player.getUniqueID() : null;
        if (leftTextView != null)
            leftTextView.activate(getContext(), this);
        if (centerTextView != null)
            centerTextView.activate(getContext(), this);
        if (rightTextView != null)
            rightTextView.activate(getContext(), this);
        pingView.activate(getContext(), this);
        iconView.activate(getContext(), this);
    }

    private void updateSlot() {
        Area area = getArea();
        if (area != null) {
            area.setSlot(0, uuid, iconView.getIcon(), textAfterAlignment, '&', pingView.getPing());
        }
    }

    private void updateText() {

        String leftText = leftTextView != null ? leftTextView.getText() : "";

        if (centerTextView != null || rightTextView != null || longText != BasicComponentConfiguration.LongTextBehaviour.DISPLAY_ALL) {

            float leftTextLength = ChatFormat.formattedTextLength(leftText);

            if (centerTextView != null) {

                String centerText = centerTextView.getText();
                float centerTextLength = ChatFormat.formattedTextLength(centerText);

                leftText = leftText + ChatFormat.createSpaces(Math.max(4f, slotWidth / 2f - leftTextLength - centerTextLength / 2f)) + centerText;
                leftTextLength = ChatFormat.formattedTextLength(leftText);
            }

            String rightText = rightTextView != null ? rightTextView.getText() : "";
            float rightTextLength = ChatFormat.formattedTextLength(rightText);

            float totalTextLength = leftTextLength + (rightTextView != null ? 4f : 0f) + rightTextLength;

            if (longText != BasicComponentConfiguration.LongTextBehaviour.DISPLAY_ALL && totalTextLength > slotWidth) {
                String suffix = "";
                if (longText == BasicComponentConfiguration.LongTextBehaviour.CROP_2DOTS) {
                    suffix = "..";
                } else if (longText == BasicComponentConfiguration.LongTextBehaviour.CROP_3DOTS) {
                    suffix = "...";
                }
                float suffixLength = ChatFormat.formattedTextLength(suffix);
                leftText = ChatFormat.cropFormattedText(leftText, slotWidth - ((rightTextView != null ? 4f : 0f) + rightTextLength) - suffixLength) + suffix;
                leftTextLength = ChatFormat.formattedTextLength(leftText);
            }

            if (rightTextView != null) {
                leftText = leftText + ChatFormat.createSpacesExact(Math.max(4f, slotWidth - rightTextLength - leftTextLength)) + rightText;
            } else if (centerTextView != null) {
                leftText = leftText + ChatFormat.createSpaces(slotWidth - leftTextLength);
            }
        }

        textAfterAlignment = leftText;
    }

    @Override
    protected void onAreaUpdated() {
        if (getArea() != null) {
            this.slotWidth = getArea().getSlotWidth();
            updateText();
            updateSlot();
        }
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        // shouldn't be called
        throw new AssertionError();
    }

    @Override
    public void onTextUpdated() {
        Area area = getArea();
        if (area != null) {
            updateText();
            area.setText(0, textAfterAlignment, '&');
        }
    }

    @Override
    public void onPingUpdated() {
        Area area = getArea();
        if (area != null) {
            area.setPing(0, pingView.getPing());
        }
    }

    @Override
    public void onIconUpdated() {
        Area area = getArea();
        if (area != null) {
            area.setIcon(0, iconView.getIcon());
        }
    }

    @Override
    public int getMinSize() {
        return 1;
    }

    @Override
    public int getPreferredSize() {
        return 1;
    }

    @Override
    public int getMaxSize() {
        return 1;
    }

    @Override
    public boolean isBlockAligned() {
        return false;
    }

    @Override
    protected void onDeactivation() {
        if (leftTextView != null)
            leftTextView.deactivate();
        if (centerTextView != null)
            centerTextView.deactivate();
        if (rightTextView != null)
            rightTextView.deactivate();
        pingView.deactivate();
        iconView.deactivate();
        super.onDeactivation();
    }
}
