package getservicesinfo.kubernetes;

import getservicesinfo.Main;
import io.kubernetes.client.ApiCallback;
import io.kubernetes.client.ApiException;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PodCallBack implements ApiCallback<String> {

    private boolean isEqual;
    private String log;
    private ProgressBar progressBar;

    PodCallBack(boolean isEqual, String log, ProgressBar progressBar) {
        this.isEqual = isEqual;
        this.log = log;
        this.progressBar = progressBar;
    }

    @Override
    public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
        progressBar.setVisible(false);
        Main.showAlert(e.getMessage());
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
            if (!s.isEmpty()) {
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
}
