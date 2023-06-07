package org.example;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *  ChessGUI is a class responsible for graphic user interface and has all the methods that describe possible user behavior
 */
public class ChessGUI {

    /**
     * Default constructor
     */
    public ChessGUI() {
    }

    /**
     *  Board that contains Squares
     *  Basically is the chess board
     */
    public Board board;

    /**
     * The first player
     */
    private Player player1;

    /**
     * The second player
     */
    private Player player2;

    /**
     * Piece which is selected to be moved
     */
    private Piece selectedPiece;

    /**
     * Contains performed moves in a game
     */
    public List<Square> moves;

    /**
     * Inform which player is making move
     */
    public Color turn;

    /**
     * Timer measures time of a turn
     */
    private Timer timer;

    /**
     * Updates the sate of chess board
     */
    public void update() {
        // TODO implement here
    }

    /**
     * Graphical representation of chess board
     */
    public void drawBoard() {
        // TODO implement here
    }

    /**
     * Graphical representation of chess pieces
     */
    public void drawPieces() {
        // TODO implement here
    }

    /**
     * It's responsible for action after piece being clicked
     */
    public void mouseClicked() {
        // TODO implement here
    }


    /**
     *  It's responsible for action after piece being released
     */
    public void mouseReleased() {
        // TODO implement here
    }

    /**
     *  It's responsible for action after piece being dragged
     */
    public void mouseDragged() {
        // TODO implement here
    }


    /**
     * This method saves the game state that can be reloaded later
     */
    public void saveGame() {
        // TODO implement here
    }

    /**
     * This method customizes look of a chess board
     * @param skin
     */
    public void customize(Skin skin) {
        // TODO implement here
    }

    /**
     * Displays pieces taken by players in game
     */
    public void showTakenPieces() {
        // TODO implement here
    }

    /**
     * This method lets user to change their in game name
     */
    public void createPlayerName() {
        // TODO implement here
    }

}