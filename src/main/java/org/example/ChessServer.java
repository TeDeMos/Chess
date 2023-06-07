package org.example;

import javax.swing.plaf.ColorUIResource;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.*;

/**
 * Two - threaded server for communication between the two clients.
 * Service is its supporting structure which facilitates the communication
 * on the threads.
 *
 * @author janek
 */
public class ChessServer implements Runnable {
    /**
     * Enum designating the different states of server's FSM
     */
    private enum State {
        IDLE,
        WAIT,
        ACCEPTGUEST,
        WELCOME,
        ASKFORCOLOR,
        BEGIN,
        LOAD,
        WHOSTURN,
        GAMESETUP,
        MOVEHOST,
        MOVEGUEST,
        SURRENDERHOST,
        SURRENDERGUEST,
        DRAWHOST,
        DRAWGUEST,
        SAVEGAME,
        GAMEOVER
    }

    /**
     * Instance of game - the "brain" of the server
     */
    public Game game;
    /**
     * Gate for checking whether the designated move is valid or not
     */
    boolean valid;
    boolean load;
    String reply;
    /**
     * Variable representing the current state of server's FSM
     */
    private State currentState = State.WAIT;
    /**
     * Socket, on which the server communicates with clients
     */
    private ServerSocket serverSocket;
    /**
     * The two services operating on the two players
     */
    private final Service clients[] = new Service[2];
    /**
     * Current number of clients (max 2)
     */
    private int clientNr = 0;
    /**
     * Permanent socket number
     */
    private final int port = 3333;
    /**
     * Main thread of the server
     */
    private Thread serverThread;

    /**
     * Default constructor
     */
    public ChessServer() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        serverThread = new Thread(this);
        serverThread.start();
    }

    /**
     * Standard override od run() in Runnable interface
     */
    @Override
    public void run() {
        String move;
        while (serverThread == Thread.currentThread()) {
            switch (currentState) {
                case WAIT:
                    try {
                        Socket client = serverSocket.accept();
                        createNewClient(client);
                        clients[0].send(ask("What is your name?"));
                        clients[0].setName(clients[0].receive());
                        System.out.println("Added client no. " + clientNr);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    currentState = State.ACCEPTGUEST;
                    break;
                case ACCEPTGUEST:
                    try {
                        Socket client = serverSocket.accept();
                        createNewClient(client);
                        clients[0].send(ask("Do you want another client? (y/n)"));

                        String reply = clients[0].receive();
                        if (reply.equalsIgnoreCase("y")) {
                            System.out.println("Added client no. " + clientNr);
                            clients[1].send(ask("What is your name?"));
                            clients[1].setName(clients[1].receive());
                            currentState = State.WELCOME;
                            break;
                        } else {
                            System.out.println("Kicking out");
                            clientNr--;
                            clients[1].send(end());
                            currentState = State.ACCEPTGUEST;
                        }
                    } catch (IOException ioe) {
                        System.out.println("Error with accepting guest.");
                    }
                    break;
                case WELCOME:
                    System.out.println(clientNr);
                    for (int i = 0; i < 2; i++) {
                        clients[i].send(say("Welcome to the jungle, baby"));
                    }
                    currentState = State.LOAD;
                    break;
                case LOAD:
                    clients[0].send(ask("Do you want to load the previous game? (y/n)"));
                    reply = clients[0].receive();
                    if (reply.equalsIgnoreCase("n")) {
                        game = new Game(clients[0].getName(), clients[1].getName(), LocalDate.now());
                    } else if (reply.equalsIgnoreCase("y")) {
                        game = new Game(clients[0].getName(), clients[1].getName(), LocalDate.now());
                        Board newBoard = game.loadGame("savedGame.json");
                        game = new Game(clients[0].getName(), clients[1].getName(), LocalDate.now(), newBoard);
                        game.whoseMove = game.loadPlayer("savedGame.json");
                        /*System.out.println(game.whoseMove.toString());*/
                    }
                    currentState = State.ASKFORCOLOR;
                    break;
                case ASKFORCOLOR:
                    clients[0].send(ask("Do you want to be white? y/n"));
                    reply = clients[0].receive();
                    if (reply.equalsIgnoreCase("y")) {
                        clients[0].setWhite(true);
                        clients[1].setWhite(false);
                        game.player1 = new Player(Colour.WHITE, clients[0].getName());
                        game.player2 = new Player(Colour.BLACK, clients[1].getName());
                        clients[0].send(say("You're white. Congratulations"));
                        clients[1].send(say("You're black. We're sorry"));
                        currentState = State.BEGIN;
                    } else {
                        clients[1].setWhite(true);
                        clients[0].setWhite(false);
                        game.player1 = new Player(Colour.WHITE, clients[1].getName());
                        game.player2 = new Player(Colour.BLACK, clients[0].getName());
                        clients[1].send(say("You're white. Congratulations"));
                        clients[0].send(say("You're black. We're sorry"));
                        currentState = State.BEGIN;
                    }
                    break;
                case BEGIN:
                    boolean[] replies = new boolean[2];
                    clients[0].send(ask("Do you want to start the game now?"));
                    clients[1].send(ask("Do you want to start the game now?"));
                    if (clients[0].receive().equalsIgnoreCase("y")) {
                        replies[0] = true;
                    } else {
                        replies[0] = false;
                    }
                    if (clients[1].receive().equalsIgnoreCase("y")) {
                        replies[1] = true;
                    } else {
                        replies[1] = false;
                    }
                    if (replies[0] && replies[1]) {
                        for (int i = 0; i < 2; i++) {
                            clients[i].send(say("Let's start!"));
                        }
                    }
                        game.whoseMove=game.player1;
                        if (clients[0].isWhite()) {
                            System.out.println(clients[0].isWhite());
                            System.out.println("movehost");
                            currentState = State.MOVEHOST;
                        } else {
                            System.out.println(clients[0].isWhite());
                            System.out.println("moveguest");
                            currentState = State.MOVEGUEST;
                        }
                    break;
                case MOVEHOST:
                    System.out.println(game.whoseMove.toString());
                    clients[0].send(say("Your turn!"));
                    clients[1].send(say("Opponent's turn!"));
                    clients[0].send(pickMove(""));
                    move = clients[0].receive();
                    System.out.println("Player1: " + move);
                    int code = gameCommandProcessor(move);
                    /**
                     * Next state, as defined by the FSM and gameCommandProcessor
                     */
                    if (code == 1) {
                        clients[1].send(say("Opponent's move was: " + move));
                        currentState = State.MOVEGUEST;
                    } else if (code == 0) {
                        System.out.println("Invalid move from Client1");
                        clients[0].send(say("Invalid move, pick another"));
                        currentState = State.MOVEHOST;
                    } else if (code == -1) {
                        currentState = State.DRAWHOST;
                    } else {
                        currentState = State.SURRENDERHOST;
                    }
                    break;
                case MOVEGUEST:
                    clients[1].send(say("Your turn!"));
                    clients[0].send(say("Opponent's turn!"));
                    clients[1].send(pickMove(""));
                    move = clients[1].receive();
                    System.out.println(move);
                    code = gameCommandProcessor(move);
                    /**
                     * Next state, as defined by the FSM and gameCommandProcessor
                     */
                    if (code == 1) {
                        clients[0].send(say("Opponent's move was: " + move));
                        currentState = State.MOVEHOST;
                    } else if (code == 0) {
                        System.out.println("Invalid move from Client2");
                        clients[1].send(say("Invalid move, pick another"));
                        currentState = State.MOVEGUEST;
                    } else if (code == -1) {
                        currentState = State.DRAWGUEST;
                    } else if (code == -2) {
                        currentState = State.SURRENDERGUEST;
                    } else {
                        currentState = State.SAVEGAME;
                    }
                    break;
                case SURRENDERGUEST:
                    clients[1].send(say("You have officially surrendered"));
                    clients[0].send(say("Your opponent has surrendered, crushed by your power"));
                    currentState = State.GAMEOVER;
                    break;
                case SURRENDERHOST:
                    clients[0].send(say("You have officially surrendered"));
                    clients[1].send(say("Your opponent has surrendered, crushed by your power"));
                    currentState = State.GAMEOVER;
                    break;
                case DRAWHOST:
                    clients[0].send(say("You propose a draw"));
                    clients[1].send(ask("Your opponent proposes a draw. Do you accept? (y/n)"));
                    reply = clients[1].receive();
                    if (reply.equalsIgnoreCase("y")) {
                        currentState = State.GAMEOVER;
                    } else {
                        currentState = State.MOVEHOST;
                    }
                    break;
                case DRAWGUEST:
                    clients[1].send(say("You propose a draw"));
                    clients[0].send(ask("Your opponent proposes a draw. Do you accept? (y/n)"));
                    reply = clients[0].receive();
                    if (reply.equalsIgnoreCase("y")) {
                        currentState = State.GAMEOVER;
                    } else {
                        currentState = State.MOVEGUEST;
                    }
                    break;
                case SAVEGAME:
                    for (int i = 0; i < 2; i++) {
                        clients[i].send(say("Your game has been saved to server"));
                    }
                    currentState = State.GAMEOVER;
                    break;
                case GAMEOVER:
                    for (int i = 0; i < 2; i++) {
                        clients[i].send(say("You can close the game now. Goodbye!"));
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < 2; i++) {
                        clients[i].send(CommProtocol.END.toString() + "<" + "");
                    }
                    System.exit(0);
                    break;
            }
        }
    }

    /**
     * Method for creating new Services and delegating them to the next thread
     *
     * @param clientSocket
     * @throws IOException
     */
    synchronized void createNewClient(Socket clientSocket) throws IOException {
        Service client = new Service(clientSocket, this);
        clients[clientNr] = client;
        clientNr++;
        new Thread(client).start();
    }

    /**
     * Simplification of performing a RECEIVE on the client.
     * per the
     *
     * @param msg
     * @return information readable by Client
     */
    public String say(String msg) {
        String result = "";
        result += CommProtocol.RECEIVE.toString();
        result += "<";
        result += msg;
        return result;
    }

    /**
     * Simplification of performing a QUERY.
     *
     * @param msg
     * @return information readable by Client
     */
    public String ask(String msg) {
        String result = "";
        result += CommProtocol.QUERY.toString();
        result += "<";
        result += msg;
        return result;
    }

    /**
     * Simplification of performing a MOVE.
     *
     * @param msg
     * @return information readable by Client
     */
    public String pickMove(String msg) {
        String result = "";
        result += CommProtocol.MOVE.toString();
        result += "<";
        result += msg;
        return result;
    }

    /**
     * Simplification of performing sending an END.
     *
     * @return information readable by Client
     */
    public String end() {
        String result = "";
        result += CommProtocol.END.toString();
        result += "<";
        return result;
    }

    /**
     * Method for deciding what to do (what state comes next)
     * based on input form client
     *
     * @param move
     * @return number, the value of which determines server's action
     */
    public int gameCommandProcessor(String move) {
        if (move.equalsIgnoreCase("DRAW")) {
            return -1;
        } else if (move.equalsIgnoreCase("surrender")) {
            return -2;
        } else if (move.equalsIgnoreCase("savemygame")) {
            game.saveBoardPosition(new File("board"));
            return -3;
        }
        String[] array = move.split(" ");
        if (game.makeTurn(Integer.parseInt(array[0]), Integer.parseInt(array[1]),
                Integer.parseInt(array[2]), Integer.parseInt(array[3]))) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        new ChessServer();
    }

}