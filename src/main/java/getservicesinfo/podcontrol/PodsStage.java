package getservicesinfo.podcontrol;

import getservicesinfo.kubernetes.Kube;
import getservicesinfo.models.Endpoint;
import getservicesinfo.models.PodInfo;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import java.util.Set;

public class PodsStage extends Stage {

    private Kube kube;
    private Set<PodInfo> podInfoSet;
    private PodsTable podsTable;

    public PodsStage(Kube kube) {
        this.kube = kube;
    }

    public void showPodsFrame(@Nonnull Endpoint selectedEndpoint) {
        setTitle("Pods");
        kube.getEndpoints().stream()
                .filter(endpoint -> endpoint.getName().equals(selectedEndpoint.getName()))
                .findFirst()
                .ifPresent(endpoint -> {
                    podInfoSet = endpoint.getPods();
                });
        podsTable = new PodsTable(podInfoSet);

        PodControlBox podControlBox = new PodControlBox(this);

        VBox vBox = new VBox(podsTable, podControlBox);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 900, 400);
        scene.getStylesheets().add("styles.css");
        setScene(scene);
        show();
    }

    public PodInfo getSelectedPod() {
        return podsTable.getSelectedPod();
    }

    public Kube getKube() {
        return kube;
    }
}
