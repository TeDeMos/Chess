package com.example.chesss;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.text.Element;

import javafx.scene.image.ImageView;
import org.example.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

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
    public Label whiteClock;
    public Label blackClock;
    public Pane whiteClockBackground;
    public Pane blackClockBackground;
    public Button resign;
    public Button draw;
    public Button save;
    public ListView<String> moves;
    private StackPane[][] boardPanes;
    private ImageView[] topTakenDisplay;
    private ImageView[] bottomTakenDisplay;
    private EnumMap<PieceType, ImageView>[][] piecesViews;
    private Game game;
    private int xBoardStart;
    private int yBoardStart;
    private boolean moving;

    ReadOnlyDoubleProperty width;
    ReadOnlyDoubleProperty height;

    private void showPiece(PieceType piece, int x, int y) {
        for (PieceType p : PieceType.values())
            piecesViews[x][y].get(p).visibleProperty().set(false);
        if (piece != null)
            piecesViews[x][y].get(piece).visibleProperty().set(true);
    }

    public void showBoard(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquare(i, 7 - j);
                if (square.isOccupied())
                    showPiece(PieceType.fromPiece(square.getPiece()), i, j);
                else
                    showPiece(null, i, j);
            }
        }
    }

    public void showPlayers(Game game) {
        Player player1 = game.player1;
        Player player2 = game.player2;
        if (player1.getColour() == Colour.WHITE) {
            Player temp = player1;
            player1 = player2;
            player2 = temp;
        }
        topPlayerName.setText(player1.getName());
        bottomPlayerName.setText(player2.getName());
        ArrayList<Piece> top = player1.getPiecesCaptured();
        ArrayList<Piece> bottom = player2.getPiecesCaptured();
        for (int i = 0; i < top.size(); i++)
            topTakenDisplay[i].setImage(ImageHandler.pieces.get(PieceType.fromPiece(top.get(i))));
        for (int i = 0; i < bottom.size(); i++)
            bottomTakenDisplay[i].setImage(ImageHandler.pieces.get(PieceType.fromPiece(bottom.get(i))));
    }

    public void showTimer(Game game) {
        Pane activeBackground, inactiveBackground;
        Label activeLabel, inactiveLabel;
        if (game.whoseMove.getColour() == Colour.BLACK) {
            activeBackground = whiteClockBackground;
            inactiveBackground = blackClockBackground;
            activeLabel = whiteClock;
            inactiveLabel = blackClock;
        } else {
            activeBackground = blackClockBackground;
            inactiveBackground = whiteClockBackground;
            activeLabel = blackClock;
            inactiveLabel = whiteClock;
        }
        inactiveBackground.setBackground(Background.fill(Color.DARKGRAY));
        inactiveBackground.setBorder(Border.stroke(Color.GRAY));
        inactiveLabel.setTextFill(Color.GRAY);
        activeBackground.setBackground(Background.fill(Color.WHITE));
        activeBackground.setBorder(Border.stroke(Color.DARKGRAY));
        activeLabel.setTextFill(Color.BLACK);
    }

    public void onMousePressed(MouseEvent event) {
        Point2D coords = getBoardCoords(event);
        if (coords == null)
            return;
        xBoardStart = (int) coords.getX();
        yBoardStart = (int) coords.getY();
        Square square = game.getBoard().getSquare(xBoardStart, 7 - yBoardStart);
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
            Piece start = game.getBoard().getSquare(xBoardStart, 7 - yBoardStart).getPiece();
            if (game.makeTurn(xBoardStart, 7 - yBoardStart, (int) coords.getX(), 7 - (int) coords.getY())) {
                String[] split = start.getClass().getName().split("\\.");
                String name = split[split.length - 1];
                char letterStart = (char) ('A' + xBoardStart);
                char letterEnd = (char) ('A' + (int) coords.getX());
                moves.getItems().add(0, String.format("%s: %c%d -> %c%d", name, letterStart, 8 - yBoardStart, letterEnd,
                        8 - (int) coords.getY()));
            }
        }
        floating.setVisible(false);
        moving = false;
        showBoard(game.getBoard());
        showPlayers(game);
        showTimer(game);
    }

    public void resign(ActionEvent event) {
    }

    public void draw(ActionEvent actionEvent) {
    }

    public void save(ActionEvent actionEvent) {
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

    public void prepare(Scene scene) {
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
        whiteClockBackground.prefHeightProperty().bind(unit);
        blackClockBackground.prefHeightProperty().bind(unit);
        whiteClock.styleProperty().bind(labelFontSize);
        whiteClock.setText("60:00");
        blackClock.styleProperty().bind(labelFontSize);
        blackClock.setText("60:00");
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
        game = new Game("Maksymilian", "Tymoteusz", LocalDate.now());
        showBoard(game.getBoard());
        showPlayers(game);
        showTimer(game);
    }
}