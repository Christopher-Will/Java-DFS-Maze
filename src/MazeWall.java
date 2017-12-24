import java.awt.*;

//This class represents a wall in the maze
public class MazeWall {
    //the x,y coords of the wall, and its height and width
    private int xCoord;
    private int yCoord;
    private int width;
    private int height;
    private boolean wallRemoved; //whether the wall has been removed or not

    public MazeWall(int xCoord, int yCoord, int width, int height) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;
        wallRemoved = false; //wall initially has not been removed

    }
    //remove the wall
    public void setWallRemoved(boolean wallRemoved){
        this.wallRemoved = wallRemoved;
    }
    //get if the all has been removed or not
    public boolean wallWasRemoved(){
        return wallRemoved;
    }
    //the following are getters for the x,y coords, and height/width
    public int getXCoord(){
        return xCoord;
    }
    public int getYCoord(){
        return yCoord;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    //remove the wall by drawingit black
    public void removeWall(Graphics2D g2d){
        g2d.setColor(Color.BLACK);
        g2d.fillRect(xCoord, yCoord, width, height);
    }
}
