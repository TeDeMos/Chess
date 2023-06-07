package com.example.chesss;

import javafx.scene.image.Image;

import java.util.EnumMap;

public class ImageHandler {
    public static Image corner;
    public static Image[] letters;
    public static Image[] numbers;
    public static Image black;
    public static Image white;
    public static Image pawnWhite;
    public static Image pawnBlack;
    public static EnumMap<PieceType, Image> pieces;

    public static void init() {
        corner = new Image("/com/example/chesss/corner.png");
        letters = new Image[8];
        numbers = new Image[8];
        for (int i = 0; i < 8; i++) {
            letters[i] = new Image("/com/example/chesss/" + (char)('a' + i) + ".png");
            numbers[i] = new Image("/com/example/chesss/" + (1 + i) + ".png");
        }
        black = new Image("/com/example/chesss/black.png");
        white = new Image("/com/example/chesss/white.png");
        pieces = new EnumMap<>(PieceType.class);
        pieces.put(PieceType.PAWN_WHITE, new Image("/com/example/chesss/pawn_white.png"));
        pieces.put(PieceType.PAWN_BLACK, new Image("/com/example/chesss/pawn_black.png"));
        pieces.put(PieceType.ROOK_WHITE, new Image("/com/example/chesss/rook_white.png"));
        pieces.put(PieceType.ROOK_BLACK, new Image("/com/example/chesss/rook_black.png"));
        pieces.put(PieceType.KNIGHT_WHITE, new Image("/com/example/chesss/knight_white.png"));
        pieces.put(PieceType.KNIGHT_BLACK, new Image("/com/example/chesss/knight_black.png"));
        pieces.put(PieceType.BISHOP_WHITE, new Image("/com/example/chesss/bishop_white.png"));
        pieces.put(PieceType.BISHOP_BLACK, new Image("/com/example/chesss/bishop_black.png"));
        pieces.put(PieceType.QUEEN_WHITE, new Image("/com/example/chesss/queen_white.png"));
        pieces.put(PieceType.QUEEN_BLACK, new Image("/com/example/chesss/queen_black.png"));
        pieces.put(PieceType.KING_WHITE, new Image("/com/example/chesss/king_white.png"));
        pieces.put(PieceType.KING_BLACK, new Image("/com/example/chesss/king_black.png"));
    }
}
