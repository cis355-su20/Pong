import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;

public class Pong2P extends Application {
    public enum Direction{
        UP,DOWN,NONE
    }
    Pane root;
    Scene scene;
    Stage primaryStage;
    double maxWidth;
    double maxHeight;
    Direction p1Input=Direction.NONE;
    Direction p2Input=Direction.NONE;
    Player p1;
    Player p2;
    Circle ball;
    int ballDX;
    int ballDY;
    int ballSpeed=5;
    Random dir;
    AnimationTimer timer;
    int playerWidth=20;
    int playerHeight=80;
    Text p1Display;
    Text p2Display;
    Button btStart;
    Label result;
    int toWin=5;
    boolean running=true;
    public static void main(String args[]){
        launch(args);
    }
    private void initScene(Stage primaryStage) {
        root = new Pane();
        root.setStyle("-fx-background-color: black");
        primaryStage.setTitle("Hello World");
        scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        Bounds bounds = root.getBoundsInLocal();
        maxWidth = bounds.getMaxX();
        maxHeight = bounds.getMaxY();
        p1Display = new Text();
        p2Display = new Text();
        p1Display.setTranslateX(maxWidth/3);
        p2Display.setTranslateX(2*maxWidth/3);
        p1Display.setTranslateY(50);
        p2Display.setTranslateY(50);
        p1Display.setFont(Font.font("Consolas",30));
        p1Display.setFill(Color.WHITE);
        btStart = new Button("Start Game");
        p2Display.setFont(Font.font("Consolas",30));
        p2Display.setFill(Color.WHITE);
        btStart.setLayoutX(maxWidth/2-150);
        btStart.setLayoutY(maxHeight/2);
        btStart.setStyle("-fx-background-color: transparent;-fx-border-color: white;");
        btStart.setFont(Font.font("Consolas",50));
        btStart.setTextFill(Color.WHITE);
        result= new Label();
        result.setLayoutX(maxWidth/2 -180);
        result.setLayoutY(maxHeight/2 + 100);
        result.setTextFill(Color.WHITE);
        result.setFont(Font.font("Consolas",50));
        // btStart.setVisible(false);



        root.getChildren().addAll(p1Display,p2Display,btStart,result);
    }
    public void resetGame(){
        resetBall();
        resetPlayer();
        timer.start();

    }

    public void resetPlayer(){
        p1.setTranslateX(0);
        p1.setTranslateY(maxHeight/2);
        p2.setTranslateX(maxWidth-playerWidth);
        p2.setTranslateY(maxHeight/2);
        p1.score.setValue(0);
        p2.score.setValue(0);

    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage=primaryStage;

        initScene(primaryStage); // basic scene layouts
        initGameWorld();
        //initGame();
    }
    public void initGameWorld(){
        dir=new Random();
        p1= new Player(playerWidth,playerHeight,Color.GREEN);
        p2= new Player(playerWidth,playerHeight,Color.RED);
        p1Display.textProperty().bind(p1.score.asString());
        p2Display.textProperty().bind(p2.score.asString());
        resetPlayer();
        ball=new Circle();
        ball.setRadius(20);
        ball.setVisible(false);
        ball.setFill(Color.WHITE);
        resetBall();
        root.getChildren().addAll(p1,p2,ball);

        btStart.setOnAction(e->{
            ball.setVisible(true);
            result.setVisible(false);
            btStart.setVisible(false);
            running=true;
            if (result.getText().equals(""))
                initGame();
            else
                resetGame();
        });
    }
    public void resetBall(){
        ball.setTranslateX(maxWidth/2);
        ball.setTranslateY(maxHeight/2);
        ballDX=dir.nextInt(2);
        if(ballDX ==1)
            ballDX=-ballSpeed;
        else
            ballDX=ballSpeed;
        ballDY=dir.nextInt(2);
        if(ballDY ==1)
            ballDY=-ballSpeed;
        else
            ballDY=ballSpeed;
    }
    private void initGame() {


        scene.setOnKeyPressed(e->{
            // System.out.println("Here2 "+e.getCode());

            KeyCode ip = e.getCode();
            if(ip == KeyCode.I)
                p2Input=Direction.UP;
            else if (ip == KeyCode.K)
                p2Input=Direction.DOWN;


            if(ip == KeyCode.W)
                p1Input=Direction.UP;
            else if (ip == KeyCode.S)
                p1Input=Direction.DOWN;


        });

        scene.setOnKeyReleased(e->{
            KeyCode ip = e.getCode();
            if(ip == KeyCode.I || ip == KeyCode.K)
                p2Input=Direction.NONE;
            else if(ip == KeyCode.W || ip == KeyCode.S)
                p1Input=Direction.NONE;
        });
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                gameLoop();
            }

        };
        timer.start();

    }

    private void gameLoop() {
        if(!running)
            return;
        //check for p1 movement
        switch(p1Input) {
            case UP: if(p1.getTranslateY() > 0)p1.moveUp();break;
            case DOWN: if(p1.getTranslateY() < maxHeight - playerHeight)p1.moveDown();break;
        }
        switch(p2Input) {
            case UP: if(p2.getTranslateY() > 0)p2.moveUp();break;
            case DOWN: if(p2.getTranslateY() < maxHeight - playerHeight)p2.moveDown();break;
        }

        //move ball
        ball.setTranslateX(ball.getTranslateX()+ballDX);
        ball.setTranslateY(ball.getTranslateY()+ballDY);

        //check for top and bottom hit
        if(ball.getTranslateY() < ball.getRadius() || ball.getTranslateY() > maxHeight-ball.getRadius())
            ballDY=-ballDY;

        //check for left and right hit
        if(ball.getTranslateX() <= ball.getRadius()) { // left side hit, p2 scores,reset ball to middle
            p2.score.setValue(p2.score.getValue()+1);
            if(p2.score.getValue() == toWin) {
                timer.stop();
                running=false;
            }
            resetBall();

        }
        else if (ball.getTranslateX() >= maxWidth-ball.getRadius()) { // right side hit, p1 scores,reset ball to middle
            p1.score.setValue(p1.score.getValue()+1);
            if(p1.score.getValue() == toWin) {
                timer.stop();
                running=false;
            }
            resetBall();
        }

        //handle collision
        if(ball.getBoundsInParent().intersects(p1.getBoundsInParent()) || ball.getBoundsInParent().intersects(p2.getBoundsInParent()) ){
            // System.out.println("Collision!");
            ballDX=-ballDX;
        }
        if(!running) {
            ball.setVisible(false);
            result.setVisible(true);
            btStart.setVisible(true);
            btStart.setText("Restart ?");
            if(p1.score.getValue() == toWin)
                result.setText("Player 1 Wins!");
            else
                result.setText("Player 2 Wins!");
        }

    }



}

class Player extends Rectangle {
    boolean dead=false;
    int speed=5;
    boolean id;
    int playerWidth;
    int playerHeight;
    IntegerProperty score;
    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


    Player(int playerWidth,int playerHeight, Color color){
        super();
        setHeight(playerHeight);
        setWidth(playerWidth);
        setTranslateX(0);
        setTranslateY(0);
        setFill(color);
        score = new SimpleIntegerProperty(0);
    }

    void moveUp(){
        setTranslateX(getTranslateX());
        setTranslateY(getTranslateY()-speed);

    }
    void moveDown(){
        setTranslateX(getTranslateX());
        setTranslateY(getTranslateY()+speed);
    }

}





