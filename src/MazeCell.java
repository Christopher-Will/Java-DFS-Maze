import java.util.ArrayList;

//this class represents a cell in the maze
public class MazeCell {
    private boolean visited; //whether the cell has been visited yet or not
    //the x,y coords of the cell and its height and width
    private int xCoord;
    private int yCoord;
    private int width;
    private int height;
    private int index; //the cell's index in the array of maze cells
    //an array of other cells which are this cells neighbors
    private MazeCell[] neighbors;

    public MazeCell(int xCoord, int yCoord, int width, int height, int index){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;
        this.index = index;
        visited = false; //cell initially has not been visited
    }
    //visit the cell
    public void visitCell(){
        visited = true;
    }
    //return if the cell has been visited
    public boolean getVisited(){
        return visited;
    }

    //getters for the x,y coords of the cell, as well as its index
    public int getIndex(){
        return index;
    }
    public int getXCoord(){
        return xCoord;
    }
    public int getYCoord(){
        return yCoord;
    }

    //assign the neighbors array to the provided array
    public void setNeighbors(MazeCell[] neighbors){
        this.neighbors = neighbors;
    }

    //return whether this cell has any neighbors which have not been visited yet
    public boolean hasUnvisitedNeighbors(){
        for(int i = 0; i < neighbors.length; i++){
            if(!neighbors[i].getVisited()) return true;
        }
        return false;
    }

    //return an array list of neighbors that have not been visited yet
    public ArrayList<MazeCell> getUnvisitedNeighbors() {
        ArrayList<MazeCell> unvisitedNeighbors = new ArrayList<MazeCell>();
        for(int i = 0; i < neighbors.length; i++){
            if(!neighbors[i].getVisited()) {
                unvisitedNeighbors.add(neighbors[i]);
            }
        }
        return unvisitedNeighbors;
    }
    //set whether this cell has been visited yet or not
    public void setVisited(boolean visited){
        this.visited = visited;
    }

    //get the # of neighbors this cell has
    public int getNumNeighbors(){
        return neighbors.length;
    }

    //return this cells ith neighbor
    public MazeCell getNeighbor(int i){
        return neighbors[i];
    }

}
