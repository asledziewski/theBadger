package dialogs;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

@SuppressWarnings("ALL")
public class AddMethod {

    public static String[] addMethod() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Add method.");
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
                "", "static"));
        staticFinal.getSelectionModel().selectFirst();

        TextField returnType = new TextField();
        returnType.setPromptText("int");

        TextField name = new TextField();
        name.setPromptText("methodName");

        TextField parameters = new TextField();
        parameters.setPromptText("int a, String b");

        TextArea body = new TextArea();
        body.setPromptText("int c = this.x * a;" + System.lineSeparator() + "return c;");

        grid.add(new Label("Access level: "), 0, 0);
        grid.add(accessLevel, 1, 0);
        grid.add(new Label("Modifiers:"), 0, 1);
        grid.add(staticFinal, 1, 1);
        grid.add(new Label("Return type:"), 0, 2);
        grid.add(returnType, 1, 2);
        grid.add(new Label("Name:"), 0, 3);
        grid.add(name, 1, 3);
        grid.add(new Label("Parameters:"), 0, 4);
        grid.add(parameters, 1, 4);
        grid.add(new Label("Body:"), 0, 5);
        grid.add(body, 1, 5);


        ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButton, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(grid);
        String res[] = new String[6];
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {

                res[0] = accessLevel.getSelectionModel().getSelectedItem().toString();
                res[1] = staticFinal.getSelectionModel().getSelectedItem().toString();
                res[2] = returnType.getText();
                res[3] = name.getText();
                res[4] = parameters.getText();
                res[5] = body.getText();
                return null;
            }
            return null;
        });
        dialog.showAndWait();
        return res;
    }
}
