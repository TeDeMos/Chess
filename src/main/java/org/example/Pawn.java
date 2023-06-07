package org.example;

import java.util.ArrayList;

/**
 * The Pawn class represents a pawn chess piece, which moves and captures in a unique way compared to other pieces.
 * This class provides methods for handling pawn-specific moves, such as promotion and en passant.
 * <p>
 * Note: This class inherits from the abstract Piece class, and may need to implement its abstract methods or override
 * its inherited methods to provide specific pawn behavior.
 *
 */
public class Pawn extends Piece {
    /**
     * Constructor of the pawn.
     */
    public Pawn(Colour colour, Square positionOfPiece) {
        super(colour, positionOfPiece, 1);
        enPassant = false;
        promotion = false;
        direction = (colour == Colour.WHITE) ? 1 : -1;
    }

    /**
     * Indicator of direction, in which the pawn is moving; 1 for white, -1 for black
     */
    private int direction;
    /**
     * Idicates whether the pawn has moved (affecting en passamt).
     */
    public boolean hasMoved;
    /**
     * Flag indicating whether the pawn is eligible for promotion (i.e., has reached the opposite end of the board).
     */
    public boolean promotion;

    /**
     * Flag indicating whether the pawn can be captured en passant.
     */
    public boolean enPassant;

    /**
     * Promotes the pawn to a higher-ranked piece, such as a queen, rook, bishop, or knight, at the end of the board.
     */
    public void Promotion(Piece piece) {
        if (piece instanceof Queen) getBoard().getSquare(getPosition().getX(), getPosition().getY())
                .setPiece(new Queen(getColour(), getPosition()));
        if (piece instanceof Bishop) getBoard().getSquare(getPosition().getX(), getPosition().getY())
                .setPiece(new Bishop(getColour(), getPosition()));
        if (piece instanceof Knight) getBoard().getSquare(getPosition().getX(), getPosition().getY())
                .setPiece(new Knight(getColour(), getPosition()));
        if (piece instanceof Rook) getBoard().getSquare(getPosition().getX(), getPosition().getY())
                .setPiece(new Rook(getColour(), getPosition()));
    }

    /**
     * Enables the pawn to capture enemy pawn en passant.
     */
    public Piece EnPassant() {
        Piece captured = null;
        int y = getPosition().getY();
        int x = getPosition().getX() + 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied())
                if (getBoard().getSquare(x, y).getPiece() instanceof Pawn
                        && ((Pawn) getBoard().getSquare(x, y).getPiece()).enPassant) {
                    captured = getBoard().getSquare(x, y).getPiece();
                    getBoard().getSquare(x, y).setPiece(null);
                }
        }
        x = getPosition().getX() - 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied())
                if (getBoard().getSquare(x, y).getPiece() instanceof Pawn
                        && ((Pawn) getBoard().getSquare(x, y).getPiece()).enPassant) {
                    captured = getBoard().getSquare(x, y).getPiece();
                    getBoard().getSquare(x, y).setPiece(null);
                }
        }
        return captured;
    }

    @Override
    public boolean isValidMove(int x, int y) {
        //moving rules
        if (!((x == getPosition().getX()
                && (direction * (y - getPosition().getY())== 1 || direction * (y - getPosition().getY()) == 2))
                || (direction * (y - getPosition().getY()) == 1 && Math.abs(x - getPosition().getX()) == 1)))
            return false;
        //moving forward
        if (x == getPosition().getX()) {
            //first move
            if (!hasMoved) {
                //is something blocking?
                if (getBoard().getSquare(x, getPosition().getY() + direction).isOccupied()
                        || getBoard().getSquare(x, y).isOccupied())
                    return false;
            }
            //regular move
            else {
                //only one forward move
                if (direction * (y - getPosition().getY()) == 2) {
                    return false;
                }
                //is something blocking?
                if (getBoard().getSquare(x, y).isOccupied())
                    return false;
            }
        }
        //taking piece
        else {
            //default taking
            if (getBoard().getSquare(x, y).isOccupied()) {
                //your piece
                if (getBoard().getSquare(x, y).getPiece().getColour() == getColour())
                    return false;
            } else {
                //en passant
                if (getBoard().getSquare(x, getPosition().getY()).getPiece() == null)
                    return false;
                if (!(getBoard().getSquare(x, getPosition().getY()).getPiece() instanceof Pawn
                        && ((Pawn) getBoard().getSquare(x, getPosition().getY()).getPiece()).enPassant))
                    return false;
            }
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
        Pawn clonedPiece = new Pawn(this.getColour(), position);
        clonedPiece.enPassant = this.enPassant;
        clonedPiece.promotion = this.promotion;
        return clonedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pawn && ((Pawn) obj).getColour() == this.getColour()) {
            return true;
        }
        return false;
    }
}