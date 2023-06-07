package org.example;

import java.util.*;

/**
 * 
 */
public class Player {

    /**
     * Default constructor
     */
    public Player(Colour colour, String name) {
        this.colour = colour;
        this.name = name;
    }

    /**
     * Time of player.
     */
    private Timer time;

    /**
     * Colour of player.
     */
    private Colour colour;

    /**
     * Name of player.
     */
    private String name;

    /**
     * ArrayList of opponent's figures captured by player.
     */
    private ArrayList<Piece>piecesCaptured = new ArrayList<Piece>();

    /**
     * Client used for playing online.
     */
    private ChessClient networkClient;

    /**
     * GUI of player
     */
    private ChessGUI gameFrontend;

    /**
     * @return
     */
    public Square move(Square square1,Piece piece) {
        Square destination;

        return null;
    }
    public Colour getColour() {
        return colour;
    }

    /**
     * Method to resign a game
     */
    public void resign() {
        // TODO implement here
    }

    /**
     * Method to make a proposal to draw.
     */
    public void draw() {
        // TODO implement here
    }
    public void setPlayer(String s) {
        name = s;
    }
    public ArrayList<Piece> getPiecesCaptured() {
        return piecesCaptured;
    }

    @Override
    public String toString() {
        return name;
    }
}
