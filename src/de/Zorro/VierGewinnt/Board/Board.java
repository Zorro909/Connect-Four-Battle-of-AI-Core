package de.Zorro.VierGewinnt.Board;

import java.util.LinkedList;

import de.Zorro.VierGewinnt.Main.VierGewinnt;

public class Board {

    private String[][] board;
    private int lastPlaced = 1;
    private LinkedList<Integer> turns = new LinkedList<Integer>();
    private boolean restrict = true;
    
    public Board(int width, int height, boolean restrict) {
        board = new String[width][height];
        for (int i = 0; i < width; i++) {
            for (int i2 = 0; i2 < height; i2++) {
                board[i][i2] = "u";
            }
        }
        this.restrict = restrict;
    }
    
    public Board copyBoard(){
        Board b = new Board(getWidth(),getHeight(),false);
        for(int x = 0;x<getWidth();x++){
            for(int y = 0;y<getHeight();y++){
                b.board[x][y] = String.valueOf(board[x][y]);        
            }
        }
        b.lastPlaced = Integer.valueOf(lastPlaced);
        return b;
    }

    public int getWidth() {
        return board.length;
    }
    
    public LinkedList<Integer> getPreviousTurns() {
        return turns;
    }


    public int getHeight() {
        return board[0].length;
    }

    public String setNew(int player, int x) {
        if (VierGewinnt.isNewBlockSettable() || !restrict) {
            if (player == lastPlaced) { return "e"; }
            if (!board[x][board[x].length - 1].equalsIgnoreCase("u")) { return "e"; }
            for (int i = board[x].length - 1; i >= 0; i--) {
                if (!board[x][i].equalsIgnoreCase("u")) {
                    board[x][i + 1] = (player == 0 ? "X" : "O");
                            turns.add(x);
                        String s = checkWinning();
                        if (!s.equalsIgnoreCase("u")) { return s; }
                    break;
                } else {
                    if (i == 0) {
                        board[x][0] = (player == 0 ? "X" : "O");
                                turns.add(x);
                            String s = checkWinning();
                            if (!s.equalsIgnoreCase("u")) { return s; }
                    }
                }
            }
            if (lastPlaced != -1) {
                lastPlaced = player;
            }
            return "g";
        }
        return "e";
    }

    public String checkWinning() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                String s = board[x][y];
                if (!s.equalsIgnoreCase("u")) {
                    if (x + 4 <= board.length) {
                        if (board[x + 1][y].equalsIgnoreCase(s)) {
                            if (board[x + 2][y].equalsIgnoreCase(s)) {
                                if (board[x + 3][y].equalsIgnoreCase(s)) { return s; }
                            }
                        }
                    }
                    if (y - 3 >= 0) {
                        if (board[x][y - 1].equalsIgnoreCase(s)) {
                            if (board[x][y - 2].equalsIgnoreCase(s)) {
                                if (board[x][y - 3].equalsIgnoreCase(s)) { return s; }
                            }
                        }
                    }
                    if (y + 4 <= board[0].length) {
                        if (board[x][y + 1].equalsIgnoreCase(s)) {
                            if (board[x][y + 2].equalsIgnoreCase(s)) {
                                if (board[x][y + 3].equalsIgnoreCase(s)) { return s; }
                            }
                        }
                    }
                    if (x + 4 <= board.length && y + 4 <= board[0].length) {
                        if (board[x + 1][y + 1].equalsIgnoreCase(s)) {
                            if (board[x + 2][y + 2].equalsIgnoreCase(s)) {
                                if (board[x + 3][y + 3].equalsIgnoreCase(s)) { return s; }
                            }
                        }
                    }
                    if (x - 3 >= 0 && y - 3 >= 0) {
                        if (board[x - 1][y - 1].equalsIgnoreCase(s)) {
                            if (board[x - 2][y - 2].equalsIgnoreCase(s)) {
                                if (board[x - 3][y - 3].equalsIgnoreCase(s)) { return s; }
                            }
                        }
                    }
                }
            }
        }
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (board[x][y].equalsIgnoreCase("u")) return "u";
            }
        }
        return "l";
    }

    public String getCell(int x, int y) {
        return board[x][y];
    }
}
