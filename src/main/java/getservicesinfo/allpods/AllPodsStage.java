package getservicesinfo.allpods;

import getservicesinfo.kubernetes.Kube;
import getservicesinfo.models.PodInfo;
import getservicesinfo.podcontrol.PodsTable;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllPodsStage extends Stage {

    private Kube kube;

    public AllPodsStage(Kube kube) {
        this.kube = kube;
    }

    public void showAllPodsFrame() {
        setTitle("All Pods");
        PodsTable podsTable = new PodsTable();
        podsTable.setDisable(true);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox box = new VBox(progressIndicator);
        box.setAlignment(Pos.CENTER);
        StackPane root = new StackPane();
        root.getChildren().add(podsTable);
        root.getChildren().add(box);
        Scene scene = new Scene(root, 900, 400);
        scene.getStylesheets().add("styles.css");
        setScene(scene);
        show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            Set<PodInfo> podInfoSet = kube.getAllPodsInfo();
            Platform.runLater(() -> {
                podsTable.processPodInfo(podInfoSet);
                root.getChildren().remove(1);
                podsTable.setDisable(false);
                podsTable.refresh();
            });
        });
    }
}
