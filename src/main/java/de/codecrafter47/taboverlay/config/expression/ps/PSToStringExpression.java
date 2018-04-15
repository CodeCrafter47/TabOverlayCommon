package de.codecrafter47.taboverlay.config.expression.ps;

import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.view.ActiveElement;

public interface PSToStringExpression extends ActiveElement {

    boolean evaluate(Player player);
}
