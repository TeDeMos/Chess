package com.example.chesss;

import org.example.Colour;
import org.example.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class Server extends NetworkObject {
    private final ServerSocket serverSocket;
    private final Socket client;
    private final static int port = 34569;
    public String clientName;

    private Server(ChessController controller) throws IOException {
        super(controller);
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

    @Override
    protected void send(String message) throws IOException {
        PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
        printWriter.println(message);
    }

    @Override
    protected String receive() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String message = bufferedReader.readLine();
        System.out.printf("Server received: %s\n", message);
        return message;
    }

}


