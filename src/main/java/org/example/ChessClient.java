package org.example;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Class used to connect the user to the ChessServer socketServer that enables online play
 */
public class ChessClient {

    /**
     * Default constructor
     */
    public ChessClient() {
    }

    /**
     * This variable is the key component to make everything work. It contains the information about our user connection
     */
    private Socket socket;

    /**
     *
     */
    private BufferedReader reader;

    /**
     * 
     */
    private PrintWriter writer;

    /**
     *
     */
    public String playerName;

    /**
     * 
     */
    public String opponentName;

    /**
     * Used to initialize a game room or to connect to an already exisiting one
     */
    private String gameId;

    /**
     * 
     */
    public void ChessClient() {
        // TODO implement here
    }

    /**
     * 
     */
    public void connect() {
        // TODO implement here
    }

    /**
     * 
     */
    public void disconnect() {
        // TODO implement here
    }

    /**
     * Sends a message to another user
     * @param  message
     */
    public void sendMessage( String message) {
        // TODO implement here
    }

    /**
     * Function to handle received messages
     */
    public void receiveMessage() {
        // TODO implement here
    }

}