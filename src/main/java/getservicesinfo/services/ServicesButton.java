package getservicesinfo.services;

import getservicesinfo.kubernetes.Kube;
import javafx.scene.control.Button;

public class ServicesButton extends Button {

    private Kube kube;

    public ServicesButton(Kube kube) {
        super(String.format("Get %s Services", kube.getCurrentContext().toUpperCase()));
        this.kube = kube;
        setOnMouseClicked(event -> {
            ServicesStage servicesStage = new ServicesStage(kube);
            servicesStage.showServicesFrame();
        });
    }

    public void changeText() {
        setText(changeText(kube));
    }

    private static String changeText(Kube kube) {
        return String.format("Get %s Services", kube.getCurrentContext().toUpperCase());
    }
}