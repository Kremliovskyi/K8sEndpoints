package getservicesinfo;

import getservicesinfo.configfilechooser.ConfigFileStage;
import getservicesinfo.configfilechooser.UserPreferences;
import getservicesinfo.configparser.ConfigParser;
import getservicesinfo.contextcontrol.ContextButtonsBox;
import getservicesinfo.contextcontrol.OnContextChangeListener;
import getservicesinfo.endpointcontrol.EndpointControlBox;
import getservicesinfo.endpointcontrol.EndpointTable;
import getservicesinfo.endpointcontrol.OnEndpointsTableRefreshedListener;
import getservicesinfo.kubernetes.Kube;
import getservicesinfo.menu.ConfigFileMenu;
import getservicesinfo.podcontrol.allpods.AllPodsButton;
import getservicesinfo.services.ServicesButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executors;

public class Main extends Application implements OnContextChangeListener, OnEndpointsTableRefreshedListener {

    private StackPane root;
    private Stage mainStage;
    private boolean isShowing;
    private Kube kube;
    private AllPodsButton allPodsButton;
    private ServicesButton servicesButton;
    private EndpointTable endpointTable;

    public static void main(String[] args) {
        launch(args);
    }

    public static void showAlert(String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(text);
            alert.showAndWait();
        });
    }

    @Override
    public void start(Stage primaryStage) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String configLocation = new UserPreferences().getConfigFileLocation();

        File configFile;
        if (configLocation != null &&
                (configFile = new File(configLocation)).exists() &&
                ConfigParser.getInstance().setConfigFile(configFile)) {
            showMainStage(primaryStage);
        } else {
            showConfigFileStage();
        }
    }

    public void showMainStage() {
        showMainStage(new Stage(StageStyle.DECORATED));
    }

    public void reloadMainStage() {
        if (mainStage != null) {
            mainStage.hide();
        }
        showMainStage(new Stage(StageStyle.DECORATED));
    }

    public void showConfigFileStage() {
        ConfigFileStage configFileStage = new ConfigFileStage(this);
        configFileStage.showStage();
    }

    public void showProgressIndicator() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox box = new VBox(progressIndicator);
        box.setAlignment(Pos.CENTER);
        root.getChildren().add(1, box);
        root.getChildren().get(0).setDisable(true);
    }

    public void disableProgressIndicator() {
        root.getChildren().remove(1);
        root.getChildren().get(0).setDisable(false);
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void hideMainStage() {
        isShowing = false;
        mainStage.hide();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    private void showMainStage(Stage stage) {
        showSplash(stage);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                kube = new Kube(ConfigParser.getInstance().getCurrentContext());
                Platform.runLater(() -> {
                    ConfigFileMenu configFileMenu = new ConfigFileMenu(this);
                    endpointTable = new EndpointTable(kube, this, this);
                    ContextButtonsBox contextButtons = new ContextButtonsBox(this);
                    EndpointControlBox endpointControlBox = new EndpointControlBox(endpointTable);
                    mainStage = new Stage(StageStyle.DECORATED);
                    stage.hide();
                    allPodsButton = new AllPodsButton(kube);
                    servicesButton = new ServicesButton(kube);
                    HBox hBox = new HBox(allPodsButton, servicesButton, endpointControlBox);
                    hBox.setAlignment(Pos.BOTTOM_CENTER);
                    VBox vbox = new VBox(configFileMenu, contextButtons, endpointTable, hBox);
                    VBox.setVgrow(endpointTable, Priority.ALWAYS);
                    root = new StackPane();
                    root.getChildren().add(0, vbox);
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("styles.css");
                    mainStage.setScene(scene);
                    mainStage.setTitle("Services Info");
                    mainStage.show();
                    isShowing = true;
                    root.requestFocus();
                });
            } catch (Throwable e) {
                handleCrash(stage, e);
            }
        });
    }

    private void handleCrash(Stage stage, Throwable e) {
        Platform.runLater(() -> {
            stage.hide();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), new ButtonType("Refresh"), new ButtonType("Cancel"));
            alert.showAndWait();
            if (alert.getResult().getText().equals("Refresh")) {
                reloadMainStage();
            } else {
                System.exit(0);
            }
        });
    }

    private void showSplash(Stage initStage) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ImageView splash = new ImageView(new Image(Objects.requireNonNull(classLoader.getResourceAsStream("cropped-spin.gif"))));
        splash.setVisible(true);
        VBox splashLayout = new VBox();
        splashLayout.getChildren().add(splash);
        Scene splashScene = new Scene(splashLayout);
        splashScene.setFill(Color.TRANSPARENT);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setScene(splashScene);
        initStage.show();
    }

    @Override
    public void onContextChange(String context) {
        showProgressIndicator();
        endpointTable.refreshTable(context);
    }

    @Override
    public void onEndpointsTableRefreshed() {
        disableProgressIndicator();
        allPodsButton.changeText();
        servicesButton.changeText();
    }
}
