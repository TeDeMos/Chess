package com.example.chesss;

import org.example.Game;

import java.io.*;
import java.net.Socket;

public class Client {

    private final Socket server;
    private final static int port = 34569;
    private Game game;
    int playerIndex;
    public String firstMessage;
    private ChessController controller;

    public Client() {
        try {
            server = new Socket("localhost", port);
            firstMessage = receive();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(ChessController controller, Game game) {
        this.controller = controller;
        this.game = game;
        new Thread(this::work).start();
    }

    private void work() {
        try {
            while (true) {
                String message = receive();
                if (message.startsWith("m")) {
                    String[] split = message.split(";");
                    int x0 = Integer.parseInt(split[1]);
                    int y0 = Integer.parseInt(split[2]);
                    int x1 = Integer.parseInt(split[3]);
                    int y1 = Integer.parseInt(split[4]);
                    controller.moveOpponent(x0, y0, x1, y1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeTurn(int x0, int y0, int x1, int y1) {
        try {
            send("m;%d;%d;%d;%d".formatted(x0, y0, x1, y1));
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
