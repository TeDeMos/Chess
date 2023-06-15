package com.example.chesss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public abstract class NetworkObject {

    protected final ChessController controller;

    public NetworkObject(ChessController controller){
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

    protected abstract void send(String message) throws IOException;

    protected abstract String receive() throws IOException;
}
