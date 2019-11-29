package getservicesinfo.podcontrol;

import getservicesinfo.Main;
import getservicesinfo.kubernetes.Kube;
import getservicesinfo.logs.LogsStage;
import getservicesinfo.models.PodInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;

public class PodControlBox extends HBox {

    private static final ButtonType[] EXTERNAL_GATEWAY_OPTIONS = new ButtonType[]{new ButtonType("service-gateway-external"), new ButtonType("service-gateway-external-ui")};
    private static final ButtonType[] INTERNAL_GATEWAY_OPTIONS = new ButtonType[]{new ButtonType("service-gateway-internal"), new ButtonType("service-gateway-internal-ui")};
    private PodsStage podsStage;

    PodControlBox(PodsStage podsStage) {
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
                if (selectedPod.getName().contains("service-gateway-external")) {
                    ButtonType result = getGatewayType(EXTERNAL_GATEWAY_OPTIONS, "service-gateway-external");
                    if (result != ButtonType.CLOSE) {
                        selectedPod.setSelectedContainer(result.getText());
                    }
                } else if (selectedPod.getName().contains("service-gateway-internal")) {
                    ButtonType result = getGatewayType(INTERNAL_GATEWAY_OPTIONS, "service-gateway-internal");
                    if (result != ButtonType.CLOSE) {
                        selectedPod.setSelectedContainer(result.getText());
                    }
                }
                new LogsStage(this).showStage();
            } else {
                Main.showAlert("Select Pod first");
            }
        });
        return searchLogsButton;
    }


    private ButtonType getGatewayType(ButtonType[] buttonTypes, String initialValue) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select one of gateway service type", buttonTypes);
        alert.showAndWait();
        return alert.getResult();
    }
}
