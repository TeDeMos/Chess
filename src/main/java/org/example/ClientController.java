package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * An interface through which clients can interact with the server.
 */
public class ClientController implements Runnable {
    /**
     * Socket to communicate with the server
     */
    private Socket socket;
    /**
     * The thre input / output controllers to receive
     * and send commands to and from the server and the terminal
     */
    private BufferedReader input;
    private BufferedReader keyboard;
    private PrintWriter output;

    /**
     * The constructor already takes host's name
     * and port number.
     *
     * @param host
     * @param port
     * @throws SocketException
     */
    public ClientController(String host, int port) {
        try {
            this.socket = new Socket(host, port);
        } catch (IOException ioe) {
            System.out.println("Unable to connect");
        }

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            keyboard = new BufferedReader(new InputStreamReader(System.in));
        } catch (SocketException se) {
            System.out.println("Error when opening socket.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        new Thread(this).start();
    }

    /**
     * Standerd override of run method.
     * Inside, there are defined the states of client's FSM.
     */
    @Override
    public void run() {
        while (true) {
            String move;
            try {
                String[] commandArgs = receive();
                CommProtocol command = CommProtocol.valueOf(commandArgs[0]);
                switch (command) {
                    case QUERY:
                        System.out.println(commandArgs[1]);
                        output.println(keyboard.readLine());
                        break;
                    case RECEIVE:
                        System.out.println(commandArgs[1]);
                        break;
                    case MOVE:
                        move = keyboard.readLine();
                        output.println(move);
                        break;
                    case END:
                        System.out.println("Goodbye");
                        System.exit(0);
                    case NULLCOMMAND:
                        break;
                }
            } catch (SocketException se) {
                System.out.println(se.getMessage());
                System.out.println("Critical error. Terminating");
                System.exit(0);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Method to handle messages constructed using CommandProtocol.
     * It splits the enum header from the rest of the message
     * to use it to determine the next stage of the FSM.
     *
     * @return String[] output
     * @throws IOException
     */
    public String[] receive() throws IOException {
        String[] output = input.readLine().split("<");
        return output;
    }

    public static void main(String[] args) {
        try {
            ClientController c1 = new ClientController("localhost", 3333);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
