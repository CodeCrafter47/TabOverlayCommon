package de.codecrafter47.taboverlay.config.misc;

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import org.yaml.snakeyaml.error.Mark;

import java.awt.*;

@Getter
public class TextColor {

    public static final TextColor COLOR_WHITE = new TextColor(255, 255, 255, "&f");
    public static final TextColor COLOR_YELLOW = new TextColor(255, 255, 85, "&e");
    public static final TextColor COLOR_LIGHT_PURPLE = new TextColor(255, 85, 255, "&d");
    public static final TextColor COLOR_RED = new TextColor(255, 85, 85, "&c");
    public static final TextColor COLOR_AQUA = new TextColor(85, 255, 255, "&b");
    public static final TextColor COLOR_GREEN = new TextColor(85, 255, 85, "&a");
    public static final TextColor COLOR_BLUE = new TextColor(85, 85, 255, "&9");
    public static final TextColor COLOR_DARK_GRAY = new TextColor(85, 85, 85, "&8");
    public static final TextColor COLOR_GRAY = new TextColor(170, 170, 170, "&7");
    public static final TextColor COLOR_GOLD = new TextColor(255, 170, 0, "&6");
    public static final TextColor COLOR_DARK_PURPLE = new TextColor(170, 0, 170, "&5");
    public static final TextColor COLOR_DARK_RED = new TextColor(170, 0, 0, "&4");
    public static final TextColor COLOR_DARK_AQUA = new TextColor(0, 170, 170, "&3");
    public static final TextColor COLOR_DARK_GREEN = new TextColor(0, 170, 0, "&2");
    public static final TextColor COLOR_DARK_BLUE = new TextColor(0, 0, 170, "&1");
    public static final TextColor COLOR_BLACK = new TextColor(0, 0, 0, "&0");

    public static TextColor parse(String color, TemplateCreationContext tcc, Mark mark) {
        char c;
        if (color.length() == 2
                && ((c = color.charAt(0)) == '&'
                || c == '\u00a7'
                || c == '#')) {
            switch (color.charAt(1)) {
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
            }
        }
        if (color.matches("#[a-fA-F0-9]{6}")) {
            int rgb = Integer.parseInt( color.substring( 1 ), 16 );
            return new TextColor((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
        }
        if (color.matches("&#[a-fA-F0-9]{6}")) {
            int rgb = Integer.parseInt( color.substring( 2 ), 16 );
            return new TextColor((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
        }
        tcc.getErrorHandler().addWarning("Specified color " + color + " does not match expected format.\n" +
                "Expected a formatting code (e.g. &7) or a hex color (e.g. #012345).", mark);
        return COLOR_WHITE;
    }

    public static TextColor interpolateLinear(TextColor a, TextColor b, double x) {
        double rb = x;
        double ra = 1 - rb;
        return new TextColor((int) (ra * a.getR() + rb * b.getR()),
                (int) (ra * a.getG() + rb * b.getG()),
                (int) (ra * a.getB() + rb * b.getB()));
    }

    public static TextColor interpolateSine(TextColor a, TextColor b, double x) {
        double rb = Math.sin((x - 0.5) * Math.PI) * 0.5 + 0.5;
        double ra = 1 - rb;
        return new TextColor((int) (ra * a.getR() + rb * b.getR()),
                (int) (ra * a.getG() + rb * b.getG()),
                (int) (ra * a.getB() + rb * b.getB()));
    }

    private final int r, g, b;
    private final String formatCode;

    public TextColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.formatCode = "&#" + Integer.toHexString(255 << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255)).substring(2);
    }

    private TextColor(int r, int g, int b, String formatCode) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.formatCode = formatCode;
    }
}
