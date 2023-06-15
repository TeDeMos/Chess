package com.example.chesss;

public record Move(int x0, int y0, int x1, int y1) {
    @Override
    public String toString() {
        return "%d,%d,%d,%d".formatted(x0, y0, x1, y1);
    }

    public static Move fromString(String s) throws IllegalArgumentException {
        String[] split = s.split(",");
        int x0 = Integer.parseInt(split[0]);
        int y0 = Integer.parseInt(split[1]);
        int x1 = Integer.parseInt(split[2]);
        int y1 = Integer.parseInt(split[3]);
        return new Move(x0, y0, x1, y1);
    }
}
