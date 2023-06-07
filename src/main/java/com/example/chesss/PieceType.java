package com.example.chesss;

import org.example.*;

public enum PieceType {
    PAWN_WHITE, PAWN_BLACK, ROOK_WHITE, ROOK_BLACK, KNIGHT_WHITE, KNIGHT_BLACK, BISHOP_WHITE, BISHOP_BLACK, QUEEN_WHITE,
    QUEEN_BLACK, KING_WHITE, KING_BLACK;

    public static PieceType fromPiece(Piece piece) {
        if (piece instanceof Pawn pawn) {
            if (pawn.getColour() == Colour.WHITE)
                return PAWN_WHITE;
            return PAWN_BLACK;
        }
        if (piece instanceof Rook rook) {
            if (rook.getColour() == Colour.WHITE)
                return ROOK_WHITE;
            return ROOK_BLACK;
        }
        if (piece instanceof Knight knight) {
            if (knight.getColour() == Colour.WHITE)
                return KNIGHT_WHITE;
            return KNIGHT_BLACK;
        }
        if (piece instanceof Bishop bishop) {
            if (bishop.getColour() == Colour.WHITE)
                return BISHOP_WHITE;
            return BISHOP_BLACK;
        }
        if (piece instanceof Queen queen) {
            if (queen.getColour() == Colour.WHITE)
                return QUEEN_WHITE;
            return QUEEN_BLACK;
        }
        if (piece instanceof King king) {
            if (king.getColour() == Colour.WHITE)
                return KING_WHITE;
            return KING_BLACK;
        }
        throw new IllegalArgumentException();
    }
}
