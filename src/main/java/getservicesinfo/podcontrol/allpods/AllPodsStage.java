package getservicesinfo.podcontrol.allpods;

import getservicesinfo.kubernetes.Kube;
import getservicesinfo.models.PodInfo;
import getservicesinfo.podcontrol.IPodsStage;
import getservicesinfo.podcontrol.PodControlBox;
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

public class AllPodsStage extends Stage implements IPodsStage {

    private Kube kube;
    private PodsTable podsTable = new PodsTable();

    public AllPodsStage(Kube kube) {
        this.kube = kube;
    }

    public void showAllPodsFrame() {
        setTitle("All Pods");
        podsTable.setDisable(true);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox progressBox = new VBox(progressIndicator);
        progressBox.setAlignment(Pos.CENTER);

        PodControlBox podControlBox = new PodControlBox(this);
        podControlBox.setDisable(true);
        VBox podsBox = new VBox(podsTable, podControlBox);
        VBox.setVgrow(podsTable, Priority.ALWAYS);
        podsBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.getChildren().add(podsBox);
        root.getChildren().add(progressBox);
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
                podControlBox.setDisable(false);
                podsTable.setDisable(false);
                podsTable.refresh();
            });
        });
    }

    @Override
    public Kube getKube() {
        return kube;
    }

    @Override
    public PodInfo getSelectedPod() {
        return podsTable.getSelectedPod();
    }
}
