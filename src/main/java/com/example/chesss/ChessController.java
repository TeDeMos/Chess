package com.example.chesss;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.text.Element;
import javafx.scene.image.ImageView;
import org.example.Board;
import org.example.Piece;
import org.example.Square;

import java.util.EnumMap;

public class ChessController {
    @FXML
    public HBox main;
    public VBox left;
    public VBox right;
    public HBox topPlayer;
    public GridPane board;
    public HBox bottomPlayer;
    private StackPane[][] boardPanes;
    private EnumMap<PieceType, ImageView>[][] piecesViews;

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

    public void prepare(Scene scene) {
        ReadOnlyDoubleProperty width = scene.widthProperty();
        ReadOnlyDoubleProperty height = scene.heightProperty();
        NumberBinding unit = Bindings.min(width.divide(11), height.divide(9));
        //NumberBinding unit = Bindings.min(width, height).divide(9);
        NumberBinding mainWidth = unit.multiply(11);
        NumberBinding mainHeight = unit.multiply(9);
        //        main.prefWidthProperty().bind(mainWidth);
        main.minWidthProperty().bind(mainWidth);
        main.maxWidthProperty().bind(mainWidth);
        //        main.prefHeightProperty().bind(mainHeight);
        main.minHeightProperty().bind(mainHeight);
        main.maxHeightProperty().bind(mainHeight);
        NumberBinding leftWidth = mainWidth.multiply(7d / 11);
        NumberBinding rightWidth = mainWidth.multiply(4d / 11);
        left.minWidthProperty().bind(leftWidth);
        left.maxWidthProperty().bind(leftWidth);
        right.minWidthProperty().bind(rightWidth);
        right.maxWidthProperty().bind(rightWidth);
        NumberBinding playerInfoHeight = mainHeight.divide(9);
        NumberBinding boardHeight = mainHeight.multiply(7d / 9);
        topPlayer.minHeightProperty().bind(playerInfoHeight);
        topPlayer.maxHeightProperty().bind(playerInfoHeight);
        board.minHeightProperty().bind(boardHeight);
        board.maxHeightProperty().bind(boardHeight);
        bottomPlayer.minHeightProperty().bind(playerInfoHeight);
        bottomPlayer.maxHeightProperty().bind(playerInfoHeight);
        NumberBinding gridSide = boardHeight.divide(9);
        ImageHandler.init();
        boardPanes = new StackPane[10][10];
        //noinspection unchecked
        piecesViews = (EnumMap<PieceType, ImageView>[][]) new EnumMap[8][8];
        for (int x = 0; x < 10; x++) {
            NumberBinding gridWidth = x == 0 || x == 9 ? gridSide.divide(2) : gridSide;
            for (int y = 0; y < 10; y++) {
                NumberBinding gridHeight = y == 0 || y == 9 ? gridSide.divide(2) : gridSide;
                StackPane stack = new StackPane();
                stack.minWidthProperty().bind(gridWidth);
                stack.maxWidthProperty().bind(gridWidth);
                stack.minHeightProperty().bind(gridHeight);
                stack.maxHeightProperty().bind(gridHeight);
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
                    regular = true;
                }
                view.fitWidthProperty().bind(gridWidth);
                view.fitHeightProperty().bind(gridHeight);
                stack.getChildren().add(view);
                if (regular) {
                    piecesViews[x - 1][y - 1] = new EnumMap<>(PieceType.class);
                    for (PieceType piece: PieceType.values()) {
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
        showBoard(new Board(""));
    }
}