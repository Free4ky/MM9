package ru.Entity;
import java.awt.*;
import java.util.ArrayList;

public class Raft extends MapObject{

    public int MAX_TO_CARRY;
    public boolean canGo;
    public boolean isRight;
    public Raft(){
        canGo = true;
        isRight = true;
        MAX_TO_CARRY = 4;
        cwidth = 60;
        cheight = 15;

    }

    public void update(ArrayList<Coast> objects){
        if (intersects(objects.get(0))){
            //dx = 0.5;
            canGo = false;
            isRight = true;
            setVector(0,0);
        }
        else if(intersects(objects.get(1))){
            canGo = false;
            isRight = false;
            setVector(0,0);
            //dx = -0.5;
        }
        else{
            if(isRight){
                setVector(0.5,0);
            }
            else{
                setVector(-0.5,0);
            }
        }
        if(canGo){
            x+=dx;
        }
    }
    public void draw(Graphics2D g){
        Rectangle r = this.getRectangle();
        g.fillRect(r.x,r.y,r.width,r.height);
    }
}
