package getservicesinfo.podcontrol;

import getservicesinfo.TableWithCopy;
import getservicesinfo.models.PodInfo;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Set;

public class PodsTable extends TableWithCopy<PodInfo> {

    private PodInfo selectedPod;

    public PodsTable() {
    }

    public PodsTable(Set<PodInfo> podInfoSet) {
        processPodInfo(podInfoSet);
    }

    public void processPodInfo(Set<PodInfo> podInfoSet) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<PodInfo, String> column1 = new TableColumn<>("Pod name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PodInfo, String> column2 = new TableColumn<>("IP");
        column2.setCellValueFactory(new PropertyValueFactory<>("ip"));

        TableColumn<PodInfo, String> column3 = new TableColumn<>("Ports");
        column3.setCellValueFactory(new PropertyValueFactory<>("ports"));

        TableColumn<PodInfo, String> column4 = new TableColumn<>("Namespace");
        column4.setCellValueFactory(new PropertyValueFactory<>("podNameSpace"));

        TableColumn<PodInfo, String> column5 = new TableColumn<>("Created");
        column5.setCellValueFactory(new PropertyValueFactory<>("podCreationTimestamp"));

        TableColumn<PodInfo, String> column6 = new TableColumn<>("Status");
        column6.setCellValueFactory(new PropertyValueFactory<>("phase"));

        ObservableList<TableColumn<PodInfo, ?>> columns = getColumns();
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);
        columns.add(column4);
        columns.add(column5);
        columns.add(column6);
        setOnMouseClicked(event -> {
            selectedPod = getSelectionModel().getSelectedItem();
        });

        getItems().addAll(podInfoSet);
        autosize();
    }

    public PodInfo getSelectedPod() {
        return selectedPod;
    }

    @Override
    public String fetchData(ObservableList<PodInfo> posList) {
        StringBuilder clipboardString = new StringBuilder();
        for (PodInfo podInfo : posList) {
            clipboardString.append(podInfo.getName()).append(" ").append(podInfo.getIp()).append(System.lineSeparator());
        }
        return clipboardString.toString();
    }
}
