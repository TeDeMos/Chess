package com.example.chesss;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.text.Element;

import javafx.scene.image.ImageView;
import org.example.Board;
import org.example.Game;
import org.example.Piece;
import org.example.Square;

import java.time.LocalDate;
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
    private StackPane[][] boardPanes;
    private EnumMap<PieceType, ImageView>[][] piecesViews;
    private Game game;
    private int xStart;
    private int yStart;
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
                Square square = board.getSquare(i, j);
                if (square.isOccupied())
                    showPiece(PieceType.fromPiece(board.getSquare(i, j).getPiece()), i, j);
                else
                    showPiece(null, i, j);
            }
        }
    }

    public void onMousePressed(MouseEvent event) {
        Point2D coords = getBoardCoords(event);
        if (coords == null)
            return;
        xBoardStart = (int) coords.getX();
        yBoardStart = (int) coords.getY();
        Square square = game.getBoard().getSquare(xBoardStart, yBoardStart);
        if (!square.isOccupied() || square.getPiece().getColour() != game.whoseMove.getColour())
            return;
        floating.setImage(ImageHandler.pieces.get(PieceType.fromPiece(square.getPiece())));
        floating.setVisible(true);
        showPiece(null, xBoardStart, yBoardStart);
        floating.setTranslateX(event.getSceneX() - width.getValue() / 2);
        floating.setTranslateY(event.getSceneY() - height.getValue() / 2);
        moving = true;
    }

    private void onMouseMoved(MouseEvent event) {
        if (!moving)
            return;
        floating.setTranslateX(event.getSceneX() - width.getValue() / 2);
        floating.setTranslateY(event.getSceneY() - height.getValue() / 2);
    }

    private void onMouseReleased(MouseEvent event) {
        if (!moving)
            return;
        Point2D coords = getBoardCoords(event);
        if (coords != null)
            game.makeTurn(xBoardStart, yBoardStart, (int) coords.getX(), (int) coords.getY());
        floating.setVisible(false);
        moving = false;
        showBoard(game.getBoard());
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
        scene.setOnMousePressed(this::onMousePressed);
        scene.setOnMouseDragged(this::onMouseMoved);
        scene.setOnMouseReleased(this::onMouseReleased);
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
        NumberBinding playerInfoHeight = mainHeight.divide(9);
        NumberBinding boardHeight = mainHeight.multiply(7d / 9);
        topPlayer.prefHeightProperty().bind(playerInfoHeight);
        board.prefHeightProperty().bind(boardHeight);
        bottomPlayer.prefHeightProperty().bind(playerInfoHeight);
        NumberBinding gridSide = boardHeight.divide(9);
        ImageHandler.init();
        boardPanes = new StackPane[10][10];
        //noinspection unchecked
        piecesViews = (EnumMap<PieceType, ImageView>[][]) new EnumMap[8][8];
        for (int x = 0; x < 10; x++) {
            NumberBinding gridWidth = x == 0 || x == 9 ? gridSide.divide(2) : gridSide;
            ColumnConstraints c = new ColumnConstraints();
            c.prefWidthProperty().bind(gridWidth);
            board.getColumnConstraints().add(c);
            for (int y = 0; y < 10; y++) {
                NumberBinding gridHeight = y == 0 || y == 9 ? gridSide.divide(2) : gridSide;
                RowConstraints r = new RowConstraints();
                r.prefHeightProperty().bind(gridHeight);
                board.getRowConstraints().add(r);
                StackPane stack = new StackPane();
                ImageView view;
                boolean regular = false;
                if ((x == 0 || x == 9) && (y == 0 || y == 9))
                    view = new ImageView(ImageHandler.corner);
                else if (x == 0 || x == 9)
                    view = new ImageView(ImageHandler.numbers[y - 1]);
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
        floating.setVisible(false);
        floating.fitWidthProperty().bind(gridSide);
        floating.fitHeightProperty().bind(gridSide);
        game = new Game("Gracz a", "Gracz b", LocalDate.now());
        showBoard(game.getBoard());
    }
}