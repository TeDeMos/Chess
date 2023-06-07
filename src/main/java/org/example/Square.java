package org.example;

/**
 * The Square class represents one of the 64 squares the chessboard is made of.
 */
public class Square {

    /**
     * Default constructor.
     */
    public Square() {
    }
    /**
     * x coordinate of the square
     */
    private int x;

    /**
     * y coordinate of the square
     */
    private int y;

    /**
     * Constructor that initialize x, y and board attributes
     * @param x x coordinate of the square
     * @param y y coordinate of the square
     * @param board board on which the square is located
     */
    public Square(int x, int y, Board board){
        this.board = board;
        this.x=x;
        this.y=y;
        this.labelNumber= (char) (y+49);
        this.labelLetter= (char) (x+65);
        this.piece=null;
    }

//    /**
//     * Colour of the square.
//     */
//    private Colour colour;

    /**
     * Board on which the square is located.
     */
    private Board board;

    public Board getBoard() {
        return board;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Piece that is located on the square.
     */
    private Piece piece;

    /**
     * X coordinate as a letter in char (used for GUI).
     */
    private char labelLetter;

    /**
     * Y coordinate as a number in char (used for GUI).
     */
    private char labelNumber;

    /**
     * Method to return if the square is occupied by a figure.
     * @return true if it's occupied, false otherwise
     */
    public boolean isOccupied() {
        if (piece==null)  return false;
        else return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    public String toString() {
        return Character.toString(labelLetter)+Character.toString(labelNumber);
    }
}