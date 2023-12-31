package com.example.multiplayergame4;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Game implements Initializable {

    @FXML
    AnchorPane scene;

    @FXML
    private Rectangle platform, platform1, platform2, platform3, platform4, platform5,
            platform6, platform7;

    @FXML
    private Circle fruit, fruit1, fruit2, fruit3, fruit4, fruit5, fruit6,
            fruit7, fruit8, fruit9, fruit10, fruit11, fruit12, fruit13, fruit14;

    @FXML
    private Label scores;


    @FXML
    private TextField chatInput;

    @FXML
    private ListView<String> chatMessages;



    private Player me, opponent;
    private double oX, oY;
    private int playerID;

    private int p1Score, p2Score;

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private int movementVariable = 5;

    private Socket socket;

    private ReadFromServer readFromServer;
    private WriteToServer writeToServer;


    public Game() {

    }

    AnimationTimer opponentMoves = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if(opponent != null) {
                opponent.setX(opponent.getX());
                opponent.setY(opponent.getY());
            }
        }
    };

    private class ReadFromServer implements Runnable {

        private DataInputStream dataInputStream;

        public ReadFromServer(DataInputStream dataInputStream) {
            this.dataInputStream = dataInputStream;
            System.out.println("RFS Runnable created");
        }

        @Override
        public void run() {
            while (true) {
                if (opponent != null) {
                    try {
                        final double newX = dataInputStream.readDouble();
                        final double newY = dataInputStream.readDouble();

                        Platform.runLater(() -> {
                            double deltaX = newX - opponent.getX();
                            double deltaY = newY - opponent.getY();
                            opponent.moveX(deltaX);
                            opponent.moveY(deltaY);
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public void waitForStartMsg() {
            try {
                String startMsg = dataInputStream.readUTF();
                System.out.println("message from server " + startMsg);

                if (playerID == 1) {
                    opponent = new Player(820, 35, 35, 35, Color.RED);
                    scene.getChildren().add(opponent.createPlayer());
                }else if (playerID == 2) {
                    opponent = new Player(35, 580,35, 35, Color.BLUE);
                    scene.getChildren().add(opponent.createPlayer());
                }

                Thread readThread = new Thread(readFromServer);
                Thread writeThread = new Thread(writeToServer);
                readThread.start();
                writeThread.start();
            }catch (IOException e) {
                System.out.println("IOException from waitForStartMsg()");
            }
        }
    }


    public void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            writeToServer.sendMessage(message);
            chatInput.clear();
        }
    }


    private class WriteToServer implements Runnable {

        private DataOutputStream dataOutputStream;

        public WriteToServer(DataOutputStream dataOutputStream) {
            this.dataOutputStream = dataOutputStream;
            System.out.println("WTS Runnable created");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (me != null) {
                        dataOutputStream.writeDouble(me.getX());
                        dataOutputStream.writeDouble(me.getY());
                        dataOutputStream.flush();
                    }
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e) {
                        System.out.println("Interrupted Exception From WTS run()");
                    }
                }
            }catch (IOException e) {
                System.out.println("IOException From WTS run()");
            }
        }

        public void sendMessage(String message) {
            try {
                chatOutputStream.writeUTF(message);
                chatOutputStream.flush();
            } catch (IOException e) {
                System.out.println("IOException from WriteToServer sendMessage()");
            }
        }

    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", 1234);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            playerID = dataInputStream.readInt();
            System.out.println("Your are player#" + playerID);
            if (playerID == 1) {
                System.out.println("Waiting for player number 2");
            }
            readFromServer = new ReadFromServer(dataInputStream);
            writeToServer = new WriteToServer(dataOutputStream);
            readFromServer.waitForStartMsg();

        }catch (IOException e) {
            System.out.println("IOException from connectToServer");
        }
    }

    AnimationTimer fruitCollision = new AnimationTimer() {
        private List<Circle> collidedFruits = new ArrayList<>();

        @Override
        public void handle(long l) {
            List<Player> players = Arrays.asList(me, opponent);
            List<Circle> fruits = Arrays.asList(
                    fruit, fruit1, fruit2, fruit3, fruit4, fruit5, fruit6,
                    fruit7, fruit8, fruit9, fruit10, fruit11, fruit12, fruit13, fruit14
            );

            for (Player player : players) {
                Rectangle playerBound = player.createPlayer();
                for (Circle fruit : fruits) {
                    if (!collidedFruits.contains(fruit) && playerBound.getBoundsInParent().intersects(fruit.getBoundsInParent())) {
                        collidedFruits.add(fruit);
                        scene.getChildren().remove(fruit);

                        if (player == me) {
                            p1Score++;
                        } else if (player == opponent) {
                            p2Score++;
                        }

                        updateScore();
                    }
                }
            }
        }
    };

    public void updateScore() {
        Platform.runLater(() -> {
            scores.setText("Player 1: " + p1Score + " | Player 2: " + p2Score);
        });
    }

    AnimationTimer platformCollision = new AnimationTimer() {
        @Override
        public void handle(long l) {
            List<Player> players = Arrays.asList(me, opponent);
            List<Rectangle> platforms = Arrays.asList(
                    platform, platform1, platform2, platform3,
                    platform4, platform5, platform6, platform7
            );

            for (Player player : players) {

                double playerTop = player.getY();
                double playerBottom = player.getY() + player.getHeight();
                double playerLeft = player.getX();
                double playerRight = player.getX() + player.getWidth();

                Rectangle playerBound = player.createPlayer();
                for (Rectangle platform : platforms) {
                    double platformTop = platform.getLayoutY();
                    double platformBottom = platform.getLayoutY() + platform.getHeight();
                    double platformLeft = platform.getLayoutX();
                    double platformRight = platform.getLayoutX() + platform.getWidth();
                    if (playerBound.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                        if (playerBottom > platformTop && playerTop < platformTop) {
                            player.setY(platformTop - player.getHeight());
                        }
                        if (playerTop < platformBottom && playerBottom > platformBottom) {
                            player.setY(platformBottom);
                        }
                        if (playerLeft < platformRight && playerRight > platformRight) {
                            player.setX(platformRight);
                        }
                        if (playerRight > platformLeft && playerLeft < platformLeft) {
                            player.setX(platformLeft - player.getWidth());
                        }
                    }
                }
            }
        }
    };

    AnimationTimer moves = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if (wPressed.get()) {
                me.moveY(-movementVariable);
            }
            if (sPressed.get()) {
                me.moveY(movementVariable);
            }
            if (aPressed.get()) {
                me.moveX(-movementVariable);
            }
            if (dPressed.get()) {
                me.moveX(movementVariable);
            }
        }
    };


    public void movementSetup() {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    wPressed.set(true);
                    break;
                case S:
                    sPressed.set(true);
                    break;
                case A:
                    aPressed.set(true);
                    break;
                case D:
                    dPressed.set(true);
                    break;
                default:
                    break;
            }
        });

        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case W:
                    wPressed.set(false);
                    break;
                case S:
                    sPressed.set(false);
                    break;
                case A:
                    aPressed.set(false);
                    break;
                case D:
                    dPressed.set(false);
                    break;
                default:
                    break;
            }
        });
    }

    public void addPlayer() {

        if (playerID == 1) {
            me = new Player(35, 580, 35, 35, Color.BLUE);
        }else if (playerID == 2){
            me = new Player(850, 35, 35, 35, Color.RED);
        }
        scene.getChildren().add(me.createPlayer());
    }

    /*@Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BLABLA");
        stage.setScene(scene);
        scene.getRoot().requestFocus();

        stage.show();
    }

    public static void main (String[] args) {
        launch(args);
    }*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToServer();
        addPlayer();
        movementSetup();
        moves.start();
        opponentMoves.start();

        platformCollision.start();
        fruitCollision.start();
    }
}