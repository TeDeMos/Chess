package org.example;

import java.util.ArrayList;

/**
 * The Knight class represents a knight chess piece. Knight has unique way of moving, it can jump over other pieces.
 * It moves in an L-shape pattern, jumping over any other pieces in its way.
 * Specifically, the knight moves two squares horizontally or vertically in any direction
 * and then makes a 90-degree turn and moves one square in a perpendicular direction.
 * <p>
 * Note: This class inherits from the abstract Piece class, and may need to implement its abstract methods or override
 * its inherited methods to provide specific knight behavior.
 *
 */
public class Knight extends Piece {

    /**
     * Constructor of the knight.
     */
    public Knight(Colour colour, Square positionOfPiece) {
        super(colour, positionOfPiece, 3);
    }

    @Override
    public boolean isValidMove(int x, int y) {
        //moving rules
        if (!((Math.abs(x - getPosition().getX()) == 1 && Math.abs(y - getPosition().getY())==2)
                || (Math.abs(y - getPosition().getY()) == 1 && Math.abs(x - getPosition().getX())==2))) {
            return false;
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
        Knight clonedPiece = new Knight(this.getColour(), position);
        return clonedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Knight && ((Knight) obj).getColour() == this.getColour()) {
            return true;
        }
        return false;
    }
}