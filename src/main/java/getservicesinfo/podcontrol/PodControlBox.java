package getservicesinfo.podcontrol;

import getservicesinfo.Main;
import getservicesinfo.kubernetes.Kube;
import getservicesinfo.logs.LogsStage;
import getservicesinfo.models.PodInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class PodControlBox extends HBox {

   private IPodsStage podsStage;

    public PodControlBox(IPodsStage podsStage) {
        this.podsStage = podsStage;
        setAlignment(Pos.CENTER);
        getChildren().addAll(addSearchLogsButton());
    }

    public PodInfo getSelectedPod() {
        return podsStage.getSelectedPod();
    }

    public Kube getKube() {
        return podsStage.getKube();
    }

    private Button addSearchLogsButton() {
        Button searchLogsButton = new Button("Search Logs");
        searchLogsButton.setOnMouseClicked(event -> {
            PodInfo selectedPod = podsStage.getSelectedPod();
            if (selectedPod != null) {
                new LogsStage(this).showStage();
            } else {
                Main.showAlert("Select Pod first");
            }
        });
        return searchLogsButton;
    }
}
