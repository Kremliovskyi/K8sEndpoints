package getservicesinfo.endpointcontrol;

import getservicesinfo.Main;
import getservicesinfo.TableWithCopy;
import getservicesinfo.contextcontrol.OnContextChangeListener;
import getservicesinfo.kubernetes.Kube;
import getservicesinfo.models.Endpoint;
import getservicesinfo.podcontrol.PodsStage;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.concurrent.Executors;

public class EndpointTable extends TableWithCopy<Endpoint> {

    private Endpoint selectedEndpoint;
    private Kube kube;
    private PodsStage podsStage;
    private String currentContext;
    private OnEndpointsTableRefreshedListener tableRefreshedListener;
    private OnContextChangeListener contextChangeListener;

    public EndpointTable(Kube kube, OnEndpointsTableRefreshedListener tableRefreshedListener, OnContextChangeListener contextChangeListener) {
        this.kube = kube;
        this.currentContext = kube.getCurrentContext();
        this.tableRefreshedListener = tableRefreshedListener;
        this.contextChangeListener = contextChangeListener;
        setUpEndpointTable();
    }

    private void setUpEndpointTable() {
        podsStage = new PodsStage(kube);
        setPrefSize(600, 800);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Endpoint, String> column1 = new TableColumn<>("Endpoint name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Endpoint, String> column2 = new TableColumn<>("Version");
        column2.setCellValueFactory(new PropertyValueFactory<>("version"));

        ObservableList<TableColumn<Endpoint, ?>> columns = getColumns();
        columns.add(column1);
        columns.add(column2);
        setOnMouseClicked(event -> {
            selectedEndpoint = getSelectionModel().getSelectedItem();
        });

        getItems().addAll(kube.getEndpoints());
        autosize();
    }

    void refreshWithCurrentContext() {
        contextChangeListener.onContextChange(currentContext);
    }

    public void refreshTable(String context) {
        Executors.newSingleThreadExecutor().submit(() -> {
            selectedEndpoint = null;
            currentContext = context;
            getItems().clear();
            kube.resetEndpoints();
            try {
                kube.getEndpointInfo(context);
            } catch (Throwable e) {
                Main.showAlert(e.getMessage());
            }
            getItems().addAll(kube.getEndpoints());
            Platform.runLater(() -> {
                tableRefreshedListener.onEndpointsTableRefreshed();
            });
        });
    }

    void showPods() {
        if (selectedEndpoint != null) {
            podsStage.showPodsFrame(selectedEndpoint);
        } else {
            Main.showAlert("Select Endpoint first.");
        }
    }

    void restartEndpoint() {
        if (selectedEndpoint != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to restart " +
                    selectedEndpoint.getName() +"?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                kube.restartEndpoint(selectedEndpoint);
            }
        } else {
            Main.showAlert("Select Endpoint first.");
        }
    }

    @Override
    public String fetchData(ObservableList<Endpoint> posList) {
        StringBuilder clipboardString = new StringBuilder();
        for (Endpoint endpoint : posList) {
            clipboardString.append(endpoint.getName()).append(" ").append(endpoint.getVersion())
                    .append(System.lineSeparator());
        }
        return clipboardString.toString();
    }
}
