package game.entities;

import game.GameController;
import game.draw.Drawer;
import game.draw.Sprite;
import game.event.handler.Collisions;
import game.event.handler.inputs.KeyReader;
import util.Clock;
import util.Math;

import java.util.ArrayList;

public class Player extends Entity {

    private KeyReader key = KeyReader.getInstance();
    private Clock clock = Clock.getInstance();
    private int lives = 6;
    private int xSpeed = 0, ySpeed = 0;
    private int xMaxSpeed = 7, yMaxSpeed = 7;
    private int xAcc = 1, yAcc = 2;
    private int yMove = 0, xMove = 0;
    private double xPoss = 200, yPoss = 200;
    private int playerWidth = 150, playerHeight = 200;
    private int fire_rate = 300;
    private int damage = 1;
    private long lastTime = 0;
    private String state = "Moving"; // Moving / Dead / Dashing
    private ArrayList<Sprite> movementAnimation = new ArrayList<>();
    private ArrayList<Sprite> deathAnimation = new ArrayList<>();
    private ArrayList<Sprite> dashAnimation = new ArrayList<>();
    private ArrayList<Sprite> currentAnimation = new ArrayList<>();
    private Sprite sprite = loadImages();
    private int animationTimer = 100;
    private double lastAnimationTime = 0;
    private int currentSprite = 0;
    private int hitTimer = 0;
    private boolean dashing = false;
    private int dashDuration = 10;
    private int dashTime = dashDuration;

    public Player() {
        Drawer.getInstance().addDrawAtEnd(this);
        GameController.getInstance().addEntity(this);
    }

    @Override
    public void update() {
        if (key.shift == 1 && !dashing) dashing = true;
        if (dashing) dash();
        else {
            move();
            hitAnimation();
            if (key.shoot == 1 && canShoot()) {
                shoot();
            }
        }
    }

    @Override
    public void destroy() {
        Drawer.getInstance().deleteEntity(this);
        GameController.getInstance().deleteEntity(this);
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Sprite draw() {
        long time = clock.getTime();
        if (time - lastAnimationTime > animationTimer) {
            sprite = movementAnimation.get(currentSprite);
            lastAnimationTime = time;
            currentSprite = (currentSprite + 1) % movementAnimation.size();
        }
        sprite.move(xPoss, yPoss);
        return sprite;
    }

    private Sprite loadImages() {
        String root = "file:res/img/entities/griffin/";
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin1"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin2"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin3"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin4"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin5"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin6"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin7"));
        movementAnimation.add(sprite = new Sprite(xPoss, yPoss, playerWidth, playerHeight, root + "griffin8"));
        return movementAnimation.get(0);

    }

    @Override
    public void hit() {
        lives--;
        hitTimer = 30;
        if (lives <= 0) {
            dies();
        }
    }

    private void hitAnimation() {
        if (hitTimer > 0) {
            hitTimer--;
            sprite.getSprite().setEffect(sprite.effect);
        }
        if (hitTimer == 0) sprite.getSprite().setEffect(null);
    }

    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }

    private void heal() {

    }

    private void shoot() {
        var yDirection = key.arrow_down - key.arrow_up;
        FireBall fireBall = new FireBall(xPoss, yPoss, sprite.getWidth() / 1.4, 1, yDirection);
        Collisions.getInstance().addPlayerBullets(fireBall);
    }

    private void dies() {
        new PlayerDeath(xPoss, yPoss, 130, 120);
        destroy();
    }

    private void dash(){
        var yMove = key.up - key.down;
        dashTime--;
        ySpeed = -60 * yMove;
        yMaxSpeed = 60;
        new PlayerDash(xPoss, yPoss, sprite);

        // Calculates current position
        xSpeed = Math.clamp(xSpeed += xAcc * xMove, -xMaxSpeed, xMaxSpeed);
        ySpeed = Math.clamp(ySpeed, -yMaxSpeed, yMaxSpeed);
        xPoss += xSpeed;
        yPoss += ySpeed;

        // Calculates boundaries
        var height = Drawer.height;
        var width = Drawer.width;
        var spriteHH = sprite.getHeight() / 2;
        var spriteHW = sprite.getWidth() / 2;
        if (yPoss - spriteHH < 0) yPoss = 0 + spriteHH;
        if (yPoss + spriteHH > height) yPoss = height - spriteHH;
        if (xPoss - spriteHW < 0) xPoss = 0 + spriteHW;
        if (xPoss + spriteHW > width) xPoss = width - spriteHW;

        // Ends the dash
        if (dashTime <= 0){
            dashing = false;
            dashTime = dashDuration;
            ySpeed = 0;
            yMaxSpeed = 7;
        }
    }

    private void move() {
        var xMove = key.right - key.left;
        var yMove = key.up - key.down;

        // Adds gravity
        if (yMove < 0) {
            yAcc = 3;
            yMaxSpeed = 9;
        }
        if (yMove > 0) {
            yAcc = 2;
            yMaxSpeed = 6;
        }

        // Adds air friction
        if (xMove == 0) {
            xSpeed = Math.approach(xSpeed, 0, 1);
        }
        if (yMove == 0) {
            ySpeed = Math.approach(ySpeed, 0, 1);
        }

        // Calculates current position
        xSpeed = Math.clamp(xSpeed += xAcc * xMove, -xMaxSpeed, xMaxSpeed);
        ySpeed = Math.clamp(ySpeed -= yAcc * yMove, -yMaxSpeed, yMaxSpeed);
        xPoss += xSpeed;
        yPoss += ySpeed;

        // Calculates boundaries
        var height = Drawer.height;
        var width = Drawer.width;
        var spriteHH = sprite.getHeight() / 2;
        var spriteHW = sprite.getWidth() / 2;
        if (yPoss - spriteHH < 0) yPoss = 0 + spriteHH;
        if (yPoss + spriteHH > height) yPoss = height - spriteHH;
        if (xPoss - spriteHW < 0) xPoss = 0 + spriteHW;
        if (xPoss + spriteHW > width) xPoss = width - spriteHW;
    }

    private Boolean canShoot() {
        Boolean result = false;
        long time = clock.getTime();
        if (time - lastTime > fire_rate) {
            result = true;
            lastTime = time;
        }
        return result;

    }

    public double getxPoss() {
        return xPoss;
    }

    public void setxPoss(double xPoss) {
        this.xPoss = xPoss;
    }

    public double getyPoss() {
        return yPoss;
    }

    public void setyPoss(double yPoss) {
        this.yPoss = yPoss;
    }

    public int getSpeed() {
        return xSpeed;
    }

    public int getySpeed() {
        return ySpeed;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public int getLives() {
        return lives;
    }
}
