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
        String names = newSingleplayerDialog();
        if (names.isEmpty())
            return;
        String[] split = names.split(";");
        Game game = new Game(split[0], split[1], LocalDate.now());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        controller.startLocal(game);
    }

    public void loadSingle(ActionEvent event) {
        String content = showLoadGameDialog();
        Game game = loadGame(content);
        if (game == null) {
            showLoadingError();
            return;
        }
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        controller.startLocal(game);
    }

    public void newMulti(ActionEvent event) {
        String nameAndColor = newMultiplayerDialog();
        if (nameAndColor.isEmpty())
            return;
        String[] split = nameAndColor.split(";");
        String hostName = split[0];
        Colour hostColour = Colour.valueOf(split[1]);
        showClientWaitingAlert();
        Server server;
        try {
            server = Server.newGame(hostName, hostColour, controller);
        } catch (IOException ignored) {
            showNetworkError();
            return;
        }
        Game game;
        if (hostColour == Colour.WHITE)
            game = new Game(hostName, server.clientName, LocalDate.now());
        else
            game = new Game(server.clientName, hostName, LocalDate.now());
        controller.startHost(server, game, hostColour);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
    }

    public void loadMulti(ActionEvent event) {
        String content = showLoadGameDialog();
        Game game = loadGame(content);
        if (game == null) {
            showLoadingError();
            return;
        }
        Colour hostColour = Colour.valueOf(loadMultiplayerDialog(game.player1.getName(), game.player2.getName()));
        showClientWaitingAlert();
        Server server;
        try {
            server = Server.loadGame(content, hostColour, controller);
        } catch (IOException e) {
            showNetworkError();
            return;
        }
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        controller.startHost(server, game, hostColour);
    }

    public void joinMulti(ActionEvent event) {
        Client client;
        try {
            client = new Client();
        } catch (IOException e) {
            showNetworkError();
            return;
        }
        String[] split = client.firstMessage.split("\\|");
        if (split[0].equals("name")) {
            Colour guestColour = Colour.valueOf(split[1]);
            if (guestColour == Colour.WHITE)
                guestColour = Colour.BLACK;
            else
                guestColour = Colour.WHITE;
            String hostName = split[2];
            String guestName = joinMultiplayerDialog(guestColour);
            if (guestName.isEmpty())
                return;
            try {
                client.send(guestName);
            } catch (IOException e) {
                showNetworkError();
                return;
            }
            Game game;
            if (guestColour == Colour.WHITE)
                game = new Game(guestName, hostName, LocalDate.now());
            else
                game = new Game(hostName, guestName, LocalDate.now());
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            controller.startClient(client, game, guestColour);
        } else {
            Colour guestColour = Colour.valueOf(split[1]);
            if (guestColour == Colour.WHITE)
                guestColour = Colour.BLACK;
            else
                guestColour = Colour.WHITE;
            Game game = loadGame(split[2]);
            showGameJoinAlert(guestColour == Colour.WHITE ? game.player1.getName() : game.player2.getName(),
                    guestColour);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            controller.startClient(client, game, guestColour);
        }
    }

    private String newSingleplayerDialog() {
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
            return "";
        return "%s;%s".formatted(whiteName.getText(), blackName.getText());
    }

    private String showLoadGameDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load game file");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = chooser.showOpenDialog(scene.getWindow());
        try {
            return Files.readString(file.toPath());
        } catch (IOException | NullPointerException e) {
            return "";
        }
    }

    private Game loadGame(String content) {
        try {
            String[] split = content.split(";");
            String name1 = split[0];
            String name2 = split[1];
            Game game = new Game(name1, name2, LocalDate.now());
            String[] intMoves = split[2].split(":");
            for (String intMove : intMoves) {
                Move move = Move.fromString(intMove);
                game.makeTurn(move.x0(), move.y0(), move.x1(), move.y1());
            }
            return game;
        } catch (IllegalArgumentException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String newMultiplayerDialog() {
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
            return "";
        return "%s;%s".formatted(name.getText(), colour.getSelectionModel().getSelectedItem().toUpperCase());
    }

    private String loadMultiplayerDialog(String whiteName, String blackName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Load Multiplayer Game");
        dialog.setHeaderText("Which player are you?");
        GridPane content = new GridPane();
        ComboBox<String> player = new ComboBox<>();
        String option1 = "%s: White".formatted(whiteName);
        String option2 = "%s: Black".formatted(blackName);
        player.getItems().addAll(option1, option2);
        player.getSelectionModel().selectFirst();
        content.add(player, 0, 0);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.CANCEL)
            return "";
        if (player.getSelectionModel().getSelectedItem().equals(option1))
            return "WHITE";
        return "BLACK";
    }

    private String joinMultiplayerDialog(Colour guestColour) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Join multiplayer game");
        String headerMessage = "Set name (you're %s)".formatted(guestColour.displayName());
        dialog.setHeaderText(headerMessage);
        GridPane content = new GridPane();
        content.add(new Label("Name: "), 0, 0);
        TextField name = new TextField();
        content.add(name, 1, 0);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK/*, ButtonType.CANCEL*/);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.CANCEL)
            return "";
        return name.getText();
    }

    private void showLoadingError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Loading failed");
        alert.setContentText("Chosen file was incorrect");
        alert.showAndWait();
    }

    private void showClientWaitingAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Waiting");
        alert.setHeaderText("Client joined");
        alert.setContentText("You can close this window");
        alert.show();
    }

    private void showNetworkError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Joining failed");
        alert.setContentText("A network error occurred");
        alert.showAndWait();
    }

    private void showGameJoinAlert(String name, Colour colour) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Joined game");
        alert.setHeaderText("You joined an existing game");
        alert.setContentText("You're playing as %s with $s".formatted(name, colour.displayName()));
        alert.show();
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
