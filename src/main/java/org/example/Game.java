package org.example;


import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class Game: its instance is created at the beginning of each game
 * it permits to make a turn in a way according to the chess rules
 */
public class Game {

    /**
     * Default constructor
     */
    public Game(String name1, String name2, LocalDate date) {
        board = new Board("");
        player1 = new Player(Colour.WHITE, name1);
        player2 = new Player(Colour.BLACK, name2);
        whoseMove = player1;
        gameTime = date;
        setup();
    }
    public Game(String name1, String name2, LocalDate date, Board board) {
        this.board = board.cloneBoard();
        player1 = new Player(Colour.WHITE, name1);
        player2 = new Player(Colour.BLACK, name2);
        whoseMove = player1;
        gameTime = date;
        setup();
    }
    /**
     * The board that the game is played on
     * Initially with standard arrangement od pieces
     */
    private Board board;

    /**
     * First player that participates in the game
     */
    public Player player1;

    /**
     * Second player that participates in the game
     * Can be an instance of Computer class if game's mode is set so
     */
    public Player player2;

    /**
     * The player that should make their turn as next
     */
    public Player whoseMove;

    /**
     * Initial time of a game for each player
     * As the game starts, is passed to Player's instance as the initial value of timer
     */
    private LocalDate gameTime;

    /**
     * Describes if the Player whoseMove is currently checked
     */
    private boolean check;

    /**
     * Describes if the previous player has done a checkmate in their previous move
     */
    private boolean checkMate;

    /**
     * Describes if the game has reached the stalemate: each player has only the king that remains
     */
    private boolean staleMate;
    /**
     * Indicates the color of host
     */
    public Colour hostColor;

    /**
     * @return true if previous player has done a checkmate in their last move
     */
    public boolean isCheckMate() throws Exception {
        if (isCheck()) {
            if (whoseMove == player1) {
                for (int i = 0; i < 8; i++) {
                    for (int y = 0; y < 8; y++) {
                        if (board.getSquare(i, y).getPiece().getColour() == Colour.WHITE) {
                            if (board.getSquare(i, y).getPiece().getPossibleMoves().size() != 0) {
                                return false;
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    for (int y = 0; y < 8; y++) {
                        if (board.getSquare(i, y).getPiece().getColour() == Colour.BLACK) {
                            if (board.getSquare(i, y).getPiece().getPossibleMoves().size() != 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return true if each of players has only the king that remains
     */
    public boolean isStaleMate() {
        if (player1.getPiecesCaptured().size() == 15 && player2.getPiecesCaptured().size() == 15) {
            staleMate = true;
            return true;
        }
        if (whoseMove == player1) {
            for (int i = 0; i < 8; i++) {
                for (int y = 0; y < 8; y++) {
                    if (board.getSquare(i, y).getPiece().getColour() == Colour.WHITE) {
                        if (board.getSquare(i, y).getPiece().getPossibleMoves().size() != 0) {
                            return false;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int y = 0; y < 8; y++) {
                    if (board.getSquare(i, y).getPiece().getColour() == Colour.BLACK) {
                        if (board.getSquare(i, y).getPiece().getPossibleMoves().size() != 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return true if one of player's whoseMove pawns has reached the edge of the board,
     * else returns false
     */
    public boolean isPromotion() {
        if (whoseMove == player1) {
            for (int i = 0; i < 8; i++) {
                if (board.getSquare(i, 7).getPiece().getColour() == Colour.WHITE && board.getSquare(i, 7).getPiece().getValue() == 1) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                if (board.getSquare(i, 0).getPiece().getColour() == Colour.BLACK && board.getSquare(i, 0).getPiece().getValue() == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * player whoseMove
     * gets from a player whoseMove the move that they have chosen from getPossibleMoves()
     * then changes the state of the board
     * then changes the player whoseMove
     * then checks all the boolean methods
     * summons endGame() if needed
     */
    public boolean makeTurn(int x1, int y1, int x2, int y2) {
        if (!board.board[x1][y1].isOccupied()) {
            return false;
        }
        if (whoseMove.getColour() != board.board[x1][y1].getPiece().getColour()) {
            return false;
        }
        ArrayList<Square> possibleMoves = board.board[x1][y1].getPiece().getPossibleMoves();
        if (possibleMoves.isEmpty()) {
            return false;
        }
        if (possibleMoves.contains(board.board[x2][y2])) {
            Piece captured = board.movePiece(board.board[x1][y1].getPiece(), x2, y2);
            if (captured != null)
                whoseMove.getPiecesCaptured().add(captured);
            if (whoseMove == player1) {
                whoseMove = player2;
            } else {
                whoseMove = player1;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * sets initial values of all attributes
     */
    public void setup() {
        staleMate = false;
        checkMate = false;
        check = false;
    }

    /**
     * loads the state of a paused game from file
     */
    public Board loadGame(String filename) {
        File file = new File(filename);
        Board loadedBoardState = new Board("empty");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
            };
            Map<String, String> boardState = objectMapper.readValue(file, typeReference);
            for (Map.Entry<String, String> entry : boardState.entrySet()) {
                String coordinate = entry.getKey();
                String pieceName = entry.getValue();
                String[] coordinates = coordinate.split(";");
                String[] pieceCName = pieceName.split(" ");
                switch (pieceCName[1]) {
                    case "Pawn":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Pawn(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Pawn(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        }
                        break;
                    case "Bishop":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Bishop(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Bishop(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        }
                        break;
                    case "King":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new King(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new King(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        }
                        break;
                    case "Knight":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Knight(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Knight(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        }
                        break;
                    case "Queen":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Queen(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Queen(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        }
                        break;
                    case "Rook":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Rook(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Rook(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                        }
                        break;
                    case "RookM":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Rook(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                            ((Rook) (loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].getPiece())).hasMoved = true;
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new Rook(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                            ((Rook) (loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].getPiece())).hasMoved = true;
                        }
                        break;
                    case "KingM":
                        if (Objects.equals(pieceCName[0], "White")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new King(Colour.WHITE, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                            ((King) (loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].getPiece())).hasMoved = true;
                        } else if (Objects.equals(pieceCName[0], "Black")) {
                            loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].
                                    setPiece(new King(Colour.BLACK, new Square(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), loadedBoardState)));
                            ((King) (loadedBoardState.board[Integer.parseInt(coordinates[0])][Integer.parseInt(coordinates[1])].getPiece())).hasMoved = true;
                        }
                        break;
                    case "last":
                        if (Objects.equals(pieceCName[0], "player1")) {
                            whoseMove = player1;
                            System.out.println("whosemove is player1");
                        } else {
                            whoseMove = player2;
                            System.out.println("whosemove is player2");
                        }
                        break;
                    case "Host":
                        if(Objects.equals(pieceCName[0], "White")){
                            hostColor = Colour.WHITE;
                        }else{
                            hostColor = Colour.BLACK;
                        }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //board = loadedBoardState;
        return loadedBoardState;
    }
    public Player loadPlayer(String filename) {
        File file = new File(filename);
        Board loadedBoardState = new Board("empty");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
            };
            Map<String, String> boardState = objectMapper.readValue(file, typeReference);
            for (Map.Entry<String, String> entry : boardState.entrySet()) {
                String coordinate = entry.getKey();
                String pieceName = entry.getValue();
                String[] coordinates = coordinate.split(";");
                String[] pieceCName = pieceName.split(" ");
                if ("last".equals(pieceCName[1])) {
                    if (Objects.equals(pieceCName[0], "player1")) {
                        return player1;
                    } else {
                        return player2;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * saves the state of the game to a file when paused
     */
    public void saveBoardPosition(File file) {
        Map<String, String> boardState = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            for (int y = 0; y < 8; y++) {
                String position = String.valueOf(i) + ";" + String.valueOf(y);
                if (board.getSquare(i, y).isOccupied()) {
                    if (board.getSquare(i, y).getPiece().equals(new Pawn(Colour.WHITE, new Square()))) {
                        boardState.put(position, "White Pawn");
                    } else if (board.getSquare(i, y).getPiece().equals(new Pawn(Colour.BLACK, new Square()))) {
                        boardState.put(position, "Black Pawn");
                    } else if (board.getSquare(i, y).getPiece().equals(new Rook(Colour.BLACK, new Square()))) {
                        if (((Rook) board.getSquare(i, y).getPiece()).isHasMoved()) {
                            boardState.put(position, "Black RookM");
                        } else {
                            boardState.put(position, "Black Rook");
                        }
                    } else if (board.getSquare(i, y).getPiece().equals(new Rook(Colour.WHITE, new Square()))) {
                        if (((Rook) board.getSquare(i, y).getPiece()).isHasMoved()) {
                            boardState.put(position, "White RookM");
                        } else {
                            boardState.put(position, "White Rook");
                        }
                    } else if (board.getSquare(i, y).getPiece().equals(new Bishop(Colour.WHITE, new Square()))) {
                        boardState.put(position, "White Bishop");
                    } else if (board.getSquare(i, y).getPiece().equals(new Bishop(Colour.BLACK, new Square()))) {
                        boardState.put(position, "Black Bishop");
                    } else if (board.getSquare(i, y).getPiece().equals(new Queen(Colour.BLACK, new Square()))) {
                        boardState.put(position, "Black Queen");
                    } else if (board.getSquare(i, y).getPiece().equals(new Queen(Colour.WHITE, new Square()))) {
                        boardState.put(position, "White Queen");
                    } else if (board.getSquare(i, y).getPiece().equals(new Knight(Colour.WHITE, new Square()))) {
                        boardState.put(position, "White Knight");
                    } else if (board.getSquare(i, y).getPiece().equals(new Knight(Colour.BLACK, new Square()))) {
                        boardState.put(position, "Black Knight");
                    } else if (board.getSquare(i, y).getPiece().equals(new King(Colour.WHITE, new Square()))) {
                        if (((King) board.getSquare(i, y).getPiece()).isHasMoved()) {
                            boardState.put(position, "White KingM");
                        } else {
                            boardState.put(position, "White King");
                        }
                    } else if (board.getSquare(i, y).getPiece().equals(new King(Colour.BLACK, new Square()))) {
                        if (((King) board.getSquare(i, y).getPiece()).isHasMoved()) {
                            boardState.put(position, "Black KingM");
                        } else {
                            boardState.put(position, "Black King");
                        }
                    }
                } else {
                    boardState.put(position, "EMPTY EMPTY");
                }
            }
        }
        if (whoseMove == player1) {
            boardState.put("X;X", "player1 last");

        } else {
            boardState.put("X;X", "player2 last");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File("savedGame.json"), boardState);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return true if the player whoseMove is checked by the other player
     */
    public boolean isCheck() throws Exception {
        if (whoseMove == player1) {
            return ((King) board.findPiece(new King(Colour.WHITE, new Square()))).hasBeenChecked();
        } else {
            return ((King) board.findPiece(new King(Colour.BLACK, new Square()))).hasBeenChecked();
        }
    }


    /**
     * @return Board with its actual state
     * used by GUI to display the board with its pieces
     */
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board b) {
        board = b;
    }

    public void setPlayers(String p1, String p2) {
        player1.setPlayer(p1);
        player2.setPlayer(p2);
    }

    /**
     * when the boolean isStaleMate or isCheckMate returns true,
     * endGame() is summoned to pass this information to ChessGUI and free the memory
     */
    public void endGame() {
        // TODO implement here
    }

}
