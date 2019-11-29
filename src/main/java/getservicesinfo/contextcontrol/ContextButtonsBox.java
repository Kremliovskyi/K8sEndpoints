package getservicesinfo.contextcontrol;

import getservicesinfo.configparser.ConfigParser;
import getservicesinfo.endpointcontrol.EndpointTable;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class ContextButtonsBox extends HBox {

    private static final String CONTEXT_SWITCH_COMMAND = "cmd /c kubectl config use-context %1$s";
    private List<ContextButton> contextButtons = new ArrayList<>();

    public ContextButtonsBox(EndpointTable endpointTable) {
        setAlignment(Pos.CENTER);
        initButtons(endpointTable);
    }

    private void initButtons(EndpointTable endpointTable) {
        ConfigParser.getInstance().getContextList().forEach(context -> {
            String contextName = context.getName();
            ContextButton contextButton = new ContextButton(contextName);
            if (contextName.equals(ConfigParser.getInstance().getCurrentContext())) {
                switchKubectlContext(contextName);
                buttonClicked(contextButton);
            }
            contextButton.setOnMouseClicked(e -> {
                if (!contextButton.isClicked()) {
                    buttonClicked(contextButton);
                    String currentContext = contextButton.getContextName();
                    endpointTable.refreshTable(currentContext);
                    switchKubectlContext(currentContext);
                }
            });
            getChildren().add(contextButton);
            contextButtons.add(contextButton);
        });
    }

    private void buttonClicked(ContextButton contextButton) {
        contextButtons.forEach(contextButton1 -> {
            contextButton1.getStyleClass().removeAll("buttonClicked");
            contextButton1.setClicked(false);
        });
        contextButton.getStyleClass().add("buttonClicked");
        contextButton.setClicked(true);
    }

    private void switchKubectlContext(String currentContext) {
//        try {
//            Runtime.getRuntime().exec(String.format(CONTEXT_SWITCH_COMMAND, currentContext));
//        } catch (IOException ex) {
//            Main.showAlert(ex.getMessage());
//        }
    }
}
