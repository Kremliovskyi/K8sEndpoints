package getservicesinfo.services;

import getservicesinfo.kubernetes.Kube;
import getservicesinfo.models.PodInfo;
import getservicesinfo.models.ServiceInfo;
import getservicesinfo.podcontrol.PodsTable;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesStage extends Stage {

    private Kube kube;
    private ServicesTable servicesTable = new ServicesTable();

    public ServicesStage(Kube kube) {
        this.kube = kube;
    }

    public void showServicesFrame() {
        setTitle(kube.getCurrentContext().toUpperCase() + " Services");
        servicesTable.setDisable(true);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox progressBox = new VBox(progressIndicator);
        progressBox.setAlignment(Pos.CENTER);

        VBox.setVgrow(servicesTable, Priority.ALWAYS);

        StackPane root = new StackPane();
        root.getChildren().add(servicesTable);
        root.getChildren().add(progressBox);
        Scene scene = new Scene(root, 900, 400);
        scene.getStylesheets().add("styles.css");
        setScene(scene);
        show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Set<ServiceInfo> serviceInfoSet = kube.getServicesInfo();
            Platform.runLater(() -> {
                servicesTable.processServiceInfo(serviceInfoSet);
                root.getChildren().remove(1);
                servicesTable.setDisable(false);
                servicesTable.refresh();
            });
        });
    }
}
