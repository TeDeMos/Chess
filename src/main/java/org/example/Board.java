package org.example;

/**
 * The Board class represents the board where the game take a place.
 */
public class Board {

    /**
     * Constructor of the chessboard.
     */
    public Board(String s) {
        if (s.equalsIgnoreCase("empty")) {
            emptySetup();
        } else {
            setup();
        }
    }

    /**
     * 64-square board (8x8); [x][y] (B4 = [1][3], A1 = [0][0])
     */
    Square[][] board = new Square[8][8];

    /**
     * Makes an empty board.
     */
    public void emptySetup() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[j][i] = new Square(j, i, this);
            }
        }
    }

    /**
     * Set the placement of figures to the initial arrangement.
     */
    public void setup() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[j][i] = new Square(j, i, this);
            }
        }
        //pawns
        for (int i = 0; i < 8; i++) {
            board[i][1].setPiece(new Pawn(Colour.WHITE, board[i][1]));
        }
        for (int i = 0; i < 8; i++) {
            board[i][6].setPiece(new Pawn(Colour.BLACK, board[i][6]));
        }
        //kings
        board[4][0].setPiece(new King(Colour.WHITE, board[4][0]));
        board[4][7].setPiece(new King(Colour.BLACK, board[4][7]));
        //queens
        board[3][0].setPiece(new Queen(Colour.WHITE, board[3][0]));
        board[3][7].setPiece(new Queen(Colour.BLACK, board[3][7]));
        //bishops
        board[2][0].setPiece(new Bishop(Colour.WHITE, board[2][0]));
        board[5][0].setPiece(new Bishop(Colour.WHITE, board[5][0]));
        board[2][7].setPiece(new Bishop(Colour.BLACK, board[2][7]));
        board[5][7].setPiece(new Bishop(Colour.BLACK, board[5][7]));
        //knights
        board[1][0].setPiece(new Knight(Colour.WHITE, board[1][0]));
        board[6][0].setPiece(new Knight(Colour.WHITE, board[6][0]));
        board[1][7].setPiece(new Knight(Colour.BLACK, board[1][7]));
        board[6][7].setPiece(new Knight(Colour.BLACK, board[6][7]));
        //rooks
        board[0][0].setPiece(new Rook(Colour.WHITE, board[0][0]));
        board[7][0].setPiece(new Rook(Colour.WHITE, board[7][0]));
        board[0][7].setPiece(new Rook(Colour.BLACK, board[0][7]));
        board[7][7].setPiece(new Rook(Colour.BLACK, board[7][7]));
    }

    /**
     * Method that is used to move a piece on the board (doesn't check the correctness of given moves).
     */
    public Piece movePiece(Piece piece, int x, int y) {
        Piece captured = null;
        int direction = (piece.getColour() == Colour.WHITE) ? 1 : -1;
        int promotion = (piece.getColour() == Colour.WHITE) ? 7 : 0;
        int enemySecondLine = promotion - 3 * direction;
        int line = 7 - promotion;
        //knight, bishop, queen
        if (piece instanceof Knight || piece instanceof Queen || piece instanceof Bishop) {
            if (getSquare(x, y).isOccupied()) captured = getSquare(x, y).getPiece();
        }
        //rook
        if (piece instanceof Rook) {
            if (getSquare(x, y).isOccupied()) captured = getSquare(x, y).getPiece();
            ((Rook) piece).hasMoved = true;
        }
        //pawn
        if (piece instanceof Pawn) {
            if (getSquare(x, y).getPiece() == null && piece.getPosition().getX()!=x) {
                captured = ((Pawn) piece).EnPassant();
            }
            ((Pawn) piece).hasMoved = true;
            if (getSquare(x, y).isOccupied()) captured = getSquare(x, y).getPiece();
            if (Math.abs(piece.getPosition().getY() - y) == 2) {
                ((Pawn) piece).enPassant = true;
            } else ((Pawn) piece).enPassant = false;
            if (y == promotion) ((Pawn) piece).Promotion(new Queen(piece.getColour(), new Square()));
            Piece promotedPiece=piece.getPosition().getPiece();
            piece = promotedPiece;
            //if (y == promotion) ((Pawn) piece).promotion = true;
        }

        //king
        if (piece instanceof King) {
            if (getSquare(x, y).isOccupied()) captured = getSquare(x, y).getPiece();
            if (y == line && x == 6) {
                if (((King) piece).canShortCastle()) ((King) piece).shortCastle();
            }
            if (y == line && x == 2) {
                if (((King) piece).canLongCastle()) ((King) piece).longCastle();
            }
            ((King) piece).hasMoved = true;
        }
        //basic change of the position
        piece.getPosition().setPiece(null);
        piece.setPosition(board[x][y]);
        board[x][y].setPiece(piece);
        //if didn't enpassant -> remove the option
        for (int i = 0; i < 8; i++) {
            if (getSquare(i, enemySecondLine).isOccupied() && getSquare(i, enemySecondLine).getPiece() instanceof Pawn) {
                ((Pawn) getSquare(i, enemySecondLine).getPiece()).enPassant = false;
            }
        }
        return captured;
    }

    /**
     * Returns the square that is located on given coordinates.
     * @param x x coordinate
     * @param y y coordinate
     * @return square that is located on given coordinates
     */
    public Square getSquare(int x, int y) {
        return board[x][y];
    }

    /**
     * Clones the whole board (used for checking if the piece is bound).
     * @return cloned board
     */
    public Board cloneBoard() {
        Board clonedBoard = new Board("empty");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square clonedSquare = clonedBoard.getSquare(j, i);
                if (board[j][i].isOccupied()) {
                    Piece clonedPiece = board[j][i].getPiece().clonePiece(clonedSquare);
                    clonedSquare.setPiece(clonedPiece);
                }
            }
        }
        return clonedBoard;
    }

    /**
     * Finds the given piece on the board.
     * @param piece given piece (any piece that is of the same type and colour as a piece, we are looking for).
     * @return found piece
     */
    public Piece findPiece(Piece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getSquare(j, i).isOccupied()) {
                    if (getSquare(j, i).getPiece().equals(piece)) return getSquare(j, i).getPiece();
                }
            }
        }
        return null;
    }
}
