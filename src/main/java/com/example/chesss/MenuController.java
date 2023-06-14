package com.example.chesss;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.Colour;
import org.example.Game;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
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
        controller.startLocal(game);
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
            controller.startLocalMoves(game, stringMoves);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Saving failed");
        alert.setContentText("Chosen file was incorrect");
        alert.showAndWait();
    }

    public void newMulti(ActionEvent event) throws InterruptedException {
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
        Colour hostColour = Colour.valueOf(colour.getSelectionModel().getSelectedItem().toUpperCase());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Waiting");
        alert.setHeaderText("Client joined");
        alert.setContentText("You can close this window");
        alert.show();
        Server server = new Server(hostName, hostColour, controller);
        Game game;
        if (hostColour == Colour.WHITE)
            game = new Game(hostName, server.clientName, LocalDate.now());
        else
            game = new Game(server.clientName, hostName, LocalDate.now());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        controller.startHost(server, game, hostColour);
    }

    public void loadMulti(ActionEvent event) {
    }

    public void joinMulti(ActionEvent event) {
        Client client = new Client();
        if (client.firstMessage.startsWith("name")) {
            String[] split = client.firstMessage.split(";");
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Join multiplayer game");
            String headerMessage = "Set name (you're %s)".formatted(split[1]);
            dialog.setHeaderText(headerMessage);
            GridPane content = new GridPane();
            content.add(new Label("Name: "), 0, 0);
            TextField name = new TextField();
            content.add(name, 1, 0);
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() == ButtonType.CANCEL)
                return;
            Game game;
            if (split[1].equals("white"))
                game = new Game(name.getText(), split[2], LocalDate.now());
            else
                game = new Game(split[2], name.getText(), LocalDate.now());
            try {
                client.send(name.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            Colour clientColour = split[1].equals("white") ? Colour.WHITE : Colour.BLACK;
            controller.startClient(client, game, clientColour);
        }
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
