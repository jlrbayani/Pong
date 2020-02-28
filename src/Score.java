import javax.swing.*;
import java.awt.*;

// this class handles the score for each player
public class Score implements Runnable{
    private int score, x, y;
    private JComponent drawOn;
    private Font font;
    private boolean isPaused;

    // the score constructor which specifies its original position and sets the score to be first at 0
    public Score(int x, int y, JComponent drawOn){
        this.drawOn = drawOn;
        this.score = 0;
        this.x = x;
        this.y = y;
        this.font = new Font("Courier", Font.BOLD, 18);
        this.isPaused = false;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

    // draws the score when the score gets updated during game runtime
    public void draw(Graphics2D g2){
        g2.setFont(this.font);
        g2.drawString(this.score + "", this.x, this.y);
    }

    // puts the score into a paused or unpaused state depending on the game state
    public void setPauseState(){
        this.isPaused = !isPaused;
    }

    @Override
    // the thread that handles the updates and rendering for the score object
    public void run(){
        while(!Thread.currentThread().isInterrupted() && !this.isPaused) {
            if (!this.isPaused) {
                try {
                    drawOn.repaint();
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
