/*
Christopher Will
CS 335 Program 2
10/23/2017
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Maze {
    private JButton generateMazeButton;
    private JCheckBox showGenerationBox;
    private JButton solveMazeButton;
    private JCheckBox showSolveBox;
    private JSlider speedSlider;
    private JSlider rowSlider;
    private JSlider columnSlider;
    private JButton pauseButton;
    private JButton resumeButton;
    private JPanel controlPanel; //the panel which contains all the buttons, sliders, and boxes
    private JLabel visitedLabel; //the label for the % of cells visited

    private JPanel generatePanel; //the panel for the generate maze options
    private JPanel solvePanel; //the panel for the solve maze options


    //adds the panel that shows the Generate button and Show Generation box
    public void addGeneratePanel(){
        generatePanel = new JPanel();
        generatePanel.setLayout(new BoxLayout(generatePanel, BoxLayout.X_AXIS));
        generateMazeButton = new JButton("Generate");
        showGenerationBox = new JCheckBox("Show Generation");

        generatePanel.add(generateMazeButton);
        generatePanel.add(showGenerationBox);
    }
    //adds the panel which shows the Solve button and Show Solver box
    public void addSolvePanel(){
        solvePanel = new JPanel();
        solvePanel.setLayout(new BoxLayout(solvePanel, BoxLayout.X_AXIS));
        solveMazeButton = new JButton("Solve");
        showSolveBox = new JCheckBox("Show Solver");

        solvePanel.add(solveMazeButton);
        solvePanel.add(showSolveBox);
    }
    //adds the row, column, and speed slider and sets text labels for them
    public void addSliders(){
        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 10);
        speedSlider.setInverted(true); //set inverted so that moving to the right will decrease the speed
        rowSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 50, 50);
        columnSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 50, 50);
        JLabel speedText = new JLabel("Speed:");
        JLabel rowText = new JLabel("Rows: " + rowSlider.getValue());
        JLabel columnText = new JLabel("Columns: " + columnSlider.getValue());

        //update the text value of the row and column labels based on their sliders current values
        rowSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                rowText.setText("Rows: " + rowSlider.getValue());
            }
        });
        columnSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                columnText.setText("Columns: " + columnSlider.getValue());
            }
        });

        controlPanel.add(speedText);
        controlPanel.add(speedSlider);
        addSpace(50, 80);
        controlPanel.add(rowText);
        controlPanel.add(rowSlider);
        addSpace(50, 80);
        controlPanel.add(columnText);
        controlPanel.add(columnSlider);
    }

    //add white space to the controlPanel to make the layout look more balanced
    public void addSpace(int xSpace, int ySpace){
        controlPanel.add(Box.createRigidArea(new Dimension(xSpace, ySpace)));
    }
    //add the control panel to the RHS of the screen. This has all the buttons, sliders, and boxes needed to control the maze
    public void addControlPanel(){
        addGeneratePanel();
        controlPanel.add(generatePanel);
        addSpace(50, 50);
        addSolvePanel();
        controlPanel.add(solvePanel);
        addSpace(50, 100);
        addSliders();
        addSpace(50, 30);

        //add pause and resume buttons. These pause or resume the maze generation/solver if the generation/solving has begun
        pauseButton = new JButton("Pause");
        controlPanel.add(pauseButton);
        addSpace(50, 30);
        resumeButton = new JButton("Resume");
        controlPanel.add(resumeButton);
    }

    public Maze(){
        JFrame mainFrame = new JFrame("Maze!");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mazePanel = new JPanel();
        mazePanel.setLayout(new BorderLayout());

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        addControlPanel();
        mainFrame.getContentPane().add(mazePanel, BorderLayout.CENTER);
        mainFrame.getContentPane().add(controlPanel, BorderLayout.EAST);

        mainFrame.setSize(900, 700);

        //create a new MazeBoard object and paint it on the screen
        //this takes the current values of the row, column, and speed sliders, as well as the state of the boxes
        MazeBoard mazeBoard = new MazeBoard(rowSlider.getValue(), columnSlider.getValue(),
                showGenerationBox.isSelected(), speedSlider.getValue(), showSolveBox.isSelected());
        mainFrame.getContentPane().add(mazeBoard);

        //generate a new maze using the current values of the row, column, and speed sliders, as well as the value of
        //the check box. Also takes the visitedLabel so that the # of cells visited can be updated on the mainFrame
        generateMazeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeBoard.generateMaze(rowSlider.getValue(), columnSlider.getValue(),
                        showGenerationBox.isSelected(), speedSlider.getValue(), visitedLabel);
            }
        });

        //if the maze is currently being generated OR solved then pressing this button will pause that action
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeBoard.pause();
            }
        });
        //if the maze is currently being generated OR solved and has been paused then pressing this button will resume that action
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeBoard.resume();
            }
        });

        //if the maze has been generated then pressing this button will solve the maze
        solveMazeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazeBoard.solveMaze(visitedLabel, showSolveBox.isSelected());
            }
        });
        visitedLabel = new JLabel("Visited: 0%");
        mainFrame.getContentPane().add(visitedLabel, BorderLayout.SOUTH);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
   }

    public static void main(String[] args) {
        new Maze(); //make the maze
    }
}
