package dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

@SuppressWarnings("ALL")
public class OverwriteConstructor {

    public static String overwriteConstructor() {
        Dialog dialog = new Dialog();
        dialog.setTitle("Overwrite constructor.");
        dialog.setHeaderText(null);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("badger.png"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 250, 10, 10));

        TextArea body = new TextArea();
        body.setPromptText("this.b = b;" + System.lineSeparator() + "int c = this.x * a;");

        grid.add(new Label("Body:"), 0, 0);
        grid.add(body, 1, 0);

        ButtonType applyButton = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButton, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(grid);
        final String[] res = new String[1];
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {
                res[0] = body.getText();
                return null;
            }
            return null;
        });
        dialog.showAndWait();
        return res[0];

    }
}
