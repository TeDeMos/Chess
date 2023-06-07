package com.example.chesss;

import javafx.scene.image.Image;

public class ImageHandler {
    public static Image corner;
    public static Image b;
    public static Image n2;
    public static Image black;
    public static Image white;

    public static void init() {
        corner = new Image("/com/example/chesss/corner.png");
        b = new Image("/com/example/chesss/b.png");
        n2 = new Image("/com/example/chesss/2.png");
        black = new Image("/com/example/chesss/black.png");
        white = new Image("/com/example/chesss/white.png");
    }
}
