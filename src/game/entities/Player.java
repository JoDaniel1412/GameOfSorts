package game.entities;

import game.GameController;
import game.draw.Drawer;
import game.draw.Sprite;
import game.event.handler.inputs.Collisions;
import game.event.handler.inputs.KeyReader;
import javafx.scene.shape.Rectangle;
import util.Clock;
import util.Math;

public class Player extends Entity {

    private KeyReader key = KeyReader.getInstance();
    private Clock clock = Clock.getInstance();
    private int lives = 3;
    private int xSpeed = 0;
    private int ySpeed = 0;
    private int xMaxSpeed = 10;
    private int yMaxSpeed = 10;
    private int xAcc = 1;
    private int yAcc = 2;
    private int xPoss = 200;
    private int yPoss = 200;
    private int fire_rate = 500;
    private int damage = 1;
    private long lastTime = 0;
    private String state = "Moving"; // Moving / Dead / Dashing
    private Sprite sprite;

    public Player(){
        Drawer.getInstance().addDraw(this);
        GameController.getInstance().addEntity(this);
    }

    @Override
    public void update(){
        move();

        if (key.shoot == 1 && canShoot()){
            shoot();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public Sprite draw() {
        sprite = new Sprite(xPoss, yPoss, "file:res/img/entities/griffin/Griffin.png");
        return sprite;
    }

    private void hit(){
        lives--;
        if(lives <= 0){
            dead();
        }
    }

    private void heal(){

    }

    private void shoot(){
        var yDirection = key.arrow_down - key.arrow_up;
        FireBall fireBall = new FireBall(xPoss, yPoss, yDirection);
        Collisions.getInstance().addPlayerBullets(fireBall);
    }

    private void dead(){

    }

    private void move(){
        var xMove = key.right - key.left;
        var yMove = key.up - key.down;

        if (xMove == 0){
            xSpeed =  Math.approach(xSpeed, 0, 0.1);
        }

        if (yMove == 0){
            ySpeed =  Math.approach(ySpeed, 0, 0.1);
        }

        xSpeed = Math.clamp(xSpeed += xAcc * xMove, -xMaxSpeed, xMaxSpeed);
        ySpeed = Math.clamp(ySpeed -= yAcc * yMove, -yMaxSpeed, yMaxSpeed);
        xPoss += xSpeed;
        yPoss += ySpeed;
    }

    private Boolean canShoot(){
        Boolean result = false;
        long time = clock.getTime();
        if (time - lastTime > fire_rate){
            result = true;
            lastTime = time;
        }
        return result;

    }

    public int getxPoss() {
        return xPoss;
    }

    public void setxPoss(int xPoss) {
        this.xPoss = xPoss;
    }

    public int getyPoss() {
        return yPoss;
    }

    public void setyPoss(int yPoss) {
        this.yPoss = yPoss;
    }
}
