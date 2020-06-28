package de.codecrafter47.taboverlay.config.misc;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatFormatTest {

    @Test
    public void textHexColorCompatibility() {
        String json = ChatFormat.formattedTextToJson("§x§r§r§g§g§b§bTest");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#rrggbb\"}]}", json);
    }
}