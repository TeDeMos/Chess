package com.example.chesss;

import org.example.Game;

import java.io.*;
import java.net.Socket;

public class Client {

    private final Socket server;
    private final static int port = 34569;
    int playerIndex;
    public String firstMessage;
    private final ChessController controller;

    public Client(ChessController controller) throws IOException {
        server = new Socket("localhost", port);
        firstMessage = receive();
        this.controller = controller;
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

    public void send(String message) throws IOException {
        PrintWriter printWriter = new PrintWriter(server.getOutputStream(), true);
        printWriter.println(message);
    }

    private String receive() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        String message = bufferedReader.readLine();
        return message;
    }
}
