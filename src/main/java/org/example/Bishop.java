package org.example;

import java.util.ArrayList;

/**
 * The Bishop class represents a Bishop chess piece, which moves and captures diagonally.
 * <p>
 * Note: This class inherits from the abstract Piece class, and may need to implement its abstract methods or override
 * its inherited methods to provide specific bishop behavior.
 * <p>
 * Note: This class assumes that there are two bishops per player. Other conventions
 * may require adjustments.
 */
public class Bishop extends Piece {

    /**
     * Constructor of the bishop.
     */
    public Bishop(Colour colour, Square positionOfPiece) {
        super(colour, positionOfPiece, 3);
    }

    @Override
    public boolean isValidMove(int x, int y) {
        //moving rules
        if (x == getPosition().getX() && y == getPosition().getY())
            return false;

        if (!(Math.abs(x - getPosition().getX()) == Math.abs(y - getPosition().getY()))) {
            return false;
        }
        //is another piece blocking?
        int xAdder = -(getPosition().getX() - x) / Math.abs(getPosition().getX() - x);
        int yAdder = -(getPosition().getY() - y) / Math.abs(getPosition().getY() - y);
        int xIter = getPosition().getX() + xAdder;
        int yIter = getPosition().getY() + yAdder;
        while (xIter != x && yIter != y) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) return false;
            xIter += xAdder;
            yIter += yAdder;
        }
        //is pinned?
        Board helpingBoard = getBoard().cloneBoard();
        helpingBoard.movePiece
                (helpingBoard.getSquare(getPosition().getX(), getPosition().getY()).getPiece(), x, y);
        if (helpingBoard.findPiece(new King(this.getColour(), new Square())) != null
                && ((King) helpingBoard.findPiece(new King(this.getColour(), new Square()))).hasBeenChecked())
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
        Bishop clonedPiece = new Bishop(this.getColour(), position);
        return clonedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bishop && ((Bishop) obj).getColour() == this.getColour()) {
            return true;
        }
        return false;
    }
}