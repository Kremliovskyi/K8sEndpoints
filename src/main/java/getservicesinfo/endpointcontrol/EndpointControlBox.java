package getservicesinfo.endpointcontrol;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class EndpointControlBox extends HBox {

    public EndpointControlBox(EndpointTable endpointTable) {
        setAlignment(Pos.BOTTOM_CENTER);
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnMouseClicked(event -> {
            endpointTable.refreshWithCurrentContext();
        });
        Button infoButton = new Button("Info");
        infoButton.setOnMouseClicked(event -> {
            endpointTable.showPods();
        });
        Button restartEndpoint = new Button("Restart");
        restartEndpoint.setOnMouseClicked(event -> {
            endpointTable.restartEndpoint();
        });
        getChildren().addAll(refreshButton, infoButton, restartEndpoint);
    }
}
