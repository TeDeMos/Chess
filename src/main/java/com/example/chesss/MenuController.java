package com.example.chesss;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.ChessServer;
import org.example.Colour;
import org.example.Game;
import org.example.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class MenuController {
    public Button loadSingle;
    public Button newSingle;
    public Button newMulti;
    public Button loadMulti;
    public Button joinMulti;
    public Label title;
    public VBox main;
    private ChessController controller;
    private Scene scene;

    public void newSingle(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Singleplayer Game");
        dialog.setHeaderText("Set player names");
        GridPane content = new GridPane();
        content.add(new Label("White player: "), 0, 0);
        content.add(new Label("Black player: "), 0, 1);
        TextField whiteName = new TextField();
        TextField blackName = new TextField();
        content.add(whiteName, 1, 0);
        content.add(blackName, 1, 1);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.CANCEL)
            return;
        Game game = new Game(whiteName.getText(), blackName.getText(), LocalDate.now());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        controller.setGame(game, Mode.LOCAL);
    }

    public void loadSingle(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load game file");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = chooser.showOpenDialog(scene.getWindow());
        if (file != null) {
            String content;
            try {
                content = Files.readString(file.toPath());
            } catch (IOException e) {
                return;
            }
            String[] split = content.split(";");
            Mode mode = Mode.valueOf(split[0]);
            String name1 = split[1];
            String name2 = split[2];
            Game game = new Game(name1, name2, LocalDate.now());
            String[] intMoves = split[3].split(":");
            for (String intMove : intMoves) {
                Move move = Move.fromString(intMove);
                game.makeTurn(move.x0(), move.y0(), move.x1(), move.y1());
            }
            String[] stringMoves = split[4].split(":");
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            controller.setGame(game, Mode.LOCAL);
            controller.setMoves(stringMoves);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Saving failed");
        alert.setContentText("Chosen file was incorrect");
        alert.showAndWait();
    }

    public void newMulti(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Multiplayer Game");
        dialog.setHeaderText("Set name and color");
        GridPane content = new GridPane();
        content.add(new Label("Name: "), 0, 0);
        content.add(new Label("Colour: "), 0, 1);
        TextField name = new TextField();
        ComboBox<String> colour = new ComboBox<>();
        colour.getItems().addAll("White", "Black");
        colour.getSelectionModel().selectFirst();
        content.add(name, 1, 0);
        content.add(colour, 1, 1);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.CANCEL)
            return;
        String hostName = name.getText();
        String hostColour = colour.getSelectionModel().getSelectedItem();
        ChessServer server = new ChessServer();
        Game game = new Game(whiteName.getText(), blackName.getText(), LocalDate.now());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        controller.setGame(game, Mode.LOCAL);
    }

    public void loadMulti(ActionEvent event) {
    }

    public void joinMulti(ActionEvent event) {
    }

    public void prepare(Scene s) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource("hello-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 880, 720);
        controller = fxmlLoader.getController();
        controller.prepare(scene);
        NumberBinding unit = Bindings.min(s.widthProperty(), s.heightProperty()).divide(10);
        main.spacingProperty().bind(unit.divide(5));
        title.prefWidthProperty().bind(unit.multiply(7));
        title.prefHeightProperty().bind(unit.multiply(3));
        StringExpression titleFontSize = Bindings.concat("-fx-font-size: ", unit.multiply(2), ";");
        StringExpression buttonFontSize = Bindings.concat("-fx-font-size: ", unit.divide(2), ";");
        title.styleProperty().bind(titleFontSize);
        Button[] buttons = {newSingle, loadSingle, newMulti, loadMulti, joinMulti};
        for (Button button : buttons) {
            button.prefWidthProperty().bind(unit.multiply(5));
            button.prefHeightProperty().bind(unit);
            button.styleProperty().bind(buttonFontSize);
        }
    }
}
