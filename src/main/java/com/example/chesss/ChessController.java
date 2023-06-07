package com.example.chesss;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChessController {
    @FXML
    public HBox main;
    public VBox left;
    public VBox right;
    public HBox topPlayer;
    public GridPane board;
    public HBox bottomPlayer;
    private StackPane[][] boardPanes;

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
        NumberBinding gridSide = boardHeight.divide(8);
        boardPanes = new StackPane[10][10];
        for (int x = 0; x < 10; x++) {
            NumberBinding gridWidth = x == 0 || x == 9 ? gridSide.divide(2) : gridSide;
            for (int y = 0; y < 10; y++) {
                NumberBinding gridHeight = y == 0 || y == 9 ? gridSide.divide(2) : gridSide;
                StackPane stack = new StackPane();
                stack.minWidthProperty().bind(gridWidth);
                stack.maxWidthProperty().bind(gridWidth);
                stack.minHeightProperty().bind(gridHeight);
                stack.maxHeightProperty().bind(gridHeight);
                stack.setStyle("-fx-border-color: brown; -fx-border-width: 3px;");
                board.add(stack, x, y);
            }
        }
    }
}