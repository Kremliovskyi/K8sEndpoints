package getservicesinfo.podcontrol.allpods;

import getservicesinfo.kubernetes.Kube;
import javafx.scene.control.Button;

public class AllPodsButton extends Button {

    public AllPodsButton(Kube kube) {
        super("All Pods");
        setOnMouseClicked(event -> {
            AllPodsStage allPodsStage = new AllPodsStage(kube);
            allPodsStage.showAllPodsFrame();
        });
    }
}
