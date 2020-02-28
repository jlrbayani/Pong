import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

// this class defines the ball object in play during the game
public class Ball implements Runnable {
    private int lowBound, rightBound;
    private double x, y, xVelocity, yVelocity;
    private static final int DIAMETER = 15;
    private static final Color ballColor = Color.WHITE;
    private JComponent drawOn;
    private boolean isPaused;

    // constructor for the ball with its bounds kept in mind
    public Ball(double x, double y, int lowBound, int rightBound, JComponent drawOn){
        this.x = x;
        this.y = y;
        this.drawOn = drawOn;
        this.xVelocity = 2;
        this.yVelocity = 0.5;
        this.lowBound = lowBound;
        this.rightBound = rightBound;
        this.isPaused = false;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getXVelocity() { return this.xVelocity; }

    public double getYVelocity() { return this.yVelocity; }

    public void setXVelocity(double x){
        this.xVelocity = x;
    }

    public void setYVelocity(double y){
        this.yVelocity = y;
    }

    // checks if it collides with the racket passed through
    public boolean isColliding(Racket r){
        if(r.getRectangle().intersects(getBounds())){
            return true;
        }
        return false;
    }

    // this is how the ball moves through x and y while keeping track of its bounding rectangle
    public void move(){
        if(this.y + DIAMETER/2 >= lowBound || this.y - DIAMETER/2 <= 0){
            this.yVelocity *= -1;
        }
        if(this.x + DIAMETER/2 >= rightBound || this.x - DIAMETER/2 <= 0){
            this.xVelocity *= -1;
        }
        this.x += xVelocity;
        this.y += yVelocity;
    }

    // function that draws the ball
    public void draw(Graphics2D g2){
        Ellipse2D.Double circle = new Ellipse2D.Double(this.x - DIAMETER/2, this.y - DIAMETER/2, DIAMETER, DIAMETER);
        g2.setColor(ballColor);
        g2.fill(circle);
    }

    // this function calculates the current bounds of the ball
    public Rectangle2D.Double getBounds(){
        return new Rectangle2D.Double(this.x - DIAMETER/2, this.y - DIAMETER/2, DIAMETER, DIAMETER);
    }

    // pauses or resumes the ball update and render depending on the game state
    public void setPauseState(){
        this.isPaused = !isPaused;
    }

    @Override
    // the run method which handles the render rate for the thread of this object
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            if(!this.isPaused) {
                try {
                    move();
                    drawOn.repaint();
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
