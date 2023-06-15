package com.example.chesss;

import org.example.Colour;
import org.example.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class Server {
    private final ServerSocket serverSocket;
    private final Socket client;
    private final static int port = 34569;
    private final ChessController controller;
    public String clientName;

    private Server(ChessController controller) throws IOException {
        this.controller = controller;
        serverSocket = new ServerSocket(port);
        client = serverSocket.accept();
    }

    public static Server newGame(String hostName, Colour hostColour, ChessController controller) throws IOException {
        Server server = new Server(controller);
        server.send("name|%s|%s".formatted(hostColour, hostName));
        server.clientName = server.receive();
        return server;
    }

    public static Server loadGame(String content, Colour hostColour, ChessController controller) throws IOException {
        Server server = new Server(controller);
        server.send("game|%s|%s".formatted(hostColour, content));
        return server;
    }

    public void start() {
        new Thread(this::work).start();
    }

    private void work() {
        try {
            while (true) {
                String message = receive();
                if (message.startsWith("m")) {
                    String[] split = message.split(";");
                    Move move = Move.fromString(split[1]);
                    controller.moveOpponent(move.x0(), move.y0(), move.x1(), move.y1());
                } else if (message.startsWith("r")) {
                    controller.resignOpponent();
                } else if (message.startsWith("d")) {
                    controller.requestDrawOpponent();
                } else if (message.startsWith("y")) {
                    controller.acceptDrawOpponent();
                } else if (message.startsWith("n")) {
                    controller.declineDrawOpponent();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(String message) throws IOException {
        PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
        printWriter.println(message);
    }

    private String receive() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String message = bufferedReader.readLine();
        System.out.printf("Server received: %s\n", message);
        return message;
    }

    public void makeTurn(Move move) {
        try {
            send("m;%s".formatted(move));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resign() {
        try {
            send("r");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestDraw() {
        try {
            send("d");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void respondDraw(boolean response) {
        try {
            if (response)
                send("y");
            else
                send("n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


