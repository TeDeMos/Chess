package org.example;

import java.awt.*;
import java.util.*;

/**
 * The Piece class represents an abstract chess piece, with a color, a position, a value,
 * an icon, and methods for checking the validity of moves, getting possible moves, and
 * updating the position of the piece.
 * <p>
 * This is an abstract class, which means that it cannot be instantiated directly, but
 * only through its concrete subclasses, such as Pawn, Rook, Knight, etc.
 *
 */
public abstract class Piece {
    /**
     * Constructor of the piece.
     *
     * @param colour the colour of the piece.
     * @param positionOfPiece the position of the piece, represented by Square object.
     * @param value material value of the piece
     */
    public Piece(Colour colour, Square positionOfPiece, int value) {
        this.colour = colour;
        this.positionOfPiece = positionOfPiece;
        this.value = value;
        this.board = positionOfPiece.getBoard();
    }

    public Board getBoard() {
        return board;
    }

    /**
     * The board on which the piece is located.
     */
    private Board board;
    /**
     * The color of the piece, which can be either white or black.
     */
    private Colour colour;

    /**
     * The position of the piece on the chess board, represented as a square.
     */
    private Square positionOfPiece;

    /**
     * The value of the piece in terms of material worth, which can be used for
     * evaluating the relative strength of positions or moves.
     */
    private int value;

    /**
     * The icon of the piece, which can be used for rendering the chess board or
     * graphical user interfaces.
     */
    private Image icon;

    /**
     * Checks whether a given move is valid for the piece, according to the rules of
     * chess and the current position of other pieces on the board.
     * Implements the basic moving and taking rules and checks the effect that the given move has on the king.
     * King condition is being checked by cloning a whole board.
     *
     * @return true if the move is valid, false otherwise.
     */
    public abstract boolean isValidMove(int x, int y);

    /**
     * Gets the current position of the piece on the chess board.
     *
     * @return the square representing the current position of the piece.
     */
    public Square getPosition() {
        return positionOfPiece;
    }

    /**
     * Gets the color of the piece, which can be either white or black.
     *
     * @return the color of the piece.
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Useless method implemented by the first group.
     *
     * @return nothing actually
     */
    public abstract boolean isCapturePossible();

    /**
     * Sets the position of the piece on the chess board to a given square.
     *
     * @param position the square to which the piece should be moved.
     */
    public void setPosition(Square position) {
        positionOfPiece = position;
    }

    /**
     * Checks whether the given coordinates are in bounds.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the coordinates are in bounds, false otherwise
     */
    public boolean isValidPlace(int x, int y) {
        if (x < 8 && x >= 0 && y < 8 && y >= 0) {
            return true;
        } else return false;
    }

    /**
     * Gets a list of all the possible moves that the piece can make from its current
     * position, according to the rules of chess and the current position of other pieces.
     * Iterates over all the squares and evokes isValidMove() method.
     *
     * @return a list of squares representing the possible moves of the piece.
     */
    public abstract ArrayList<Square> getPossibleMoves();

    /**
     * Clones the piece.
     *
     * @param position position of a cloned piece
     * @return cloned piece
     */
    public abstract Piece clonePiece(Square position);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Piece && ((Piece) obj).getColour() == this.getColour()) {
            return true;
        }
        return false;
    }
     public int getValue() {
        return value;
    }
}
