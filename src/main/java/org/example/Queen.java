package org.example;

import java.util.ArrayList;

/**
 * The Queen class represents a Queen chess piece, which moves and captures both diagonally, horizontally or veritcally in straight lines .
 * <p>
 * Note: This class inherits from the abstract Piece class, and may need to implement its abstract methods or override
 * its inherited methods to provide specific Queen behavior.
 *
 * @author [Mateusz Plichta ]
 * @version 1.0
 */
public class Queen extends Piece {

    /**
     * Constructor of the queen.
     */
    public Queen(Colour colour, Square positionOfPiece) {
        super(colour, positionOfPiece, 9);
    }

    @Override
    public boolean isValidMove(int x, int y) {
        //moving rules
        if (x == getPosition().getX() && y == getPosition().getY())
            return false;

        if (!((x == getPosition().getX() && y != getPosition().getY())
                || (x != getPosition().getX() && y == getPosition().getY())
                || (Math.abs(x - getPosition().getX()) == Math.abs(y - getPosition().getY())))) {
            return false;
        }
        //is another piece blocking?
        //rook moves
        if (x == getPosition().getX()) {
            for (int i = Math.min(y, getPosition().getY()) + 1; i < Math.max(y, getPosition().getY()); i++) {
                if (getBoard().getSquare(x, i).isOccupied()) return false;
            }
        } else if (y == getPosition().getY()) {
            for (int i = Math.min(x, getPosition().getX()) + 1; i < Math.max(x, getPosition().getX()); i++) {
                if (getBoard().getSquare(i, y).isOccupied()) return false;
            }
        }
        //bishop moves
        else {
            int xAdder = -(getPosition().getX() - x) / Math.abs(getPosition().getX() - x);
            int yAdder = -(getPosition().getY() - y) / Math.abs(getPosition().getY() - y);
            int xIter = getPosition().getX() + xAdder;
            int yIter = getPosition().getY() + yAdder;
            while (xIter != x && yIter != y) {
                if (getBoard().getSquare(xIter, yIter).isOccupied()) return false;
                xIter += xAdder;
                yIter += yAdder;
            }
        }
       //is pinned?
        Board helpingBoard = getBoard().cloneBoard();
        helpingBoard.movePiece
                (helpingBoard.getSquare(getPosition().getX(), getPosition().getY()).getPiece(), x, y);
        if (helpingBoard.findPiece(new King(this.getColour(), new Square())) != null &&
                ((King) helpingBoard.findPiece(new King(this.getColour(), new Square()))).hasBeenChecked())
            return false;
       //is piece?
        if (getBoard().getSquare(x, y).getPiece() != null) {
            //is your piece?
            if (getBoard().getSquare(x, y).getPiece().getColour() == this.getColour()) return false;
            //is enemy piece?
            //if (getBoard().getSquare(x,y).getPiece().getColour()!=this.getColour()) isCapturePossible()=true;
        }
        //all good
        return true;
    }

    @Override
    public boolean isCapturePossible() {
        return false;
    }

    @Override
    public ArrayList<Square> getPossibleMoves() {
        ArrayList<Square> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(j, i)) possibleMoves.add(getBoard().getSquare(j, i));
            }
        }
        return possibleMoves;
    }

    @Override
    public Piece clonePiece(Square position) {
        Queen clonedPiece = new Queen(this.getColour(), position);
        return clonedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Queen && ((Queen) obj).getColour() == this.getColour()) {
            return true;
        }
        return false;
    }
}