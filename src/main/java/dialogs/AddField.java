package dialogs;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

@SuppressWarnings("ALL")
public class AddField {

    public static String[] addField() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Add field.");
        dialog.setHeaderText(null);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("badger.png"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 250, 10, 10));

        ChoiceBox accessLevel = new ChoiceBox(FXCollections.observableArrayList(
                "public", "protected", "private"));
        accessLevel.getSelectionModel().selectFirst();

        ChoiceBox staticFinal = new ChoiceBox(FXCollections.observableArrayList(
                "", "static", "final", "static final"));
        staticFinal.getSelectionModel().selectFirst();


        TextField type = new TextField();
        type.setPromptText("int");

        TextField name = new TextField();
        name.setPromptText("varName");

        TextField value = new TextField();
        value.setPromptText("3");

        grid.add(new Label("Access level: "), 0, 0);
        grid.add(accessLevel, 1, 0);
        grid.add(new Label("Modifier:"), 0, 1);
        grid.add(staticFinal, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(type, 1, 2);
        grid.add(new Label("Name:"), 0, 3);
        grid.add(name, 1, 3);
        grid.add(new Label("Value:"), 0, 4);
        grid.add(value, 1, 4);

        ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButton, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(grid);
        String res[] = new String[5];
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {

                res[0] = accessLevel.getSelectionModel().getSelectedItem().toString();
                res[1] = staticFinal.getSelectionModel().getSelectedItem().toString();
                res[2] = type.getText();
                res[3] = name.getText();
                res[4] = value.getText();
                if (res[4] == null) {
                    res[4] = "";
                } else {
                    res[4] = " = " + res[4];
                }
                return null;
            }
            return null;
        });
        dialog.showAndWait();
        return res;
    }
}
