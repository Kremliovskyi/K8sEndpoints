package getservicesinfo.configfilechooser;

import getservicesinfo.Main;
import getservicesinfo.configparser.ConfigParser;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ConfigFileStage extends Stage {

    private File configFile;
    private Main main;
    private String manualFilePath;

    public ConfigFileStage(Main main) {
        this.main = main;
    }

    public void showStage() {
        setTitle("Choose config file");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        VBox vBox = new VBox();

        ObservableList<Node> children =  vBox.getChildren();


        Label explanation = new Label();
        explanation.setText("Please select Kubernetes config file.");
        children.add(explanation);

        Label configPathText = new Label();
        configPathText.setText("Config file path:");

        TextField input = new TextField();
        input.setPrefWidth(400L);
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            manualFilePath = newValue;
        });

        Button browseButton = new Button("Browse");
        browseButton.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            configFile = fileChooser.showOpenDialog(this);
            if (configFile != null) {
                manualFilePath = null;
                input.setText(configFile.getPath());
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnMouseClicked(event -> {
            if (manualFilePath != null) {
                configFile = new File(manualFilePath);
            }
            if (configFile != null && configFile.exists() && ConfigParser.getInstance().setConfigFile(configFile)) {
                new UserPreferences().saveConfigFileLocation(configFile);
                if (main.isShowing()) {
                    main.reloadMainStage();
                } else {
                    main.showMainStage();
                }
                close();
            }
        });

        gridPane.add(configPathText, 0, 0);
        gridPane.add(input, 1, 0);
        gridPane.add(browseButton, 2, 0);
        gridPane.add(submitButton, 1, 1);
        children.add(gridPane);
        VBox.setMargin(explanation, new Insets(20, 0, 20,20));
        VBox.setMargin(gridPane, new Insets(0, 0, 0,20));
        Scene scene = new Scene(vBox, 750, 200);
        scene.getStylesheets().add("styles.css");
        setScene(scene);
        show();
    }

    public File getConfigFile() {
        return configFile;
    }
}
