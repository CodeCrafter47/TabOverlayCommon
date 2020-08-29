package de.codecrafter47.taboverlay.config.misc;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatFormatTest {

    @Test
    public void textHexColorCompatibility() {
        String json = ChatFormat.formattedTextToJson("§x§r§r§g§g§b§bTest");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#rrggbb\"}]}", json);
    }

    @Test
    public void textHexColorOption2() {
        String json = ChatFormat.formattedTextToJson("&#012345Test");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#012345\"}]}", json);
    }

    @Test
    public void textHexColorCMI() {
        String json = ChatFormat.formattedTextToJson("{#012345}Test");
        assertEquals("{\"text\":\"\",\"extra\":[{\"text\":\"Test\",\"color\":\"#012345\"}]}", json);
    }
}