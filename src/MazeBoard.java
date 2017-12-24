import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Stack;
import java.util.*;

public class MazeBoard extends JPanel{
    private int rows;
    private int columns;
    private int generationSpeed;

    //whether the generation/solving should be shown
    private boolean showGeneration;
    private boolean showSolving;
    //timers for showing the generation/solving of the maze
    private Timer drawCellTimer;
    private Timer solveMazeTimer;

    //the x,y coords and height, width of the item to draw
    private int drawXCoord;
    private int drawYCoord;
    private int drawWidth;
    private int drawHeight;
    private int currentDrawing; //the index of the current item to draw in the list
    //whether the timers were paused during the maze being drawn or solved
    private boolean drawTimerPaused;
    private boolean solveTimerPaused;
    private boolean mazeGenerated;//whether the maze has been fully generated yet
    private boolean startedToSolve; //whether the maze has been started to be solved
    private float numVisited; //the # cells visited
    private int wallHeight;
    private int wallWidth; //the height and width of a wall
    private int height;
    private int width; //the height and width of a cell
    private MazeCell[] cells; //all the cells
    private ArrayList<MazeWall> walls; //all the walls
    private List<List<Integer>> drawingArray; //a list of a list of cells/walls to draw
    private List<List<Integer>> solveList; //a list of a list of cells to draw

    //creates a new maze with the given rows, columns, speed and whether the generation/solving should be shown
    public MazeBoard(int rows, int columns, boolean showGeneration, int speed, boolean showSolving){
        this.rows = rows;
        this.columns = columns;
        this.showGeneration = showGeneration;
        this.showSolving = showSolving;
        generationSpeed = speed;
        height = 10;
        width = 10; //a cell is 10x10
        wallHeight = 10; //wall is 10 pixels high
        wallWidth = 1; //distance between left and right cells is 1

    }

    //paints a new maze
    public void makeNewBoard(Graphics2D g2d) {
        mazeGenerated = false; //the maze has not been generated fully yet
        cells = new MazeCell[rows * columns];
        walls = new ArrayList<MazeWall>();

        int k = 0; // index of the current cell being generated
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                int xCoord = (j * (width + wallWidth));
                int yCoord = (i * (width + wallWidth));
                Color blue = Color.decode("#050574");
                g2d.setColor(blue); //color the cells a nice blue
                g2d.fillRect(xCoord, yCoord, width, height);
                //create a MazeCell object with the given coords, height, width, and index
                MazeCell newCell = new MazeCell(xCoord, yCoord, width, height, k);
                cells[k] = newCell;
                k++;
                if(j != columns - 1){ //don't add a right wall if we're at the last column
                    MazeWall rightWall = new MazeWall(xCoord + width, yCoord , wallWidth, wallHeight);
                    walls.add(rightWall);
                    g2d.setColor(Color.white); //walls should be white
                    g2d.fillRect(xCoord + width, yCoord, wallWidth, wallHeight);
                }
                if(i != rows - 1) { //don't add a bottom wall if at last row
                    MazeWall bottomWall = new MazeWall(xCoord, yCoord + height, wallHeight, wallWidth);
                    walls.add(bottomWall);
                    g2d.setColor(Color.white);
                    g2d.fillRect(xCoord, yCoord + height, wallHeight, wallWidth);
                }
            }
        }
        setNeighbors(); //set the neighbors of all the cells
    }

    //loops thru each cell and sets its neighboring cells
    //have 4 cases for 4 corners, 4 cases for 4 edges, and 1 case for an interior cell
    public void setNeighbors(){
        for(int i = 0; i < cells.length; i++){
            if(i == 0){
                setTopLeftNeighbors(i); //cell is top left corner
            }else if(i == columns - 1){
                setTopRightNeighbors(i); //cell is top right corner
            }else if(i == (columns * (rows - 1))){
                setBottomLeftNeighbors(i); //cell is bottom left corner
            }else if(i == (columns * rows) - 1){
                setBottomRightNeighbors(i); //cell is bottom right corner
            }else if(i > 0 && i < columns){
                setTopEdgeNeighbors(i); //cell is in the top row
            }else if(i > (columns * (rows - 1)) && i < ((rows * columns) - 1)){
                setBottomEdgeNeighbors(i); //cell is in the bottom row
            }else if((i % columns) == 0){
                setLeftEdgeNeighbors(i); //cell is in the left column
            }else if(((i + 1) % columns) == 0){
                setRightEdgeNeighbors(i); //cell is in the right column
            }else{
                setInnerNeighbors(i); //cell is an interior cell
            }
        }
    }
    public void setInnerNeighbors(int i){
        //this ith cell has 4 neighbors
        MazeCell[] neighbors = new MazeCell[4];
        neighbors[0] = cells[i + 1]; //right
        neighbors[1] = cells[i - 1]; //left
        neighbors[2] = cells[i - columns]; //top
        neighbors[3] = cells[i + columns]; //bottom
        cells[i].setNeighbors(neighbors);
        //actually asign the cells[i] these neighbors
    }
    //the following 4 functions set the cells neighbors if the cell is an edge
    //the edge cells will have 3 neighbors
    public void setRightEdgeNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[3];
        neighbors[0] = cells[i - 1]; //left
        neighbors[1] = cells[i - columns]; //top
        neighbors[2] = cells[i + columns]; //bottom
        cells[i].setNeighbors(neighbors);
    }
    public void setLeftEdgeNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[3];
        neighbors[0] = cells[i + 1]; //right
        neighbors[1] = cells[i - columns]; //top
        neighbors[2] = cells[i + columns]; //bottom
        cells[i].setNeighbors(neighbors);
    }
    public void setBottomEdgeNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[3];
        neighbors[0] = cells[i - 1]; //left
        neighbors[1] = cells[i + 1]; //right
        neighbors[2] = cells[i - columns]; //top
        cells[i].setNeighbors(neighbors);
    }
    public void setTopEdgeNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[3];
        neighbors[0] = cells[i - 1]; //left
        neighbors[1] = cells[i + 1];//right
        neighbors[2] = cells[i + columns]; //bottom
        cells[i].setNeighbors(neighbors);
    }
    //the following 4 functions set the cells neighbors if the cell is a corner
    //a corner cell will always have 2 neighbors
    public void setTopLeftNeighbors(int i ){
        MazeCell[] neighbors = new MazeCell[2];
        neighbors[0] = cells[i + 1]; //right
        neighbors[1] = cells[i + columns];//bottom
        cells[i].setNeighbors(neighbors);
    }
    public void setTopRightNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[2];
        neighbors[0] = cells[i - 1]; //left
        neighbors[1] = cells[i + columns]; //bottom
        cells[i].setNeighbors(neighbors);
    }
    public void setBottomLeftNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[2];
        neighbors[0] = cells[i + 1]; //right
        neighbors[1] = cells[i - columns]; //top
        cells[i].setNeighbors(neighbors);
    }
    public void setBottomRightNeighbors(int i){
        MazeCell[] neighbors = new MazeCell[2];
        neighbors[0] = cells[i - 1]; //left
        neighbors[1] = cells[i - columns]; //top
        cells[i].setNeighbors(neighbors);
    }

    //update the # of cells visited
    public void setLabel(JLabel visitedLabel){
        numVisited++;
        float totalCells = rows * columns;
        float percentVisited = 100 * numVisited / totalCells; //find the % of cells visited
        visitedLabel.setText("Visited: " + percentVisited + "%");
    }

    //actually draw the generation of the cells and walls
    public void drawGeneration(Graphics g, JLabel visitedLabel){
        Graphics2D g2d = (Graphics2D)g;
        //draw the item at the given coords
        g2d.fillRect(drawXCoord, drawYCoord, drawWidth, drawHeight);
        if(drawWidth == width && drawHeight == height){
            setLabel(visitedLabel); //if the item we just drew was a cell then update the % of cells visited
        }
        currentDrawing++; //update this int to get the index of the next thing to draw
        if(currentDrawing >= drawingArray.size()){//end the timer if we've drawn all the board
            drawCellTimer.stop();
            mazeGenerated = true; //the maze has now been generated
            return;
        }

        //get the coords and height/width of the next item to draw
        //we do this again because we want to draw a wall and cell in pairs, and then pause while waiting for the timer
        drawXCoord = drawingArray.get(currentDrawing).get(0);
        drawYCoord = drawingArray.get(currentDrawing).get(1);
        drawWidth = drawingArray.get(currentDrawing).get(2);
        drawHeight = drawingArray.get(currentDrawing).get(3);


        g2d.fillRect(drawXCoord, drawYCoord, drawWidth, drawHeight);
        if(drawWidth == width && drawHeight == height){
            setLabel(visitedLabel);
        }
        currentDrawing++;
        if(currentDrawing >= drawingArray.size()){
            drawCellTimer.stop();
            mazeGenerated = true;
            return;
        }
        drawXCoord = drawingArray.get(currentDrawing).get(0);
        drawYCoord = drawingArray.get(currentDrawing).get(1);
        drawWidth = drawingArray.get(currentDrawing).get(2);
        drawHeight = drawingArray.get(currentDrawing).get(3);

    }

    //draw a new board
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        makeNewBoard(g2d); //make a ne board
    }

    //make the maze based on the given rows, columsn, speed, and whether thegeneration should be shown or not
    public void generateMaze(int rows, int columns, boolean showGeneration, int speed, JLabel visitedLabel){
        mazeGenerated = false; //the maze has not been generated yet
        drawTimerPaused = false; //user has not pressed pause yet
        solveTimerPaused = false;
        startedToSolve = false; //user has not started to solve yet
        numVisited = 0; //have visited 0 cells
        try{ //stop the drawCellTimer if it's still running
            drawCellTimer.stop();
            if(solveMazeTimer.isRunning()) {
                solveMazeTimer.stop(); //and if this timer is still running then stop it
            }
        }catch(NullPointerException e){
            //e.printStackTrace(); only needed for debugging purposes
        }

        Graphics2D g2d = (Graphics2D) getGraphics();
        for(int i = 0; i < this.rows * this.columns; i++){
            g2d.clearRect(cells[i].getXCoord(), cells[i].getYCoord(), width, height);
        } //undraw all the cells
        for(int i = 0; i < walls.size(); i++){
            g2d.clearRect(walls.get(i).getXCoord(), walls.get(i).getYCoord(), walls.get(i).getWidth(), walls.get(i).getHeight());
        }//undraw all the walls
        //set the boards attributes based on the given paramters
        this.rows = rows;
        this.columns = columns;
        this.showGeneration = showGeneration;
        this.generationSpeed = speed;

        //make a new board
        makeNewBoard((Graphics2D)(getGraphics()));
        currentDrawing = 0; //this is index of the drawingArray that's used to actually draw the board
        drawingArray = new ArrayList<List<Integer>>(); //this stores all the cells and walls to draw

        Stack<MazeCell> stack = new Stack<MazeCell>(); //stack used for DFS generation
        MazeCell currCell = cells[0]; //the current cell is the top left one
        currCell.visitCell(); //visit the top left cell and add it to the drawingArray
        drawingArray.add(Arrays.asList(currCell.getXCoord(), currCell.getYCoord(), width, height));
        while(!allCellsVisited()){ //loop until all the cells have been visited
            ArrayList<MazeCell> neighbors = new ArrayList<MazeCell>(); //the neighbors of currCell
            if(currCell.hasUnvisitedNeighbors()){//if the currCell has neighbors that have not yet been visited
                ArrayList<MazeCell> unvisitedNeighbors = currCell.getUnvisitedNeighbors();
                Random RNG = new Random(); //create a Random object to be used to make a random #
                int randIndex = RNG.nextInt(unvisitedNeighbors.size()); //get a random neighbor of currCell
                MazeCell cellToVisit = unvisitedNeighbors.get(randIndex); //visit this random neighbor next
                stack.push(currCell);
                //get the indices of the curCell and the cell to visit
                int currIndex = currCell.getIndex();
                int visitIndex = cellToVisit.getIndex();

                //find whether the cellToVisit is to the left, right, top, or bottom of currCell and remove the wall there
                if(currIndex + 1 == visitIndex){
                    //remove the right wall
                    MazeWall currWall = getWallAt(currCell.getXCoord() + width, currCell.getYCoord());
                    currWall.setWallRemoved(true);
                    drawingArray.add(Arrays.asList(currWall.getXCoord(), currWall.getYCoord(), wallWidth, wallHeight));
                }else if(currIndex - 1 == visitIndex){
                    //remove left wall
                    MazeWall currWall = getWallAt(currCell.getXCoord() - wallWidth, currCell.getYCoord());
                    currWall.setWallRemoved(true);
                    drawingArray.add(Arrays.asList(currWall.getXCoord(), currWall.getYCoord(), wallWidth, wallHeight));
                }else if(currIndex + columns == visitIndex){
                    //remove bottom wall
                    MazeWall currWall = getWallAt(currCell.getXCoord(), currCell.getYCoord() + width);
                    currWall.setWallRemoved(true);
                    drawingArray.add(Arrays.asList(currWall.getXCoord(), currWall.getYCoord(), wallHeight, wallWidth));
                }else if(currIndex - columns == visitIndex){
                    //remove top wall
                    MazeWall currWall = getWallAt(currCell.getXCoord(), currCell.getYCoord() - wallWidth);
                    currWall.setWallRemoved(true);
                    drawingArray.add(Arrays.asList(currWall.getXCoord(), currWall.getYCoord(), wallHeight, wallWidth));
                }
                cellToVisit.visitCell(); //visit this cell and it to the drawing array
                drawingArray.add(Arrays.asList(cellToVisit.getXCoord(), cellToVisit.getYCoord(), width, height));
                currCell = cellToVisit; //assign the currCell as the cell we just visited
            }else if(!stack.empty()){
                currCell = stack.pop(); //pop the top cell of the stack and make it the currCell
            }
        }
        //if the showGeneration box is not checked then make the timer speed 0
        if(!showGeneration) speed = 0;

        drawCellTimer = new Timer(speed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawGeneration(getGraphics(), visitedLabel);
            }
        });

        //get the coords, and height/width of the item to draw
        drawXCoord = drawingArray.get(currentDrawing).get(0);
        drawYCoord = drawingArray.get(currentDrawing).get(1);
        drawWidth = drawingArray.get(currentDrawing).get(2);
        drawHeight = drawingArray.get(currentDrawing).get(3);
        drawCellTimer.start();
    }

    //return the wall at the given coords
    public MazeWall getWallAt(int xCoord, int yCoord){
        MazeWall returnWall = null;
        for(int i = 0; i < walls.size(); i++){
            if(walls.get(i).getXCoord() == xCoord && walls.get(i).getYCoord() == yCoord){
                returnWall = walls.get(i); //set the wall to return as this wall
            }
        }
        return returnWall;
    }
    //return whether or not all the cells have been visited
    public boolean allCellsVisited(){
        for(int i = 0; i < cells.length; i++){
            if(!cells[i].getVisited()) return false;
        }
        return true;
    }

    public void pause(){//if we're generating or solving the maze then pause that action
        try {//if the drawCellTimer is running then we were generating the maze, so pause that timer
            if (drawCellTimer.isRunning()) {
                drawCellTimer.stop();
                drawTimerPaused = true;//this indicates that pause was pressed while generating the maze
            }
        }catch(NullPointerException e){
            //e.printStackTrace(); only needed for debugging purposes
        }
        try{ //if the solveMazeTimer is running then we were solving the maze, so pause that timer
            if(solveMazeTimer.isRunning()){
                solveMazeTimer.stop();
                solveTimerPaused = true;//this indicates that pause was pressed while solving the maze
            }
        }catch (NullPointerException e){
            //e.printStackTrace(); only needed for debugging purposes
        }
    }

    //user pressed resume so resume the generation or solving
    public void resume(){
        if(drawTimerPaused){ //if this timer was paused then we paused on the generation, so reume it
            drawCellTimer.start();
            drawTimerPaused = false;
        }
        if(solveTimerPaused){//if this timer was paused then we paused on the solving, so reume it
            solveMazeTimer.start();
            solveTimerPaused = false;
        }
    }
    
    //run the DFS algorithm to solve the maze
    public void solveMaze(JLabel visitedLabel, boolean showSolving){
        if(!mazeGenerated) return; //if the maze has not been generated then return
        if(startedToSolve == true){
            return; //if the we've already started to solve the maze and the user hit Pause then return
        }
        this.showSolving = showSolving; //set whether we show the solving animation or not
        //this list contains all the cells that we backtrack to having reached a dead-end in the maze
        List<List<Integer>> backtrackList = new ArrayList<List<Integer>>(); 
        //this list keeps track of all the cells we've visited
        solveList = new ArrayList<List<Integer>>(); 
        unvisitCells(); //unvisit all the cells
        Stack<MazeCell> stack = new Stack<MazeCell>(); //create a stack used for DFS
        MazeCell currCell = cells[0]; //set the currCell as the top left one
        stack.push(currCell);

        startedToSolve = true; //we've begun to solve the maze
        numVisited = 0; //we haven't visited any cells yet
        
        //run DFS until we reach the bottom right cell
        while(currCell.getIndex() != ((rows * columns) - 1)){
            currCell = stack.pop();
            if(!currCell.getVisited()){ //if we haven't visited this cell then set it as visited
                currCell.setVisited(true);
                //we add this cell to the solveList so we can color it green later
                solveList.add(Arrays.asList(currCell.getXCoord(), currCell.getYCoord(), width, height, 0));
                //we add the cell to the backtrackList in case we encounter it when we reach a dead-end and have to backtrack
                backtrackList.add(Arrays.asList(currCell.getXCoord(), currCell.getYCoord(), width, height, 1));
            }
            //keeps track of the # of neighbors we could visit
            int visitedNeighbors  = 0;

            //loop over currCells neighbors
            for(int i = 0; i < currCell.getNumNeighbors(); i++){
                if(!currCell.getNeighbor(i).getVisited()){ //we have not visited this neighbor yet
                    if(currCell.getIndex() + 1 == currCell.getNeighbor(i).getIndex()){
                        //this is a right neighbor so see if a wall exists here or not
                        //find the coords of the right wall and see if it has been drawn or undrawn
                        if(getWallAt(currCell.getXCoord() + width, currCell.getYCoord()).wallWasRemoved()){
                            stack.push(currCell.getNeighbor(i));
                            visitedNeighbors++;
                        }
                    }else if(currCell.getIndex() - 1 == currCell.getNeighbor(i).getIndex()){
                        //this is a left neighbor so see if there is a wall here or not
                        //if no wall exists then we can add the left neighbor to the stack
                        if(getWallAt(currCell.getXCoord() - wallWidth, currCell.getYCoord()).wallWasRemoved()){
                            stack.push(currCell.getNeighbor(i));
                            visitedNeighbors++;
                        }
                    }else if(currCell.getIndex() + columns == currCell.getNeighbor(i).getIndex()){
                        //this is a bottom neighbor. If no wall exists between currCell and this bottom neighbor
                        //then we can add the bottom neighbor to the stack
                        if(getWallAt(currCell.getXCoord(), currCell.getYCoord() + height).wallWasRemoved()){
                            stack.push(currCell.getNeighbor(i));
                            visitedNeighbors++;
                        }
                    }else if(currCell.getIndex() - columns == currCell.getNeighbor(i).getIndex()){
                        //this is the top neighbor. If no wall exists then we can add the top neighbor to the stack
                        if(getWallAt(currCell.getXCoord(), currCell.getYCoord() - wallWidth).wallWasRemoved()){
                            stack.push(currCell.getNeighbor(i));
                            visitedNeighbors++;
                        }
                    }
                }
            }
            //if we're at a dead end and not at the bottom-right cell then we need to backtrack
            if(visitedNeighbors == 0 && currCell.getIndex() != ((rows * columns) - 1)){
                //reverse the backtrack list so we can show the backtrack happen correctly
                Collections.reverse(backtrackList);
                for(int i = 0; i < backtrackList.size(); i++){
                    solveList.add(backtrackList.get(i)); //add the backtrack cells to the solveList so we can show them being drawn
                }
                backtrackList = new ArrayList<List<Integer>>(); //reset the backtrack list
            }
        }
        //if the showSolving box is not checked then the speed for the timer is 0
        if(!showSolving) generationSpeed = 0;
        solveMazeTimer = new Timer(generationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                     drawSolve((Graphics2D) getGraphics(), visitedLabel);
            }
        });
        currentDrawing = 0; //the current item we're drawing is at the 0th index in the solveList

        //get the x,y coords of the item to draw, and its height/width
        drawXCoord = solveList.get(currentDrawing).get(0);
        drawYCoord = solveList.get(currentDrawing).get(1);
        drawWidth = solveList.get(currentDrawing).get(2);
        drawHeight = solveList.get(currentDrawing).get(3);
        solveMazeTimer.start();
    }
    //unvisit all the cells
    public void unvisitCells(){
        for(int i = 0; i < cells.length; i++){
            cells[i].setVisited(false);
        }
    }
    
    //this function draws the maze being solved
    public void drawSolve(Graphics2D g2d, JLabel visitedLabel){
        //if the 4th index is 0 then we aren't backtracking so draw that cell green
        if(solveList.get(currentDrawing).get(4) == 0){
            Color green = Color.decode("#1A5604"); //this green is much more subtle than the default offered
            g2d.setColor(green);
            setLabel(visitedLabel);
        }else if(solveList.get(currentDrawing).get(4) == 1){//if the 4th index is 1 then we're backtracking, so draw grey
            Color gray = Color.decode("#3B3B3B");
            g2d.setColor(gray);
        }
        g2d.fillRect(drawXCoord, drawYCoord, drawWidth, drawHeight);
        currentDrawing++; //increment so we can go to the next item to draw
        if(currentDrawing >= solveList.size()){//stop the drawing if everything has been drawn
            solveMazeTimer.stop(); //the maze has been solved so stop the timer 
            return;
        }
        //get the coords and height/width of the next item to draw
        drawXCoord = solveList.get(currentDrawing).get(0);
        drawYCoord = solveList.get(currentDrawing).get(1);
        drawWidth = solveList.get(currentDrawing).get(2);
        drawHeight = solveList.get(currentDrawing).get(3);
    }
}
