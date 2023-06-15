package com.example.chesss;

import org.example.Game;

import java.io.*;
import java.net.Socket;

public class Client extends NetworkObject{

    private final Socket server;
    private final static int port = 34569;
    int playerIndex;
    public String firstMessage;

    public Client(ChessController controller) throws IOException {
        super(controller);
        server = new Socket("localhost", port);
        firstMessage = receive();
    }

    @Override
    protected void send(String message) throws IOException {
        PrintWriter printWriter = new PrintWriter(server.getOutputStream(), true);
        printWriter.println(message);
    }

    @Override
    protected String receive() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        String message = bufferedReader.readLine();
        return message;
    }
}
