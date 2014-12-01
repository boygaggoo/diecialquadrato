package com.davide.activity.sqareten;

/**
 * Created by dcazzaniga on 10/07/14.
 */
public enum Mossa {

    N("North",          0,  -3,  0,  1),
    NE("NorthEst",      2,  -2, 1,  1),
    E("Est",            3,  0,  1,  0),
    SE("SudEst",        2,  +2,  1,  -1),
    S("Sud",            0,  +3, 0,  -1),
    SW("SudWest",       -2, +2, -1, -1),
    W("West",           -3, 0,  -1, 0),
    NW("NorthWest",     -2, -2,  -1, 1);

    private String nome;
    private int skipx, skipy, dx, dy;

    private Mossa(String nome, int skipx, int skipy, int dx, int dy) {
        this.nome = nome;
        this.skipx = skipx;
        this.skipy = skipy;
        this.dx = dx;
        this.dy = dy;
    }

    public static Mossa load(int dx, int dy) {
        for (Mossa m : Mossa.values()) {
            if (m.getDx() == dx && m.getDy() == dy) {
                return m;
            }
        }
        return null;
    }

    public String getNome() {
        return nome;
    }

    public int getSkipx() {
        return skipx;
    }

    public int getSkipy() {
        return skipy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
