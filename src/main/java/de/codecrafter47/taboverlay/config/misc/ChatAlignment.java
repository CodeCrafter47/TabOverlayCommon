package de.codecrafter47.taboverlay.config.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChatAlignment {

    private static char COLOR_CHAR = '\u00a7';

    private static final String NON_UNICODE_CHARS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

    private static final int[] NON_UNICODE_CHAR_WIDTHS = new int[]{6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 6, 7, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6, 8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9, 8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1};

    private static final byte[] UNICODE_CHAR_WIDTHS = new byte[65536];

    static {
        InputStream resourceAsStream = ChatAlignment.class.getResourceAsStream("unicode.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                UNICODE_CHAR_WIDTHS[i++] = Byte.valueOf(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getCharWidth(int codePoint, boolean isBold) {
        int nonUnicodeIdx = NON_UNICODE_CHARS.indexOf(codePoint);
        double width;
        if (nonUnicodeIdx != -1) {
            width = NON_UNICODE_CHAR_WIDTHS[nonUnicodeIdx];
            if (isBold) {
                width += 1;
            }
        } else {
            // MC unicode -- what does this even do? but it's client-only so we can't use it directly :/
            int j = UNICODE_CHAR_WIDTHS[codePoint] >>> 4;
            int k = UNICODE_CHAR_WIDTHS[codePoint] & 15;

            if (k > 7) {
                k = 15;
                j = 0;
            }
            width = ((k + 1) - j) / 2 + 1;
            if (isBold) {
                width += 0.5;
            }
        }
        return width;
    }

    public static int legacyTextLength(String legacyText, char alternateColorChar) {
        double length = 0;
        boolean bold = false;
        for (int i = 0; i < legacyText.length(); ++i) {
            char c = legacyText.charAt(i);
            if (i + 1 < legacyText.length() && (c == COLOR_CHAR || (c == alternateColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(legacyText.charAt(i + 1)) > -1))) {
                c = legacyText.charAt(++i);
                if ("0123456789AaBbCcDdEeFfRr".indexOf(c) > -1) {
                    bold = false;
                } else if ("Ll".indexOf(c) > -1) {
                    bold = true;
                }
            } else {
                length += getCharWidth(c, bold);
            }
        }
        return (int) Math.ceil(length);
    }

    public static String cropLegacyText(String legacyText, char alternateColorChar, int maxLength) {
        StringBuilder result = new StringBuilder(legacyText.length());
        double length = 0;
        boolean bold = false;
        for (int i = 0; i < legacyText.length(); ++i) {
            char c = legacyText.charAt(i);
            if (i + 1 < legacyText.length() && (c == COLOR_CHAR || (c == alternateColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(legacyText.charAt(i + 1)) > -1))) {
                result.append(c);
                c = legacyText.charAt(++i);
                result.append(c);
                if ("0123456789AaBbCcDdEeFf".indexOf(c) > -1) {
                    bold = false;
                } else if ("Ll".indexOf(c) > -1) {
                    bold = true;
                }
            } else {
                length += getCharWidth(c, bold);
                if (length <= maxLength) {
                    result.append(c);
                } else {
                    break;
                }
            }
        }
        return result.toString();
    }
}
