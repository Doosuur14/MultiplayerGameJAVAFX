package com.example.multiplayergame4;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {

    private double x, y, width, height;
    private Color color;
    private Rectangle rectangle;

    public Player(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.rectangle = new Rectangle();

        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setFill(color);
    }

    public Rectangle createPlayer() {
        return rectangle;
    }

    public void moveX(double n) {
        x += n;
        rectangle.setLayoutX(x);
    }

    public void moveY(double n) {
        y += n;
        rectangle.setLayoutY(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
