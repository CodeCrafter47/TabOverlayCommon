package de.codecrafter47.taboverlay.config.misc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

public class ChatFormat {

    private static final char COLOR_CHAR = '\u00a7';
    private static final Map<String, FontInfo> CHAR_WIDTH;
    private static final String EMPTY_JSON_TEXT = "{\"text\":\"\"}";
    private static final CharSet HEX_CHARS = new CharOpenHashSet(new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'});
    private static final FontInfo DEFAULT_FONT;

    static {
        InputStream resourceAsStream = ChatFormat.class.getResourceAsStream("char-width.json");
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        CHAR_WIDTH = new Gson().fromJson(inputStreamReader, new TypeToken<Map<String, FontInfo>>() {
        }.getType());
        DEFAULT_FONT = CHAR_WIDTH.get("minecraft:default");
    }

    public static String formattedTextToJson(String text) {
        if (text == null || text.isEmpty()) {
            return EMPTY_JSON_TEXT;
        }

        StringBuilder jsonBuilder = new StringBuilder("{\"text\":\"\",\"extra\":[");
        StringBuilder builder = new StringBuilder();
        boolean bold = false;
        boolean italic = false;
        boolean underlined = false;
        boolean strikeout = false;
        boolean obfuscated = false;
        boolean first = true;
        String color = "white";
        String font = null;

        for (int i = 0; i < text.length(); ++i) {
            Style style = readFormatCode(text, i);

            if (style != null) {
                if (builder.length() > 0) {
                    if (first) {
                        first = false;
                    } else {
                        jsonBuilder.append(",");
                    }
                    jsonBuilder.append("{\"text\":\"").append(builder.toString()).append("\"");
                    jsonBuilder.append(",\"color\":\"").append(color).append("\"");
                    if (font != null) {
                        jsonBuilder.append(",\"font\":\"").append(font).append("\"");
                    }
                    if (bold) {
                        jsonBuilder.append(",\"bold\":\"true\"");
                    }
                    if (italic) {
                        jsonBuilder.append(",\"italic\":\"true\"");
                    }
                    if (underlined) {
                        jsonBuilder.append(",\"underlined\":\"true\"");
                    }
                    if (strikeout) {
                        jsonBuilder.append(",\"strikethrough\":\"true\"");
                    }
                    if (obfuscated) {
                        jsonBuilder.append(",\"obfuscated\":\"true\"");
                    }
                    jsonBuilder.append("}");
                    builder.setLength(0);
                }

                switch (style.type) {
                    case BOLD:
                        bold = true;
                        break;
                    case ITALIC:
                        italic = true;
                        break;
                    case UNDERLINE:
                        underlined = true;
                        break;
                    case STRIKE_THROUGH:
                        strikeout = true;
                        break;
                    case OBFUSCATED:
                        obfuscated = true;
                        break;
                    case COLOR:
                        bold = false;
                        italic = false;
                        underlined = false;
                        strikeout = false;
                        obfuscated = false;
                        color = style.stringValue;
                        break;
                    case FONT:
                        font = style.stringValue;
                }

                i += style.formatCodeLength - 1;
            } else {
                char c = text.charAt(i);
                if (mustEscape(c)) {
                    builder.append(escape(c));
                } else {
                    builder.append(c);
                }
            }
        }

        if (builder.length() > 0) {
            if (first) {
                first = false;
            } else {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("{\"text\":\"").append(builder.toString()).append("\"");
            jsonBuilder.append(",\"color\":\"").append(color).append("\"");
            if (font != null) {
                jsonBuilder.append(",\"font\":\"").append(font).append("\"");
            }
            if (bold) {
                jsonBuilder.append(",\"bold\":\"true\"");
            }
            if (italic) {
                jsonBuilder.append(",\"italic\":\"true\"");
            }
            if (underlined) {
                jsonBuilder.append(",\"underlined\":\"true\"");
            }
            if (strikeout) {
                jsonBuilder.append(",\"strikethrough\":\"true\"");
            }
            if (obfuscated) {
                jsonBuilder.append(",\"obfuscated\":\"true\"");
            }
            jsonBuilder.append("}");
            builder.setLength(0);
        }

        if (first) {
            return EMPTY_JSON_TEXT;
        }

        jsonBuilder.append("]}");
        return jsonBuilder.toString();
    }

    private static Style readFormatCode(String text, int index) {
        char c = text.charAt(index);
        if (index + 1 < text.length() && isFormatChar(c)) {
            switch (text.charAt(index + 1)) {
                case '0':
                    return COLOR_BLACK;
                case '1':
                    return COLOR_DARK_BLUE;
                case '2':
                    return COLOR_DARK_GREEN;
                case '3':
                    return COLOR_DARK_AQUA;
                case '4':
                    return COLOR_DARK_RED;
                case '5':
                    return COLOR_DARK_PURPLE;
                case '6':
                    return COLOR_GOLD;
                case '7':
                    return COLOR_GRAY;
                case '8':
                    return COLOR_DARK_GRAY;
                case '9':
                    return COLOR_BLUE;
                case 'a':
                case 'A':
                    return COLOR_GREEN;
                case 'b':
                case 'B':
                    return COLOR_AQUA;
                case 'c':
                case 'C':
                    return COLOR_RED;
                case 'd':
                case 'D':
                    return COLOR_LIGHT_PURPLE;
                case 'e':
                case 'E':
                    return COLOR_YELLOW;
                case 'f':
                case 'F':
                case 'r':
                case 'R':
                    return COLOR_WHITE;
                case 'k':
                case 'K':
                    return STYLE_OBFUSCATED;
                case 'l':
                case 'L':
                    return STYLE_BOLD;
                case 'm':
                case 'M':
                    return STYLE_STRIKE_THROUGH;
                case 'n':
                case 'N':
                    return STYLE_UNDERLINE;
                case 'o':
                case 'O':
                    return STYLE_ITALIC;
                case 'x':
                    if (isFormatChar(c)
                            && index + 13 < text.length()
                            && isFormatChar(text.charAt(index + 2))
                            && isFormatChar(text.charAt(index + 4))
                            && isFormatChar(text.charAt(index + 6))
                            && isFormatChar(text.charAt(index + 8))
                            && isFormatChar(text.charAt(index + 10))
                            && isFormatChar(text.charAt(index + 12))) {
                        return new Style(Style.Type.COLOR, escape("#"
                                + text.charAt(index + 3)
                                + text.charAt(index + 5)
                                + text.charAt(index + 7)
                                + text.charAt(index + 9)
                                + text.charAt(index + 11)
                                + text.charAt(index + 13)), 14);
                    }
                    break;
                case '#':
                    if (index + 7 < text.length()
                            && HEX_CHARS.contains(text.charAt(index + 2))
                            && HEX_CHARS.contains(text.charAt(index + 3))
                            && HEX_CHARS.contains(text.charAt(index + 4))
                            && HEX_CHARS.contains(text.charAt(index + 5))
                            && HEX_CHARS.contains(text.charAt(index + 6))
                            && HEX_CHARS.contains(text.charAt(index + 7))) {
                        return new Style(Style.Type.COLOR, text.substring(index + 1, index + 8), 8);
                    }
                    break;
            }
        }
        if (c == '[') {
            if (text.regionMatches(true, index, "[color=", 0, 7)) {
                int endIdx = text.indexOf(']', index + 7);
                if (endIdx >= 0) {
                    return new Style(Style.Type.COLOR, escape(text.substring(index + 7, endIdx)), endIdx + 1 - index);
                }
            }
            if (text.regionMatches(true, index, "[font=", 0, 6)) {
                int endIdx = text.indexOf(']', index + 6);
                if (endIdx >= 0) {
                    return new Style(Style.Type.FONT, escape(text.substring(index + 6, endIdx)), endIdx + 1 - index);
                }
            }
        }
        return null;
    }

    private static boolean isFormatChar(char c) {
        return c == '&' || c == COLOR_CHAR;
    }

    private static boolean mustEscape(char c) {
        return c == '"' || c == '\\' || c <= 0x1F;
    }

    private static String escape(char c) {
        if (c == '"') {
            return "\\\"";
        } else if (c == '\\') {
            return "\\\\";
        } else if (c == '\n') {
            return "\\n";
        } else if (c <= 0x000f) {
            return "\\u000" + Integer.toHexString(c).toUpperCase();
        } else {
            return "\\u00" + Integer.toHexString(c).toUpperCase();
        }
    }

    private static String escape(String s) {
        boolean mustEscape = false;
        for (int i = 0; i < s.length(); i++) {
            mustEscape |= mustEscape(s.charAt(i));
        }
        if (mustEscape) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (mustEscape(s.charAt(i))) {
                    sb.append(escape(s.charAt(i)));
                } else {
                    sb.append(s.charAt(i));
                }
            }
            return sb.toString();
        } else {
            return s;
        }
    }

    public static double getCharWidth(int codePoint) {
        return getCharWidth(codePoint, DEFAULT_FONT, false);
    }

    private static double getCharWidth(int codePoint, FontInfo font, boolean isBold) {
        int index = Arrays.binarySearch(font.codePoints, codePoint);
        if (index < 0) {
            return isBold ? 7.0f : 6.0f;
        } else {
            return isBold ? font.advanceBf[index] : font.advance[index];
        }
    }

    public static float formattedTextLength(String text) {
        float length = 0;
        boolean bold = false;
        FontInfo font = DEFAULT_FONT;

        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            Style style = readFormatCode(text, i);
            if (style != null) {
                if (style.type == Style.Type.COLOR) {
                    bold = false;
                } else if (style.type == Style.Type.BOLD) {
                    bold = true;
                } else if (style.type == Style.Type.FONT) {
                    font = CHAR_WIDTH.get(style.stringValue);
                    if (font == null) {
                        font = DEFAULT_FONT;
                    }
                }
                i += style.formatCodeLength - 1;
            } else {
                length += getCharWidth(text.codePointAt(i), font, bold);
            }
        }
        return length;
    }

    public static String cropFormattedText(String text, float maxLength) {
        float length = 0;
        boolean bold = false;
        FontInfo font = DEFAULT_FONT;

        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            Style style = readFormatCode(text, i);
            if (style != null) {
                if (style.type == Style.Type.COLOR) {
                    bold = false;
                } else if (style.type == Style.Type.BOLD) {
                    bold = true;
                } else if (style.type == Style.Type.FONT) {
                    font = CHAR_WIDTH.get(style.stringValue);
                    if (font == null) {
                        font = DEFAULT_FONT;
                    }
                }
                i += style.formatCodeLength - 1;
            } else {
                length += getCharWidth(text.codePointAt(i), font, bold);
                if (length > maxLength) {
                    return text.substring(0, i);
                }
            }
        }
        return text;
    }

    public static String stripFormat(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i += Character.charCount(text.codePointAt(i))) {
            Style style = readFormatCode(text, i);
            if (style == null) {
                sb.appendCodePoint(text.codePointAt(i));
            }
        }
        return sb.toString();
    }

    public static String createSpaces(float length) {
        int spaces = (int) Math.floor(length / 4f);
        int boldCharacters = (int) Math.floor(length - spaces * 4f);
        StringBuilder sb = new StringBuilder(spaces + 30);
        sb.append("[font=minecraft:default]&r");
        int i = 0;
        for (; i < spaces - boldCharacters; i++)
            sb.append(' ');
        sb.append("&l");
        for (; i < spaces; i++) {
            sb.append(' ');
        }
        sb.append("&r");
        return sb.toString();
    }

    public static String createSpacesExact(float length) {
        String spaces = createSpaces(length);
        StringBuilder sb = new StringBuilder(spaces);
        float missing = length - formattedTextLength(spaces);
        for (; missing >= 2f; missing -= 2) {
            sb.append('\u061E');
        }
        if (missing >= 1f) {
            sb.append('\u205A');
        }
        if (sb.length() >= 1 && missing > 0f) {
            sb.insert(sb.length() - 1, "&l");
            sb.append("&r");
        }
        return sb.toString();
    }

    private static class FontInfo {
        private int[] codePoints;
        private float[] advance;
        private float[] advanceBf;
    }

    private static final Style STYLE_BOLD = new Style(Style.Type.BOLD, null, 2);
    private static final Style STYLE_ITALIC = new Style(Style.Type.ITALIC, null, 2);
    private static final Style STYLE_UNDERLINE = new Style(Style.Type.UNDERLINE, null, 2);
    private static final Style STYLE_STRIKE_THROUGH = new Style(Style.Type.STRIKE_THROUGH, null, 2);
    private static final Style STYLE_OBFUSCATED = new Style(Style.Type.OBFUSCATED, null, 2);
    private static final Style COLOR_WHITE = new Style(Style.Type.COLOR, "white", 2);
    private static final Style COLOR_YELLOW = new Style(Style.Type.COLOR, "yellow", 2);
    private static final Style COLOR_LIGHT_PURPLE = new Style(Style.Type.COLOR, "light_purple", 2);
    private static final Style COLOR_RED = new Style(Style.Type.COLOR, "red", 2);
    private static final Style COLOR_AQUA = new Style(Style.Type.COLOR, "aqua", 2);
    private static final Style COLOR_GREEN = new Style(Style.Type.COLOR, "green", 2);
    private static final Style COLOR_BLUE = new Style(Style.Type.COLOR, "blue", 2);
    private static final Style COLOR_DARK_GRAY = new Style(Style.Type.COLOR, "dark_gray", 2);
    private static final Style COLOR_GRAY = new Style(Style.Type.COLOR, "gray", 2);
    private static final Style COLOR_GOLD = new Style(Style.Type.COLOR, "gold", 2);
    private static final Style COLOR_DARK_PURPLE = new Style(Style.Type.COLOR, "dark_purple", 2);
    private static final Style COLOR_DARK_RED = new Style(Style.Type.COLOR, "dark_red", 2);
    private static final Style COLOR_DARK_AQUA = new Style(Style.Type.COLOR, "dark_aqua", 2);
    private static final Style COLOR_DARK_GREEN = new Style(Style.Type.COLOR, "dark_green", 2);
    private static final Style COLOR_DARK_BLUE = new Style(Style.Type.COLOR, "dark_blue", 2);
    private static final Style COLOR_BLACK = new Style(Style.Type.COLOR, "black", 2);

    private static class Style {
        private enum Type {
            BOLD, ITALIC, UNDERLINE, STRIKE_THROUGH, OBFUSCATED, COLOR, FONT
        }

        Type type;
        String stringValue;
        int formatCodeLength;

        public Style(Type type, String stringValue, int formatCodeLength) {
            this.type = type;
            this.stringValue = stringValue;
            this.formatCodeLength = formatCodeLength;
        }
    }
}
