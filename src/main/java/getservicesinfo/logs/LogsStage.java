package getservicesinfo.logs;

import getservicesinfo.Main;
import getservicesinfo.podcontrol.PodControlBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class LogsStage extends Stage {

    private PodControlBox podControlBox;
    private ProgressBar progressBar;
    private ToggleGroup radioGroup = new ToggleGroup();
    private TextField searchField;
    private TextField tailLinesField;
    private CheckBox enableGrep;
    private boolean isEqual;

    public LogsStage(PodControlBox podControlBox) {
        setTitle("Log options");
        setWidth(800);
        setHeight(300);
        this.podControlBox = podControlBox;
    }

    public void showStage() {
        progressBar = new ProgressBar(-1.0d);
        progressBar.setVisible(false);

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        initSearchField();

        initTailLinesField();

        CheckBox enableGrep = initGrepCheckBox();

        RadioButton isEqualButton = new RadioButton("Equal");
        isEqualButton.setSelected(true);
        isEqualButton.setToggleGroup(radioGroup);
        RadioButton containsButton = new RadioButton("Contains");
        containsButton.setToggleGroup(radioGroup);
        enableRadioButtons(false);

        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                isEqual = ((RadioButton)newValue).getText().equals("Equal"));

        Button searchLogsButton = initSearchLogsButton(datePicker);


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(datePicker, 0, 0);
        gridPane.add(tailLinesField, 1, 0);
        gridPane.add(enableGrep, 0, 1);
        gridPane.add(isEqualButton, 0, 2);
        gridPane.add(containsButton, 1, 2);
        gridPane.add(progressBar, 1, 2);
        gridPane.add(searchField, 0, 3);

        gridPane.add(searchLogsButton, 1, 4);

        Scene scene = new Scene(gridPane, 300, 240);
        scene.getStylesheets().add("styles.css");
        setScene(scene);
        show();
    }

    private void initSearchField() {
        searchField = new TextField();
        searchField.setPrefWidth(400);
        searchField.setDisable(true);
    }

    private CheckBox initGrepCheckBox() {
        enableGrep = new CheckBox("Search for specific log");
        enableGrep.setSelected(false);
        enableGrep.selectedProperty().addListener((observable, oldValue, newValue) -> {
            enableRadioButtons(newValue);
            searchField.setDisable(!newValue);
        });
        return enableGrep;
    }

    private Button initSearchLogsButton(DatePicker datePicker) {
        Button searchLogsButton = new Button("Search");
        searchLogsButton.setOnAction(action -> {
            LocalDate value = datePicker.getValue();
            Integer sinceSeconds = null;
            LocalDate now = LocalDate.now();
            if (value.equals(now) || value.isBefore(now)) {
                try {
                    sinceSeconds = Math.toIntExact(Instant.now().getEpochSecond() - value.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
                } catch (ArithmeticException e) {
                    Main.showAlert("Value too large. Logs will be shown since pod is created.");
                }
            }
            podControlBox.getKube().findPodLogs(podControlBox.getSelectedPod(), isEqual,
                    getSearchText(), sinceSeconds, getTailLines(), progressBar);
        });
        return searchLogsButton;
    }

    private void initTailLinesField() {
        tailLinesField = new TextField();
        tailLinesField.setPromptText("Tail lines");
        tailLinesField.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                char ch = tailLinesField.getText().charAt(oldValue.intValue());
                // Check if the new character is the number or other's
                if (!(ch >= '0' && ch <= '9' )) {
                    // if it's not number then just setText to previous one
                    tailLinesField.setText(tailLinesField.getText().substring(0,tailLinesField.getText().length()-1));
                }
            }
        });
    }

    private String getSearchText() {
        String result = searchField.getText();
        return result != null && !result.isEmpty() && enableGrep.isSelected() ? result : null;
    }

    private Integer getTailLines() {
        String result = tailLinesField.getText();
        return !result.isEmpty() ? Integer.valueOf(result) : null;
    }

    private void enableRadioButtons(boolean enable) {
        radioGroup.getToggles().forEach(toggle -> {
            Node node = (Node) toggle;
            node.setDisable(!enable);
        });
    }
}
