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

package de.codecrafter47.taboverlay.config.expression.token;

public class DefaultTokens {
    public static final Token AND = new Token("AND");
    public static final Token OR = new Token("OR");
    public static final Token OPENING_PARENTHESIS = new Token("OPENING_PARENTHESIS");
    public static final Token CLOSING_PARENTHESIS = new Token("CLOSING_PARENTHESIS");
    public static final Token EQUAL = new Token("EQUAL");
    public static final Token NOT_EQUAL = new Token("NOT_EQUAL");
    public static final Token NEGATION = new Token("NEGATION");
    public static final Token GREATER_THAN = new Token("GREATER_THAN");
    public static final Token LESSER_THAN = new Token("LESSER_THAN");
    public static final Token GREATER_OR_EQUAL_THAN = new Token("GREATER_OR_EQUAL_THAN");
    public static final Token LESSER_OR_EQUAL_THAN = new Token("LESSER_OR_EQUAL_THAN");
    public static final Token CONCAT_STRING = new Token("CONCAT_STRING");
    public static final Token ADD = new Token("ADD");
    public static final Token SUB = new Token("SUB");
    public static final Token MULT = new Token("MULT");
    public static final Token DIV = new Token("DIV");
}
