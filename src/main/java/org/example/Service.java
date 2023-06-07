package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The servive is used to handle each one of the two threads
 */
public class Service implements Runnable{
    /**
     * name of the host
     */
    private final ChessServer host;
    /**
     * the host's socket
     */
    private Socket clientSocket;
    /**
     * The objects controlling the input and output of the sockets
     */
    private BufferedReader input;
    private PrintWriter output;
    /**
     * used to assign a color to the service. Makes the code more readable
     */
    boolean white;
    /**
     * A name chosen by the client
     */
    private String name;

    /**
     * Constructor of the service
     * @param clientSocket
     * @param host
     * @throws IOException
     */
    public Service(Socket clientSocket,ChessServer host) throws IOException {
        this.host = host;
        this.clientSocket = clientSocket;

        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        output = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    /**
     * Standard necessary override of the run() method
     * of Runnable interface
     */
    public void run() {}

    /**
     * Sends the given message to the host
     * @param message
     */
    public void send(String message) {
        output.println(message);
    }

    /**
     * Receives the message incoming from the host
     * @return
     */
    public String receive() {
        try {
            return input.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * closing the sockets
     */
    void close() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client" + e);
        } finally {
            output = null;
            input = null;
            clientSocket = null;
        }
    }

    /**
     * setter for the boolean
     * @param b
     */
    public void setWhite(boolean b) {
        white = b;
    }

    /**
     * getter for the boolean
     * @return
     */
    public boolean isWhite() {
        return white;
    }
    public synchronized void stop() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
