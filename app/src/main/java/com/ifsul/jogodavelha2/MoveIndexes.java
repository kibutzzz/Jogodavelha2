package com.ifsul.jogodavelha2;

public class MoveIndexes {
    private int col;
    private int row;

    public MoveIndexes(int row, int col) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public boolean isValid() {
        if (this.col < 0 || this.row < 0){
            return false;
        }
        return true;
    }
}
