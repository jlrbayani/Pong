import javax.swing.JFrame;
import java.awt.*;

// This project is simply a classic game of pong. The user can choose whether to play against another person or against a cpu.
// This project was first started with these goals in mind:
//        - Practice java again with its gui elements of swing and awt
//        - relearn how to "draw" in java
//        - Kinda Practice Object-Oriented Programming and Exceptions
//        - Have a grasp of how the workflow is for creating a game
//        - Learn how to implement images and sound in some manner
//        - Learn how to format or sort the steps to creating a responsive  gui application that flows well enough from beginning (main menu) to end (game finishing) then back to beginning
//
// By: Jasper Bayani

// class: Pong
// This class creates the main Pong object that initializes the object and starts the thread for the Board GUI
// throughout the runtime of the program.
public class Pong extends JFrame {

    public Pong(){
        initUI();
    }

    public void initUI(){
        setContentPane(new Board());
        setResizable(false);
        pack();

        setTitle("Pong");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            JFrame frame = new Pong();
            frame.setVisible(true);
        });
    }
}
