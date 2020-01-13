package getservicesinfo.services;

import getservicesinfo.TableWithCopy;
import getservicesinfo.models.ServiceInfo;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Set;

public class ServicesTable extends TableWithCopy<ServiceInfo> {

    public void processServiceInfo(Set<ServiceInfo> podInfoSet) {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<ServiceInfo, String> column1 = new TableColumn<>("Service name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ServiceInfo, String> column2 = new TableColumn<>("External IP");
        column2.setCellValueFactory(new PropertyValueFactory<>("ip"));

        TableColumn<ServiceInfo, String> column3 = new TableColumn<>("Ports");
        column3.setCellValueFactory(new PropertyValueFactory<>("ports"));

        TableColumn<ServiceInfo, String> column4 = new TableColumn<>("Namespace");
        column4.setCellValueFactory(new PropertyValueFactory<>("serviceNameSpace"));

        TableColumn<ServiceInfo, String> column5 = new TableColumn<>("Created");
        column5.setCellValueFactory(new PropertyValueFactory<>("serviceCreationTimestamp"));

        ObservableList<TableColumn<ServiceInfo, ?>> columns = getColumns();
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);
        columns.add(column4);
        columns.add(column5);

        getItems().addAll(podInfoSet);
        autosize();
    }

    @Override
    public String fetchData(ObservableList<ServiceInfo> serviceInfos) {
        StringBuilder clipboardString = new StringBuilder();
        for (ServiceInfo serviceInfo : serviceInfos) {
            clipboardString.append(serviceInfo.getName()).append(" ").append(serviceInfo.getIp()).append(System.lineSeparator());
        }
        return clipboardString.toString();
    }
}
