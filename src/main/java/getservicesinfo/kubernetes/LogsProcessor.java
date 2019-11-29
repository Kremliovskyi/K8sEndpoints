package getservicesinfo.kubernetes;

import getservicesinfo.Main;
import getservicesinfo.models.PodInfo;
import io.kubernetes.client.ApiCallback;
import io.kubernetes.client.ApiException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogsProcessor implements ApiCallback<String> {

    private static final String CONTAINER_SELECTION_PART = "choose one of: [";
    private boolean isEqual;
    private String log;
    private ProgressBar progressBar;
    private Kube kube;
    private PodInfo podInfo;
    private Integer sinceSeconds;
    private Integer tailLines;

    LogsProcessor(ProgressBar progressBar, Kube kube) {
        this.progressBar = progressBar;
        this.kube = kube;
    }

    @Override
    public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
        progressBar.setVisible(false);
        String errorBody = e.getResponseBody();
        if (errorBody.contains(CONTAINER_SELECTION_PART)) {
            tryFetchLogsWithContainerFromException(errorBody);
        } else {
            Main.showAlert(e.getMessage());
        }
    }

    private void tryFetchLogsWithContainerFromException(String errorBody) {
        ButtonType[] buttonTypes;
        String tempPart = errorBody.substring(errorBody.indexOf(CONTAINER_SELECTION_PART));
        int closingBracketIndex = tempPart.indexOf("]");
        int openingBracketIndex = tempPart.indexOf("[");
        errorBody = tempPart.substring(openingBracketIndex + 1, closingBracketIndex);
        String[] containers = errorBody.split(" ");
        buttonTypes = new ButtonType[containers.length];
        for (int j = 0; j < buttonTypes.length; j++) {
            buttonTypes[j] = new ButtonType(containers[j]);
        }
        ButtonType result = getContainer(buttonTypes);
        podInfo.setSelectedContainer(result.getText());
        findPodLogs();
    }

    @Override
    public void onSuccess(String s, int i, Map<String, List<String>> map) {
        try {
            if (log != null) {
                Predicate<String> predicate = line -> line.contains(log);
                if (isEqual) {
                    predicate = line -> line.equals(log);
                }
                s = Arrays.stream(s.split("[\r\n]+")).filter(predicate).collect(Collectors.joining(System.lineSeparator()));
            }
            if (s != null && !s.isEmpty()) {
                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                File file = new File(tempDir, "k8s-pod.log");
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file), 1024)) {
                    bufferedWriter.write(s);
                } catch (IOException e) {
                    Main.showAlert("Temp file with log was not created");
                }
                if (file.exists()) {
                    Desktop dt = Desktop.getDesktop();
                    dt.open(file);
                }
            } else {
                Platform.runLater(() -> {
                    Main.showAlert("No records were found.");
                });
            }
        } catch (IOException e) {
            Main.showAlert(e.getMessage());
        } finally {
            progressBar.setVisible(false);
        }
    }

    @Override
    public void onUploadProgress(long l, long l1, boolean b) {

    }

    @Override
    public void onDownloadProgress(long l, long l1, boolean b) {

    }

    LogsProcessor setLogRequest(LogRequest logRequest){
        this.podInfo = logRequest.getPodInfo();
        this.sinceSeconds = logRequest.getSinceSeconds();
        this.tailLines = logRequest.getTailLines();
        this.isEqual = logRequest.isEqual();
        this.log = logRequest.getLog();
        return this;
    }

    void findPodLogs() {
        try {
            progressBar.setVisible(true);
            kube.getApi().readNamespacedPodLogAsync(podInfo.getName(), podInfo.getPodNameSpace(), getContainer(podInfo), null, null, "true",
                    null, sinceSeconds, tailLines, null, this);
        } catch (ApiException e) {
            Main.showAlert(e.getMessage());
        }
    }

    private String getContainer(PodInfo podInfo) {
        String selectedContainer = podInfo.getSelectedContainer();
        return selectedContainer != null && !selectedContainer.isEmpty() ? selectedContainer : null;
    }

    private ButtonType getContainer(ButtonType[] buttonTypes) {
        CountDownLatch waitForUser = new CountDownLatch(1);
        AtomicReference<ButtonType> result = new AtomicReference<>();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select container: ", buttonTypes);
            alert.showAndWait();
            result.set(alert.getResult());
            waitForUser.countDown();
        });
        try {
            waitForUser.await();
        } catch (InterruptedException e) {
            Main.showAlert(e.getMessage());
        }
        return result.get();
    }
}
