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
    private ServerSocket serverSocket;
    private Socket client;
    private Game game;
    private final static int port = 34569;
    private String hostName;
    private Colour hostColour;
    private final ChessController controller;
    public String clientName;

    public Server(String hostName, Colour hostColour, ChessController controller) {
        this.hostName = hostName;
        this.hostColour = hostColour;
        this.controller = controller;
        try {
            serverSocket = new ServerSocket(port);
            client = serverSocket.accept();
            send("name;%s;%s".formatted(hostColour == Colour.BLACK ? "white" : "black", hostName));
            clientName = receive();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void makeTurn(int x0, int y0, int x1, int y1) {
        try {
            send("m;%d;%d;%d;%d".formatted(x0, y0, x1, y1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

