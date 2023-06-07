package org.example;

import java.util.ArrayList;

/**
 * The King class represents a king chess piece, which is the most important piece in the game and must be protected.
 * This class provides methods for handling king-specific moves, such as castling and checking.
 * <p>
 * Note: This class inherits from the abstract Piece class, and may need to implement its abstract methods or override
 * its inherited methods to provide specific king behavior.
 * <p>
 * Note: This class assumes that there is only one king per player. Other conventions may require adjustments.
 */
public class King extends Piece {
    /**
     * Constructor of the king.
     */
    public King(Colour colour, Square positionOfPiece) {
        super(colour, positionOfPiece, 9999999);
        hasMoved = false;
        direction = (colour == Colour.WHITE) ? 1 : -1;
        line = (colour == Colour.WHITE) ? 0 : 7;
    }

    /**
     * Indicator of direction, in which the pawns are moving; 1 for white, -1 for black
     */
    private int direction;
    /**
     * Number line, on which the king initially is located.
     */
    private int line;

    /**
     * Method indicating whether the king has been checked by an opponent piece.
     * Method checks every direction is order to find proper enemy piece.
     */
    public boolean hasBeenChecked() {
        //pawns
        int x = getPosition().getX() + 1;
        int y = getPosition().getY() + direction;
        if (isValidPlace(x, getPosition().getY() + direction)) {
            if (getBoard().getSquare(x, y).isOccupied()
                    && getBoard().getSquare(x, y).getPiece() instanceof Pawn
                    && getBoard().getSquare(x, y).getPiece().getColour() != getColour()) {
                return true;
            }
        }
        x = getPosition().getX() - 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()
                    && getBoard().getSquare(x, y).getPiece() instanceof Pawn
                    && getBoard().getSquare(x, y).getPiece().getColour() != getColour()) {
                return true;
            }
        }
        //bishops
        //+1,+1
        int xAdder = 1, yAdder = 1;
        int xIter = getPosition().getX() + xAdder;
        int yIter = getPosition().getY() + yAdder;
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Bishop
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            xIter += xAdder;
            yIter += yAdder;
        }
        //+1,-1
        xAdder = 1;
        yAdder = -1;
        xIter = getPosition().getX() + xAdder;
        yIter = getPosition().getY() + yAdder;
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Bishop
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            xIter += xAdder;
            yIter += yAdder;
        }
        //-1,+1
        xAdder = -1;
        yAdder = 1;
        xIter = getPosition().getX() + xAdder;
        yIter = getPosition().getY() + yAdder;
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Bishop
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            xIter += xAdder;
            yIter += yAdder;
        }
        //-1,-1
        xAdder = -1;
        yAdder = -1;
        xIter = getPosition().getX() + xAdder;
        yIter = getPosition().getY() + yAdder;
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Bishop
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            xIter += xAdder;
            yIter += yAdder;
        }
        //rooks
        //axis X
        xAdder = 1;
        xIter = getPosition().getX() + xAdder;
        yIter = getPosition().getY();
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Rook
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            xIter += xAdder;
        }
        xAdder = -1;
        xIter = getPosition().getX() + xAdder;
        yIter = getPosition().getY();
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Rook
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            xIter += xAdder;
        }
        //axis Y
        yAdder = 1;
        xIter = getPosition().getX();
        yIter = getPosition().getY() + yAdder;
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Rook
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            yIter += yAdder;
        }
        yAdder = -1;
        xIter = getPosition().getX();
        yIter = getPosition().getY() + yAdder;
        while (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece().getColour() == getColour()) break;
                else if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Rook
                        || getBoard().getSquare(xIter, yIter).getPiece() instanceof Queen) return true;
                break;
            }
            yIter += yAdder;
        }
        //knights
        //++, --
        xIter = getPosition().getX() + 2;
        yIter = getPosition().getY() + 1;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        xIter = getPosition().getX() + 1;
        yIter = getPosition().getY() + 2;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        xIter = getPosition().getX() - 2;
        yIter = getPosition().getY() - 1;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        xIter = getPosition().getX() - 1;
        yIter = getPosition().getY() - 2;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //+-, -+
        xIter = getPosition().getX() + 2;
        yIter = getPosition().getY() - 1;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        xIter = getPosition().getX() + 1;
        yIter = getPosition().getY() - 2;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        xIter = getPosition().getX() - 2;
        yIter = getPosition().getY() + 1;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        xIter = getPosition().getX() - 1;
        yIter = getPosition().getY() + 2;
        if (isValidPlace(xIter, yIter)) {
            if (getBoard().getSquare(xIter, yIter).isOccupied()) {
                if (getBoard().getSquare(xIter, yIter).getPiece() instanceof Knight
                        && getBoard().getSquare(xIter, yIter).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //king
        //1,1
        x = getPosition().getX() + 1;
        y = getPosition().getY() + 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //-1,-1
        x = getPosition().getX() - 1;
        y = getPosition().getY() - 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //1,-1
        x = getPosition().getX() + 1;
        y = getPosition().getY() - 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //-1,1
        x = getPosition().getX() - 1;
        y = getPosition().getY() + 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //1,0
        x = getPosition().getX() + 1;
        y = getPosition().getY();
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //0,1
        x = getPosition().getX();
        y = getPosition().getY() + 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //-1,0
        x = getPosition().getX() - 1;
        y = getPosition().getY();
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //0,-1
        x = getPosition().getX();
        y = getPosition().getY() - 1;
        if (isValidPlace(x, y)) {
            if (getBoard().getSquare(x, y).isOccupied()) {
                if (getBoard().getSquare(x, y).getPiece() instanceof King
                        && getBoard().getSquare(x, y).getPiece().getColour() != getColour())
                    return true;
            }
        }
        //all good
        return false;
    }

    /**
     * Flag indicating whether the king has moved from its original position (affecting castling options).
     */
    public boolean hasMoved;

    @Override
    public boolean isValidMove(int x, int y) {
        //castle
        if (x == 6 && y == line) {
            if (canShortCastle()) return true;
        }
        if (x == 2 && y == line) {
            if (canLongCastle()) return true;
        }
        //moving rules
        if (!(Math.abs(x - getPosition().getX()) <= 1 && Math.abs(y - getPosition().getY()) <= 1)) {
            return false;
        }
        if (x == getPosition().getX() && y == getPosition().getY())
            return false;

        //is legal?
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

    /**
     * Method indicating whether the king can do the long castle.
     * @return true if the king can do the long castle, false otherwise.
     */
    public boolean canLongCastle() {
        //is rook ok?
        if (!getBoard().getSquare(0, line).isOccupied()
                || !(getBoard().getSquare(0, line).getPiece() instanceof Rook
                || getBoard().getSquare(0, line).getPiece().getColour() != getColour()
                || ((Rook) getBoard().getSquare(0, line).getPiece()).hasMoved))
            return false;
        //has moved and is checked
        if (hasBeenChecked() || hasMoved) return false;
        //are squares free?
        if (getBoard().getSquare(1, line).isOccupied()) return false;
        if (getBoard().getSquare(2, line).isOccupied()) return false;
        if (getBoard().getSquare(3, line).isOccupied()) return false;
        //is pinned?
        Board helpingBoard = getBoard().cloneBoard();
        Piece helpingKing = helpingBoard.findPiece(new King(this.getColour(), new Square()));
        helpingBoard.getSquare(getPosition().getX(), getPosition().getY()).setPiece(null);
        getBoard().getSquare(2, line).setPiece(helpingKing);
        helpingKing.setPosition(getBoard().getSquare(2, line));
        if (((King) helpingKing).hasBeenChecked())
            return false;
        helpingBoard.getSquare(2, line).setPiece(null);
        getBoard().getSquare(3, line).setPiece(helpingKing);
        helpingKing.setPosition(getBoard().getSquare(3, line));
        if (((King) helpingKing).hasBeenChecked())
            return false;
        return true;
    }
    /**
     * Method indicating whether the king can do the short castle.
     * @return true if the king can do the short castle, false otherwise.
     */
    public boolean canShortCastle() {
        //is rook ok?
        if (!getBoard().getSquare(7, line).isOccupied()
                || !(getBoard().getSquare(7, line).getPiece() instanceof Rook
                || getBoard().getSquare(7, line).getPiece().getColour() != getColour()
                || ((Rook) getBoard().getSquare(7, line).getPiece()).hasMoved))
            return false;
        //has moved and is checked
        if (hasBeenChecked() || hasMoved) return false;
        //are squares free?
        if (getBoard().getSquare(6, line).isOccupied()) return false;
        if (getBoard().getSquare(5, line).isOccupied()) return false;
        //is pinned?
        Board helpingBoard = getBoard().cloneBoard();
        Piece helpingKing = helpingBoard.findPiece(new King(this.getColour(), new Square()));
        helpingBoard.getSquare(getPosition().getX(), getPosition().getY()).setPiece(null);
        getBoard().getSquare(6, line).setPiece(helpingKing);
        helpingKing.setPosition(getBoard().getSquare(6, line));
        if (((King) helpingKing).hasBeenChecked())
            return false;
        helpingBoard.getSquare(6, line).setPiece(null);
        getBoard().getSquare(5, line).setPiece(helpingKing);
        helpingKing.setPosition(getBoard().getSquare(5, line));
        if (((King) helpingKing).hasBeenChecked())
            return false;
        return true;
    }

    /**
     * Makes the long castle.
     */
    public void longCastle() {
        //rook
        Piece rook = getBoard().getSquare(0, line).getPiece();
        rook.getPosition().setPiece(null);
        getBoard().getSquare(3, line).setPiece(rook);
        rook.setPosition(getBoard().getSquare(3, line));
        ((Rook) rook).hasMoved = true;
        //king
        getPosition().setPiece(null);
        getBoard().getSquare(2, line).setPiece(this);
        setPosition(getBoard().getSquare(2, line));
    }

    /**
     * Makes the short castle.
     */
    public void shortCastle() {
        //rook
        Piece rook = getBoard().getSquare(7, line).getPiece();
        rook.getPosition().setPiece(null);
        getBoard().getSquare(5, line).setPiece(rook);
        rook.setPosition(getBoard().getSquare(5, line));
        ((Rook) rook).hasMoved = true;
        //king
        getPosition().setPiece(null);
        getBoard().getSquare(6, line).setPiece(this);
        setPosition(getBoard().getSquare(6, line));
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
        King clonedPiece = new King(this.getColour(), position);
        clonedPiece.hasMoved = this.hasMoved;
        return clonedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof King && ((King) obj).getColour() == this.getColour()) {
            return true;
        }
        return false;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }
}
