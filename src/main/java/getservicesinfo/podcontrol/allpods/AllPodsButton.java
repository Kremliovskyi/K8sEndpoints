package getservicesinfo.podcontrol.allpods;

import getservicesinfo.kubernetes.Kube;
import javafx.scene.control.Button;

public class AllPodsButton extends Button {

    private Kube kube;

    public AllPodsButton(Kube kube) {
        super(changeText(kube));
        this.kube = kube;
        setOnMouseClicked(event -> {
            AllPodsStage allPodsStage = new AllPodsStage(kube);
            allPodsStage.showAllPodsFrame();
        });
    }

    public void changeText() {
        setText(changeText(kube));
    }

    private static String changeText(Kube kube) {
        return String.format("Get All %s Pods", kube.getCurrentContext().toUpperCase());
    }
}
