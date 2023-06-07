package org.example;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Rook class represents a rook chess piece, which moves and captures horizontally or vertically in straight lines.
 * This class provides methods for handling rook-specific moves, such as castling.
 * <p>
 * Note: This class inherits from the abstract Piece class, and may need to implement its abstract methods or override
 * its inherited methods to provide specific rook behavior.
 * <p>
 * Note: This class assumes that there are two rooks per player, located at the corners of the board. Other conventions
 * may require adjustments.
 *
 */
public class Rook extends Piece {
    /**
     * Constructor of the rook.
     */
    public Rook(Colour colour, Square positionOfPiece) {
        super(colour, positionOfPiece, 5);
        hasMoved = false;
    }

    /**
     * Flag indicating whether the rook has moved from its original position (affecting castling options).
     */
    public boolean hasMoved;

    @Override
    public boolean isValidMove(int x, int y) {
        //moving rules
        if (!((x == getPosition().getX() && y != getPosition().getY())
                || (x != getPosition().getX() && y == getPosition().getY()))) {
            return false;
        }
        //is another piece blocking?
        if (x == getPosition().getX()) {
            for (int i = Math.min(y, getPosition().getY())+1; i < Math.max(y, getPosition().getY()); i++) {
                if (getBoard().getSquare(x,i).isOccupied()) return false;
            }
        }
        if (y == getPosition().getY()) {
            for (int i = Math.min(x, getPosition().getX())+1; i < Math.max(x, getPosition().getX()); i++) {
                if (getBoard().getSquare(i,y).isOccupied()) return false;
            }
        }
        //is pinned?
        Board helpingBoard = getBoard().cloneBoard();
        helpingBoard.movePiece
                (helpingBoard.getSquare(getPosition().getX(), getPosition().getY()).getPiece(),x,y);
        if (helpingBoard.findPiece(new King(this.getColour(),new Square()))!=null &&
                ((King) helpingBoard.findPiece(new King(this.getColour(),new Square()))).hasBeenChecked())
            return false;
        //is piece?
        if (getBoard().getSquare(x,y).getPiece()!=null) {
            //is your piece?
            if (getBoard().getSquare(x,y).getPiece().getColour()==this.getColour()) return false;
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
                if (isValidMove(j,i)) possibleMoves.add(getBoard().getSquare(j,i));
            }
        }
        return possibleMoves;
    }

    @Override
    public Piece clonePiece(Square position) {
        Rook clonedPiece = new Rook(this.getColour(), position);
        clonedPiece.hasMoved = this.hasMoved;
        return clonedPiece;
    }
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Rook && ((Rook) obj).getColour()==this.getColour()){
            return true;
        }
        return false;
    }
    public boolean isHasMoved() {
        return hasMoved;
    }
}
