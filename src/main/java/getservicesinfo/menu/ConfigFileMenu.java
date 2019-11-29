package getservicesinfo.menu;

import getservicesinfo.Main;
import getservicesinfo.configfilechooser.UserPreferences;
import getservicesinfo.configparser.ConfigParser;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class ConfigFileMenu extends MenuBar {

    public ConfigFileMenu(Main main) {
        Menu configFileLocationMenu = new Menu("Config file");
        MenuItem newConfigFileMenuItem = new MenuItem("Select new config file");
        newConfigFileMenuItem.setOnAction(event -> {
            main.showConfigFileStage();
        });
        MenuItem reloadConfig = new MenuItem("Reload existing config file");
        reloadConfig.setOnAction(event -> {
            ConfigParser.getInstance().readConfigFile();
            main.reloadMainStage();
        });
        MenuItem forgetConfigFilePath = new MenuItem("Forget config file path");
        forgetConfigFilePath.setOnAction(event -> {
            ConfigParser.getInstance().forgetConfigFile();
            new UserPreferences().removeConfigFileLocation();
            main.hideMainStage();
            main.showConfigFileStage();
        });
        configFileLocationMenu.getItems().addAll(reloadConfig, newConfigFileMenuItem, forgetConfigFilePath);
        getMenus().add(configFileLocationMenu);
    }

    private ConfigFileMenu(Menu... menus) {
        super(menus);
    }
}
