package dialogs;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

@SuppressWarnings("ALL")
public class AddConstructor {

    public static String[] addConstructor() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Add constructor.");
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
        //  username.setPromptText("package.subpackage.className");

        TextField parameters = new TextField();
        parameters.setPromptText("int a, String b");

        TextArea body = new TextArea();
        body.setPromptText("this.b = b;" + System.lineSeparator() + "int c = this.x * a;");

        grid.add(new Label("Access level: "), 0, 0);
        grid.add(accessLevel, 1, 0);
        grid.add(new Label("Parameters:"), 0, 1);
        grid.add(parameters, 1, 1);
        grid.add(new Label("Body:"), 0, 2);
        grid.add(body, 1, 2);

        ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButton, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(grid);
        String res[] = new String[3];
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {
                res[0] = accessLevel.getSelectionModel().getSelectedItem().toString();
                res[1] = parameters.getText();
                res[2] = body.getText();
                return null;
            }
            return null;
        });
        dialog.showAndWait();
        return res;
    }
}
