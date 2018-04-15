package de.codecrafter47.taboverlay.config;

import de.codecrafter47.taboverlay.config.dsl.AbstractTabOverlayTemplateConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.*;

public class ConfigurationDSLTest {

    private Yaml yaml;

    @Before
    public void setUp() throws Exception {
        yaml = ConfigTabOverlayManager.constructYamlInstance(ConfigTabOverlayManager.Options.createBuilderWithDefaults().build());
    }

    @Test
    public void testSimpleConfig1() {
        yaml.loadAs("showTo: all\n" +
                "priority: 1\n" +
                "\n" +
                "showHeaderFooter: false\n" +
                "\n" +
                "playerSets: {} # none yet\n" +
                "\n" +
                "type: FIXED_SIZE\n" +
                "\n" +
                "size: 80\n" +
                "\n" +
                "defaultIcon: colors/black.png\n" +
                "defaultPing: 1000\n" +
                "\n" +
                "components:\n" +
                "- 'Slot 1'\n" +
                "- 'Slot 2'\n" +
                "- 'Slot 3'\n" +
                "- 'Slot 4'\n" +
                "- 'Slot 5'\n" +
                "- 'Slot 6'\n" +
                "- 'Slot 7'\n" +
                "- 'Slot 8'\n" +
                "- 'Slot 9'\n" +
                "- 'Slot 10'",
                AbstractTabOverlayTemplateConfiguration.class);
    }

    @Test
    public void testSimpleConfig2() {
        Object load = yaml.loadAs("showTo: all\n" +
                "priority: 1\n" +
                "\n" +
                "showHeaderFooter: false\n" +
                "\n" +
                "playerSets:\n" +
                "  all_players:\n" +
                "    filter: all\n" +
                "\n" +
                "type: FIXED_SIZE\n" +
                "\n" +
                "size: 80\n" +
                "\n" +
                "defaultIcon: colors/black.png\n" +
                "defaultPing: 1000\n" +
                "\n" +
                "components:\n" +
                "- '&aGreen'\n" +
                "- '&bBlue'\n" +
                "- '&cRed'\n" +
                "- '&eYellow'\n" +
                "- 'Players: ${playerset:all_players size}'\n" +
                "- 'Ping: ${viewer ping}'\n" +
                "- 'Server: ${viewer server}'",
                AbstractTabOverlayTemplateConfiguration.class);
    }

    @Test
    public void testSimpleConfig3() {
        yaml.loadAs("showTo: all\n" +
                "priority: 1\n" +
                "\n" +
                "showHeaderFooter: false\n" +
                "\n" +
                "playerSets:\n" +
                "  all_players:\n" +
                "    filter: all\n" +
                "\n" +
                "type: FIXED_SIZE\n" +
                "\n" +
                "size: 80\n" +
                "\n" +
                "defaultIcon: colors/black.png\n" +
                "defaultPing: 1000\n" +
                "\n" +
                "components:\n" +
                "- {text: '&aGreen', icon: 'colors/green.png'}\n" +
                "- {text: '&bBlue', icon: 'colors/aqua.png'}\n" +
                "- {text: '&cRed', icon: 'colors/red.png'}\n" +
                "- {text: '&eYellow', icon: 'colors/yellow.png'}\n" +
                "- 'Players: ${playerset:all_players size}'\n" +
                "- {text: 'Ping: ${viewer ping}', ping: '${viewer ping}'}\n" +
                "- {text: 'Server: ${viewer server}', icon: 'default/server.png', ping: 0}",
                AbstractTabOverlayTemplateConfiguration.class);
    }

    @Test
    public void testSimpleConfig4() {
        yaml.loadAs("showTo: all\n" +
                "priority: 1\n" +
                "\n" +
                "showHeaderFooter: false\n" +
                "\n" +
                "playerSets:\n" +
                "  all_players:\n" +
                "    filter: all\n" +
                "\n" +
                "type: FIXED_SIZE\n" +
                "\n" +
                "size: 80\n" +
                "\n" +
                "defaultIcon: colors/black.png\n" +
                "defaultPing: 1000\n" +
                "\n" +
                "components:\n" +
                "- {text: 'Slot 1', alignment: LEFT}\n" +
                "- {text: 'Slot 2', alignment: CENTER}\n" +
                "- {text: 'Slot 3', alignment: CENTER}\n" +
                "- {text: 'Slot 4', alignment: RIGHT}",
                AbstractTabOverlayTemplateConfiguration.class);
    }
}