
import javax.swing.*;
import java.awt.*;

// this class defines what a 'player' is regardless if it is human controlled or cpu controlled
public class Racket implements Runnable{
    private int x, y, yVelocity, lowBound, upBound, height, width, frameRate;
    private JComponent drawOn;
    private boolean isMoveUp, isMoveDown, isPaused;

    // constructor for the racket and places its first location while taking note of its bounding rectangle
    public Racket(int x, int y, int lowBound, int upBound, JComponent drawOn){
        this.x = x;
        this.y = y;
        this.lowBound = lowBound;
        this.upBound = upBound;
        this.yVelocity = 2;
        this.drawOn = drawOn;
        this.isMoveUp = false;
        this.isMoveDown = false;
        this.height = 50;
        this.width = 10;
        this.isPaused = false;
        this.frameRate = 5;
    }

    // sets speed for how fast the updates the movement of the paddle is
    public void setFrameRate(int x){this.frameRate = x; }
    public boolean getIsMoveUp(){ return this.isMoveUp; }
    public boolean getIsMoveDown(){ return this.isMoveDown;}
    public int getY(){
        return this.y;
    }
    public int getX() { return this.x; }
    public int getHeight() { return this.height; }
    public int getWidth() { return this.width; }

    // gets the rectangle shape for the paddle
    public Rectangle getRectangle(){
        return new Rectangle(x - width/2, y - height/2, width, height);
    }

    // allows the paddle to move up
    public void moveUp(){
        this.isMoveUp = true;
    }

    // allows the paddle to move down
    public void moveDown(){
        this.isMoveDown = true;
    }

    // allows the paddle to stop moving up
    public void stopMoveUp() { this.isMoveUp = false; }

    // allows the paddle to stop moving down
    public void stopMoveDown() { this.isMoveDown = false; }

    // allows the paddle to move during the updates on the screen and keeps track of the paddle's bounds
    public void move(){
        if(isMoveDown && this.y + this.height/2 <= lowBound){
            this.y += this.yVelocity;
        }
        if(isMoveUp && this.y - this.height/2 >= upBound){
            this.y -= this.yVelocity;
        }
    }

    // draws the paddle using the fillRect function through a passed in Graphics2D object
    public void draw(Graphics2D g2){
        g2.fillRect(x - width/2, y - height/2, width, height);
    }

    public void setYVelocity(int vel){
        this.yVelocity = vel;
    }

    // puts the paddle into a paused or unpaused state depending on the game state
    public void setPauseState(){
        this.isPaused = !isPaused;
    }

    public int getFrameRate(){
        return this.frameRate;
    }

    @Override
    // the thread that handles the updates and rendering for the paddle object
    // a special side effect for the racket class is how the frameRate can be restricted for the paddle when the cpu is using it
    // this could eventually be utilized in the future
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            if(!this.isPaused) {
                try {
                    move();
                    drawOn.repaint();
                    Thread.sleep(this.frameRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
