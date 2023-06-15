package com.example.chesss;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Optional;

public class ChessController {
    @FXML
    public HBox main;
    public VBox left;
    public VBox right;
    public HBox topPlayer;
    public GridPane board;
    public HBox bottomPlayer;
    public StackPane pane;
    public ImageView floating;
    public Label topPlayerName;
    public Label bottomPlayerName;
    public GridPane topPlayerTaken;
    public GridPane bottomPlayerTaken;
    public Label topClock;
    public Label bottomClock;
    public Pane topClockBackground;
    public Pane bottomClockBackground;
    public Button resign;
    public Button draw;
    public Button save;
    public ListView<String> moves;

    private Scene scene;
    private StackPane[][] boardPanes;
    private ImageView[] topTakenDisplay;
    private ImageView[] bottomTakenDisplay;
    private EnumMap<PieceType, ImageView>[][] piecesViews;
    private Game game;
    private int xBoardStart;
    private int yBoardStart;
    private boolean moving;
    private Mode mode;
    private Colour colour;
    private boolean flipped;
    private NetworkObject network;
    private Mode backup;

    ReadOnlyDoubleProperty width;
    ReadOnlyDoubleProperty height;

    public void startLocal(Game game) {
        this.game = game;
        flipped = false;
        mode = Mode.LOCAL;
        moves.setItems(game.getMovesDisplay());
        refresh();
    }

    public void startOnline(NetworkObject network, Game game, Colour colour) {
        this.network = network;
        this.game = game;
        this.colour = colour;
        flipped = colour == Colour.BLACK;
        if (flipped)
            flipBoard();
        mode = Mode.ONLINE;
        refresh();
        moves.setItems(game.getMovesDisplay());
        network.start();
    }

    public void refresh() {
        showBoard(game.getBoard());
        showPlayers(game);
        showTimer(game);
        moves.refresh();
    }

    private void showPiece(PieceType piece, int x, int y) {
        for (PieceType p : PieceType.values())
            piecesViews[x][y].get(p).visibleProperty().set(false);
        if (piece != null)
            piecesViews[x][y].get(piece).visibleProperty().set(true);
    }

    private int getRealX(int x) {
        return flipped ? 7 - x : x;
    }

    private int getRealY(int y) {
        return flipped ? y : 7 - y;
    }

    public void showBoard(Board board) {
        for (int i = 0; i < 8; i++) {
            int realX = getRealX(i);
            for (int j = 0; j < 8; j++) {
                int realY = getRealY(j);
                Square square = board.getSquare(realX, realY);
                showPiece(PieceType.fromPiece(square.getPiece()), i, j);
            }
        }
    }

    public void showPlayers(Game game) {
        Player top, bottom;
        if (flipped) {
            top = game.player1;
            bottom = game.player2;
        } else {
            top = game.player2;
            bottom = game.player1;
        }
        topPlayerName.setText(top.getName());
        bottomPlayerName.setText(bottom.getName());
        ArrayList<Piece> topCaptured = top.getPiecesCaptured();
        ArrayList<Piece> bottomCaptured = bottom.getPiecesCaptured();
        for (int i = 0; i < topCaptured.size(); i++)
            topTakenDisplay[i].setImage(ImageHandler.pieces.get(PieceType.fromPiece(topCaptured.get(i))));
        for (int i = topCaptured.size(); i < 16; i++)
            topTakenDisplay[i].setImage(null);
        for (int i = 0; i < bottomCaptured.size(); i++)
            bottomTakenDisplay[i].setImage(ImageHandler.pieces.get(PieceType.fromPiece(bottomCaptured.get(i))));
        for (int i = bottomCaptured.size(); i < 16; i++)
            bottomTakenDisplay[i].setImage(null);
    }

    public void showTimer(Game game) {
        Pane whiteBackground, blackBackground, activeBackground, inactiveBackground;
        Label whiteLabel, blackLabel, activeLabel, inactiveLabel;
        if (flipped) {
            whiteBackground = topClockBackground;
            whiteLabel = topClock;
            blackBackground = bottomClockBackground;
            blackLabel = bottomClock;
        } else {
            whiteBackground = bottomClockBackground;
            whiteLabel = bottomClock;
            blackBackground = topClockBackground;
            blackLabel = topClock;
        }
        if (game.whoseMove.getColour() == Colour.WHITE) {
            activeBackground = whiteBackground;
            inactiveBackground = blackBackground;
            activeLabel = whiteLabel;
            inactiveLabel = blackLabel;
        } else {
            activeBackground = blackBackground;
            inactiveBackground = whiteBackground;
            activeLabel = blackLabel;
            inactiveLabel = whiteLabel;
        }
        inactiveBackground.setBackground(Background.fill(Color.DARKGRAY));
        inactiveBackground.setBorder(Border.stroke(Color.GRAY));
        inactiveLabel.setTextFill(Color.GRAY);
        activeBackground.setBackground(Background.fill(Color.WHITE));
        activeBackground.setBorder(Border.stroke(Color.DARKGRAY));
        activeLabel.setTextFill(Color.BLACK);
    }

    public void onMousePressed(MouseEvent event) {
        if (mode == null || mode == Mode.ONLINE && colour != game.whoseMove.getColour())
            return;
        Point2D coords = getBoardCoords(event);
        if (coords == null)
            return;
        xBoardStart = (int) coords.getX();
        yBoardStart = (int) coords.getY();
        int realX = getRealX(xBoardStart);
        int realY = getRealY(yBoardStart);
        Square square = game.getBoard().getSquare(realX, realY);
        if (!square.isOccupied() || square.getPiece().getColour() != game.whoseMove.getColour())
            return;
        floating.setImage(ImageHandler.pieces.get(PieceType.fromPiece(square.getPiece())));
        floating.setVisible(true);
        showPiece(null, xBoardStart, yBoardStart);
        floating.setTranslateX(event.getSceneX() - width.getValue() / 2);
        floating.setTranslateY(event.getSceneY() - height.getValue() / 2);
        moving = true;
    }

    public void onMouseMoved(MouseEvent event) {
        if (!moving)
            return;
        floating.setTranslateX(event.getSceneX() - width.getValue() / 2);
        floating.setTranslateY(event.getSceneY() - height.getValue() / 2);
    }

    public void onMouseReleased(MouseEvent event) {
        if (!moving)
            return;
        Point2D coords = getBoardCoords(event);
        if (coords != null) {
            int realStartX = getRealX(xBoardStart);
            int realStartY = getRealY(yBoardStart);
            int realEndX = getRealX((int) coords.getX());
            int realEndY = getRealY((int) coords.getY());
            if (game.makeTurn(realStartX, realStartY, realEndX, realEndY)) {
                if (mode == Mode.ONLINE) {
                    try {
                        network.makeTurn(new Move(realStartX, realStartY, realEndX, realEndY));
                    } catch (IOException e) {
                        showNetworkError();
                    }
                }
                checkEnd();
            }
        }
        floating.setVisible(false);
        moving = false;
        refresh();
    }

    public void moveOpponent(int x0, int y0, int x1, int y1) {
        Platform.runLater(() -> {
            game.makeTurn(x0, y0, x1, y1);
            refresh();
            checkEnd();
        });
    }

    public void resignOpponent() {
        Platform.runLater(() -> {
            Colour loser = game.whoseMove.getColour();
            Colour winner = loser == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
            showWin(winner);
        });
    }

    public void requestDrawOpponent() {
        Platform.runLater(() -> {
            boolean result = askTie(game.whoseMove.getName());
            try {
                network.respondDraw(result);
            } catch (IOException e) {
                showNetworkError();
            }
            if (result)
                showTie();
        });
    }

    public void acceptDrawOpponent() {
        Platform.runLater(this::showTie);
    }

    public void declineDrawOpponent() {
        Platform.runLater(this::showNoTie);
    }

    private void checkEnd() {
        Player checked = game.isCheckMate();
        if (checked != null)
            showWin(checked.getColour());
        else if (game.isStaleMate())
            showTie();
    }

    private void showWin(Colour colour) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String winnerName = (colour == Colour.WHITE ? game.player1 : game.player2).getName();
        String loserName = (colour == Colour.WHITE ? game.player2 : game.player1).getName();
        if (mode == Mode.LOCAL) {
            alert.setTitle("Game ends");
            alert.setHeaderText("%s won!".formatted(winnerName));
            alert.setContentText("Congratulations");
        } else if (colour == this.colour) {
            alert.setTitle("You won");
            alert.setHeaderText("You beat %s!".formatted(loserName));
            alert.setContentText("Congratulations");
        } else {
            alert.setTitle("You lost");
            alert.setHeaderText("%s beat you!".formatted(winnerName));
            alert.setContentText("Better luck next time");
        }
        alert.showAndWait();
        mode = null;
    }

    private void showTie() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game ends");
        alert.setHeaderText("It's a tie!");
        alert.setContentText("");
        alert.showAndWait();
        mode = null;
    }

    private void showNoTie() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opponent declined");
        alert.setHeaderText("Continue playing or resign");
        alert.setContentText("");
        alert.showAndWait();
        mode = backup;
        backup = null;
    }

    private void showTieWait() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Waiting");
        alert.setHeaderText("Waiting for opponent response");
        alert.setContentText("You can close this window");
        alert.show();
    }

    private boolean askTie(String askerName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Draw request");
        String headerMessage = "%s asks you to draw".formatted(askerName);
        dialog.setHeaderText(headerMessage);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() != ButtonType.CANCEL;
    }

    private void showNetworkError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fatal error");
        alert.setHeaderText("Network error");
        alert.setContentText("A network error occurred");
        alert.showAndWait();
    }

    public void resign(ActionEvent ignoredEvent) {
        if (mode == null || colour != game.whoseMove.getColour())
            return;
        Colour winner = game.whoseMove.getColour().getInverse();
        if (mode == Mode.ONLINE) {
            try {
                network.resign();
            } catch (IOException e) {
                showNetworkError();
            }
        }
        showWin(winner);
    }

    public void draw(ActionEvent ignoredEvent) {
        if (mode == null || mode == Mode.ONLINE && colour != game.whoseMove.getColour())
            return;
        if (mode == Mode.LOCAL) {
            if (askTie(game.whoseMove.getName()))
                showTie();
            return;
        }
        showTieWait();
        backup = mode;
        mode = null;
        try {
            network.requestDraw();
        } catch (IOException e) {
            showNetworkError();
        }
    }

    public void save(ActionEvent ignoredEvent) {
        if (mode == null)
            return;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save game file");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File file = chooser.showSaveDialog(scene.getWindow());
        if (file != null) {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(game.player1.getName()).append(';').append(game.player2.getName()).append(';');
                for (int i = 0; i < game.getMoves().size(); i++) {
                    if (i != 0)
                        builder.append(':');
                    builder.append(game.getMoves().get(i));
                }
                Files.writeString(file.toPath(), builder.toString());
            } catch (IOException ignored) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Saving failed");
                alert.setContentText("Chosen file was incorrect");
                alert.showAndWait();
            }
        }
    }

    private Point2D getBoardCoords(MouseEvent event) {
        double xMouse = event.getSceneX();
        double yMouse = event.getSceneY();
        Point2D local = board.sceneToLocal(xMouse, yMouse);
        double xLocal = local.getX();
        double yLocal = local.getY();
        ObservableList<ColumnConstraints> columns = board.getColumnConstraints();
        double smallLength = columns.get(0).getPrefWidth();
        double bigLength = columns.get(1).getPrefWidth();
        if (xLocal < smallLength || xLocal > board.getWidth() - smallLength || yLocal < smallLength ||
                yLocal > board.getHeight() - smallLength)
            return null;
        int x = (int) ((xLocal - smallLength) / bigLength);
        int y = (int) ((yLocal - smallLength) / bigLength);
        return new Point2D(x, y);
    }

    public void flipBoard() {
        Image odd = ((ImageView) boardPanes[1][1].getChildren().get(0)).getImage();
        Image even = ((ImageView) boardPanes[1][2].getChildren().get(0)).getImage();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                ImageView view = (ImageView) boardPanes[i + 1][j + 1].getChildren().get(0);
                if ((i + j) % 2 == 0)
                    view.setImage(even);
                else
                    view.setImage(odd);
            }
        Image[] numbers = new Image[8];
        for (int i = 0; i < 8; i++)
            numbers[i] = ((ImageView) boardPanes[0][i + 1].getChildren().get(0)).getImage();
        for (int i = 0; i < 8; i++) {
            ((ImageView) boardPanes[0][i + 1].getChildren().get(0)).setImage(numbers[7 - i]);
            ((ImageView) boardPanes[9][i + 1].getChildren().get(0)).setImage(numbers[7 - i]);
        }
        for (int i = 0; i < 8; i++)
            numbers[i] = ((ImageView) boardPanes[i + 1][0].getChildren().get(0)).getImage();
        for (int i = 0; i < 8; i++) {
            ((ImageView) boardPanes[i + 1][0].getChildren().get(0)).setImage(numbers[7 - i]);
            ((ImageView) boardPanes[i + 1][9].getChildren().get(0)).setImage(numbers[7 - i]);
        }
        refresh();
    }

    public void prepare(Scene scene) {
        this.scene = scene;
        width = scene.widthProperty();
        height = scene.heightProperty();
        NumberBinding unit = Bindings.min(width.divide(11), height.divide(9));
        NumberBinding mainWidth = unit.multiply(11);
        NumberBinding mainHeight = unit.multiply(9);
        main.prefWidthProperty().bind(mainWidth);
        main.prefHeightProperty().bind(mainHeight);
        NumberBinding leftWidth = mainWidth.multiply(7d / 11);
        NumberBinding rightWidth = mainWidth.multiply(4d / 11);
        left.prefWidthProperty().bind(leftWidth);
        right.prefWidthProperty().bind(rightWidth);
        right.maxHeightProperty().bind(mainHeight);
        NumberBinding playerInfoHeight = mainHeight.divide(9);
        NumberBinding boardHeight = mainHeight.multiply(7d / 9);
        topPlayerName.prefHeightProperty().bind(playerInfoHeight);
        topPlayerName.prefWidthProperty().bind(leftWidth.divide(2));
        bottomPlayerName.prefHeightProperty().bind(playerInfoHeight);
        bottomPlayerName.prefWidthProperty().bind(leftWidth.divide(2));
        StringExpression labelFontSize = Bindings.concat("-fx-font-size: ", playerInfoHeight.divide(2), ";");
        topPlayerName.styleProperty().bind(labelFontSize);
        bottomPlayerName.styleProperty().bind(labelFontSize);
        board.prefHeightProperty().bind(boardHeight);
        bottomPlayer.prefHeightProperty().bind(playerInfoHeight);
        NumberBinding gridSide = boardHeight.divide(9);
        ImageHandler.init();
        boardPanes = new StackPane[10][10];
        //noinspection unchecked
        piecesViews = (EnumMap<PieceType, ImageView>[][]) new EnumMap[8][8];
        for (int i = 0; i < 10; i++) {
            NumberBinding gridSquare = i == 0 || i == 9 ? gridSide.divide(2) : gridSide;
            ColumnConstraints c = new ColumnConstraints();
            c.prefWidthProperty().bind(gridSquare);
            board.getColumnConstraints().add(c);
            RowConstraints r = new RowConstraints();
            r.prefHeightProperty().bind(gridSquare);
            board.getRowConstraints().add(r);
        }
        for (int x = 0; x < 10; x++) {
            NumberBinding gridWidth = x == 0 || x == 9 ? gridSide.divide(2) : gridSide;
            for (int y = 0; y < 10; y++) {
                NumberBinding gridHeight = y == 0 || y == 9 ? gridSide.divide(2) : gridSide;
                StackPane stack = new StackPane();
                ImageView view;
                boolean regular = false;
                if ((x == 0 || x == 9) && (y == 0 || y == 9))
                    view = new ImageView(ImageHandler.corner);
                else if (x == 0 || x == 9)
                    view = new ImageView(ImageHandler.numbers[8 - y]);
                else if (y == 0 || y == 9)
                    view = new ImageView(ImageHandler.letters[x - 1]);
                else {
                    if ((x + y) % 2 == 1)
                        view = new ImageView(ImageHandler.black);
                    else
                        view = new ImageView(ImageHandler.white);
                    stack.setId(String.format("b%d%d", x - 1, y - 1));
                    regular = true;
                }
                view.fitWidthProperty().bind(gridWidth);
                view.fitHeightProperty().bind(gridHeight);
                stack.getChildren().add(view);
                if (regular) {
                    piecesViews[x - 1][y - 1] = new EnumMap<>(PieceType.class);
                    for (PieceType piece : PieceType.values()) {
                        ImageView v = new ImageView(ImageHandler.pieces.get(piece));
                        v.fitWidthProperty().bind(gridWidth);
                        v.fitHeightProperty().bind(gridHeight);
                        v.setVisible(false);
                        stack.getChildren().add(v);
                        piecesViews[x - 1][y - 1].put(piece, v);
                    }
                }
                board.add(stack, x, y);
                boardPanes[x][y] = stack;
            }
        }
        NumberBinding viewSide = leftWidth.divide(16);
        topPlayerTaken.prefWidthProperty().bind(leftWidth.divide(2));
        bottomPlayerTaken.prefWidthProperty().bind(leftWidth.divide(2));
        for (int i = 0; i < 8; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.prefWidthProperty().bind(viewSide);
            RowConstraints r = new RowConstraints();
            r.prefHeightProperty().bind(viewSide);
        }
        topTakenDisplay = new ImageView[16];
        bottomTakenDisplay = new ImageView[16];
        for (int i = 0; i < 16; i++) {
            ImageView top = new ImageView();
            top.fitHeightProperty().bind(viewSide);
            top.fitWidthProperty().bind(viewSide);
            ImageView bottom = new ImageView();
            bottom.fitHeightProperty().bind(viewSide);
            bottom.fitWidthProperty().bind(viewSide);
            topPlayerTaken.add(top, 7 - i % 8, i / 8);
            bottomPlayerTaken.add(bottom, 7 - i % 8, i / 8);
            topTakenDisplay[i] = top;
            bottomTakenDisplay[i] = bottom;
        }
        right.spacingProperty().bind(unit.divide(5));
        right.paddingProperty()
                .bind(Bindings.createObjectBinding(() -> new Insets(unit.divide(5).doubleValue()), unit));
        NumberBinding childWidth = rightWidth.subtract(unit.multiply(0.4));
        topClockBackground.prefHeightProperty().bind(unit);
        bottomClockBackground.prefHeightProperty().bind(unit);
        topClock.styleProperty().bind(labelFontSize);
        topClock.setText("60:00");
        bottomClock.styleProperty().bind(labelFontSize);
        bottomClock.setText("60:00");
        StringExpression buttonFontSize = Bindings.concat("-fx-font-size: ", unit.divide(4), ";");
        resign.prefHeightProperty().bind(unit.divide(2));
        draw.prefHeightProperty().bind(unit.divide(2));
        save.prefHeightProperty().bind(unit.divide(2));
        resign.prefWidthProperty().bind(childWidth);
        draw.prefWidthProperty().bind(childWidth);
        save.prefWidthProperty().bind(childWidth);
        resign.styleProperty().bind(buttonFontSize);
        draw.styleProperty().bind(buttonFontSize);
        save.styleProperty().bind(buttonFontSize);
        moves.styleProperty().bind(buttonFontSize);
        floating.setVisible(false);
        floating.fitWidthProperty().bind(gridSide);
        floating.fitHeightProperty().bind(gridSide);
    }
}