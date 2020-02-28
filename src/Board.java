
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

//Note: inefficient handling of AudioInputStream, it should be eventually closed, this might be causing the application to use up a lot of memory because of opened streams

// class: Board
// This class handles the drawing of the different elements and handles how the different screens for different game states.
// Multiple empty definitions for certain lines of code for future updates.
public class Board extends JPanel implements Runnable {

    private enum GameState{
        MENU, OPTIONS, RUNNINGPVP, RUNNINGPVE, GAMEFIN;
    }

    private enum MenuChoices{
        PVP, PVE, OPTIONS;
    }

    private enum OptionsChoices{
        MENU;
    }

    private enum GameFinChoices{
        MENU;
    }

    private final int W_HEIGHT = 500;
    private final int W_WIDTH = 750;
    private final int DELAY = 25;
    private final int DISTSIDEWALL = 30;
    private final int DISTCEILING = 15;

    private Ball ball;
    private Racket p1, p2;
    private Score s1, s2;
    private Thread tb, tp1, tp2, ts1, ts2, animator;
    private boolean isWinP1, isWinP2, isPaused, isColliding;
    private GameState currentState;
    private Container menuPane, gameFinPane;
    private JPanel optionsPane;

    // empty constructor for Board
    public Board(){
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));
        initMainMenu();
        this.currentState = GameState.MENU;
    }

    // initializes the main menu screen for the Pong Board with the appropriate buttons.
    private void initMainMenu(){
        menuPane = new Container();
        menuPane.setLayout(new BoxLayout(menuPane, BoxLayout.Y_AXIS));

        ImageIcon icon = new ImageIcon("src/icons/pongLogo.png");
        JLabel title = new JLabel(icon);
        JButton buttonPVP = addAButton("2 Players");
        JButton buttonPVE = addAButton("1 Player");
        JButton buttonOpt = addAButton("Options");

        buttonPVP.setActionCommand(MenuChoices.PVP.name());
        buttonPVE.setActionCommand(MenuChoices.PVE.name());
        buttonOpt.setActionCommand(MenuChoices.OPTIONS.name());

        buttonPVP.addActionListener(new MainMenuListener());
        buttonPVE.addActionListener(new MainMenuListener());
        buttonOpt.addActionListener(new MainMenuListener());

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPane.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPane.add(title);
        menuPane.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPane.add(buttonPVP);
        menuPane.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPane.add(buttonPVE);
        menuPane.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPane.add(buttonOpt);

        this.add(menuPane);

        this.revalidate();
        this.repaint();

    }

    // initializes the options menu
    private void initOptions(){

        optionsPane = new JPanel();
        optionsPane.setLayout(new BoxLayout(optionsPane, BoxLayout.Y_AXIS));
        optionsPane.setBackground(Color.BLACK);

        JButton buttonToMain = addAButton("Back to Main Menu");

        buttonToMain.setActionCommand(OptionsChoices.MENU.name());

        buttonToMain.addActionListener(new OptionsListener());

        optionsPane.add(Box.createRigidArea(new Dimension(0,50)));
        optionsPane.add(buttonToMain);

        this.add(optionsPane);
        this.revalidate();
        this.repaint();

    }

    // initializes the screen for when the game ends (when a player reaches a score of 7)
    private void initGameFin(){
        gameFinPane = new Container();
        gameFinPane.setLayout(new BoxLayout(gameFinPane, BoxLayout.Y_AXIS));

        JButton buttonToMain = addAButton("Back to Main Menu");
        String win = "";
        if(isWinP1){
            win = "Player 1!";
        }
        else{
            win = "Player 2!";
        }

        JLabel winner = new JLabel("The winner is: " + win);
        winner.setFont(new Font("Courier", Font.BOLD, 30));

        buttonToMain.setActionCommand(OptionsChoices.MENU.name());
        buttonToMain.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonToMain.addActionListener(new GameFinListener());

        gameFinPane.add(Box.createRigidArea(new Dimension(0, 50)));
        gameFinPane.add(buttonToMain);
        gameFinPane.add(Box.createRigidArea(new Dimension(0, 50)));
        gameFinPane.add(winner);

        this.add(gameFinPane);
        this.revalidate();
        this.repaint();
    }

    // a small function that creates a button with the right properties
    private JButton addAButton(String text){
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        return b;
    }

    // initializes the Board/screen view when a game starts
    private void initBoard(){
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(W_WIDTH, W_HEIGHT));

        this.isWinP1 = false;
        this.isWinP2 = false;
        this.isPaused = false;
        this.isColliding = false;
        addNewBall();
        addPlayers();
        addScores();
        addKeyListener(new PlayerListener());

        if(this.currentState == GameState.RUNNINGPVE){
            p2.setFrameRate(25);
        }
    }

    // creates a new ball when someone scores or when the game starts
    public void addNewBall(){
        ball = new Ball(W_WIDTH/2, W_HEIGHT/2, W_HEIGHT, W_WIDTH, this);
        tb = new Thread(ball);
        tb.start();
        repaint();
    }

    // adds the Rackets for the player(s) and/or cpu to use
    public void addPlayers(){
        p1 = new Racket(DISTSIDEWALL, W_HEIGHT/2, W_HEIGHT - DISTCEILING, DISTCEILING, this);
        p2 = new Racket(W_WIDTH - DISTSIDEWALL, W_HEIGHT/2,  W_HEIGHT - DISTCEILING, DISTCEILING, this);

        tp1 = new Thread(p1);
        tp2 = new Thread(p2);
        tp1.start();
        tp2.start();
        repaint();
    }

    // draws and updates the scores on the screen
    public void addScores(){
        s1 = new Score( this.W_WIDTH/5, W_HEIGHT/10, this);
        s2 = new Score(this.W_WIDTH*4/5,W_HEIGHT/10, this);

        ts1 = new Thread(s1);
        ts2 = new Thread(s2);
        ts1.start();
        ts2.start();
        repaint();
    }

    @Override
    // starts the animation updates by starting the main thread for different objects on the screen
    public void addNotify(){
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @Override
    // paintComponent to get the Graphics2D object to be used to draw different component objects
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch(this.currentState){
            case MENU:
            case OPTIONS:
            case GAMEFIN:
                break;
            case RUNNINGPVE:
            case RUNNINGPVP:
                drawGameRunning(g2);
                break;
            default:
                System.out.println("Error! Undefined GameState found.");
                break;
        }
    }

    // the function that draws all the different components
    public void drawGameRunning(Graphics2D g2){
        ball.draw(g2);
        p1.draw(g2);
        p2.draw(g2);
        s1.draw(g2);
        s2.draw(g2);
        for(int index = 0; index < this.W_HEIGHT; index += 40){
            g2.drawLine(W_WIDTH/2, index, W_WIDTH/2, index + 20);
        }
    }

    // checks the collisions between the current ball and the paddles
    public void checkCollisions(){
        if(ball.isColliding(p1) || ball.isColliding(p2)) {
            ball.setXVelocity(-1 * ball.getXVelocity());
            this.isColliding = true;
            try{
                playPaddleHit();
            }
            catch(Exception e){
                System.out.println("Error with playing sound!");
                e.printStackTrace();
            }
        }
    }

    // function that plays a certain sound when the ball collides with a paddle
    //sound files found at https://www.pacdv.com/sounds/interface_sounds-3.html
    private void playPaddleHit() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String filePath = "";
        int rand = (int)(Math.random() * 10) + 1;
        if(rand % 2 == 0)
            filePath = "src/sounds/paddleSound1.wav";
        else
            filePath = "src/sounds/paddleSound2.wav";

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
    }

    //checks if a score on which side was made by a player and adjusts the correct score object
    public void checkIfScore(){
        boolean isScore = false;
        if(ball.getX() <= DISTSIDEWALL - p1.getWidth()){
            s2.setScore(s2.getScore() + 1);
            ball = null;
            addNewBall();
            isScore = true;
        }
        if(ball.getX() >= W_WIDTH - DISTSIDEWALL + p2.getWidth()){
            s1.setScore(s1.getScore() + 1);
            ball = null;
            addNewBall();
            isScore = true;
        }
        if(isScore){
            try{
                playScoreSound();
            }
            catch(Exception e){
                System.out.println("Error with playing sound!");
                e.printStackTrace();
            }
        }
    }

    // function that plays a sound when a player scores
    //sound files found at https://www.pacdv.com/sounds/interface_sounds-3.html
    public void playScoreSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sounds/scoreSound.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
    }

    // checks the scores if a player has won
    public void checkIfWin(){
        if(s1.getScore() >= 7){
            this.isWinP1 = true;
        }
        else if(s2.getScore() >= 7){
            this.isWinP2 = true;
        }
    }

    // pauses the game when the escape key is pressed during the game
    public void changePauseState(){
        p1.setPauseState();
        p2.setPauseState();
        ball.setPauseState();
        s1.setPauseState();
        s2.setPauseState();
    }

    // when the correct game state is checked to be true this functions runs that handles  the general movement of components
    public void runGame(){
        if(this.currentState == GameState.RUNNINGPVE){
            if(ball.getY() < p2.getY()){
                p2.stopMoveDown();
                p2.moveUp();
            }
            else if(ball.getY() > p2.getY()){
                p2.stopMoveUp();
                p2.moveDown();
            }
        }
        if(!this.isColliding){
            checkCollisions(); // in if statement to sleep for a bit to prevent wonky calculations of collisions between the rackets and the ball
            try{
                Thread.sleep(25);
            } catch (InterruptedException e){
                String msg = String.format("Thread interrupted: %s", e.getMessage());

                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            this.isColliding = false;
        }

        checkIfScore();
        checkIfWin();
    }

    @Override
    // the run method for this Board object which handles everything about the game throughout runtime
    // In other words, this is where the main game loop is handled
    public void run(){

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while(true){
            switch(this.currentState){
                case MENU:
                case OPTIONS:
                case GAMEFIN:
                    break;
                case RUNNINGPVE:
                case RUNNINGPVP:
                    runGame();
                    checkIfWin();
                    if(this.isWinP1 || this.isWinP2){
                        this.currentState = GameState.GAMEFIN;
                    }
                    break;
                default:
                    System.out.println("Error! Undefined GameState found. ");
                    break;
            }

            if(this.currentState == GameState.GAMEFIN){
                initGameFin();
                while(this.currentState == GameState.GAMEFIN){
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e){
                        String msg = String.format("Thread interrupted: %s", e.getMessage());

                        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if(sleep < 0){
                sleep = 2;
            }

            try{
                Thread.sleep(sleep);
            } catch (InterruptedException e){
                String msg = String.format("Thread interrupted: %s", e.getMessage());

                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    // a private listener class which handles the screen for the main menu
    private class MainMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals(MenuChoices.PVP.name())){
                remove(menuPane);
                currentState = GameState.RUNNINGPVP;
                initBoard();
            }
            if(e.getActionCommand().equals(MenuChoices.PVE.name())){
                remove(menuPane);
                currentState = GameState.RUNNINGPVE;
                initBoard();
            }
            if(e.getActionCommand().equals(MenuChoices.OPTIONS.name())){
                remove(menuPane);
                initOptions();
                currentState = GameState.OPTIONS;
            }
        }
    }

    // a private listener class which handles the options screen
    private class OptionsListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals(OptionsChoices.MENU.name())){
                remove(optionsPane);
                currentState = GameState.MENU;
                initMainMenu();
            }
        }
    }

    // a private listener class which handles the screen for when a game finishes and someone wins
    private class GameFinListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals(GameFinChoices.MENU.name())){
                remove(gameFinPane);
                currentState = GameState.MENU;
                initMainMenu();
            }
        }

    }

    // a private listener class for the player(s) during the game
    private class PlayerListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_S) {
                p1.moveDown();
            }
            if(e.getKeyCode() == KeyEvent.VK_W){
                p1.moveUp();
            }
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                changePauseState();
            }
            if(currentState == GameState.RUNNINGPVP) {
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    p2.moveDown();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    p2.moveUp();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_W){
                p1.stopMoveUp();
            }
            if(e.getKeyCode() == KeyEvent.VK_S){
                p1.stopMoveDown();
            }
            if(currentState == GameState.RUNNINGPVP) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    p2.stopMoveUp();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    p2.stopMoveDown();
                }
            }
        }
    }

}
