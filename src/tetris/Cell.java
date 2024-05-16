package tetris;

import java.awt.image.BufferedImage;

/** Class for the cell in the Tetris game */
public class Cell {
    private int row;
    private int col;
    private BufferedImage image;
    
    // Constructor to initialize cell with position and image
    public Cell(int row, int col, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.image = image;
    }

    // Getter and setter for row
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    // Getter and setter for column
    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // Getter and setter for image
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    // Method to move the cell down by one row
    public void softDrop() {
        this.row++;
    }

    // Method to move the cell to the right
    public void moveRight() {
        col++;
    }

    // Method to move the cell to the left
    public void moveLeft() {
        col--;
    }
}
