package org.example;

/**
 * Colours are used to determinate colour of a player and a piece
 */
public enum Colour {
    WHITE, BLACK;

    public String displayName() {
        return this == WHITE ? "White" : "Black";
    }

    public Colour getInverse() {
        return this == WHITE ? BLACK : WHITE;
    }
}