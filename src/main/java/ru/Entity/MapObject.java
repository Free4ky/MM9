/**
 * Супер класс всех объектов карты.
 * Содержит основные поля и методы.
 */
package ru.Entity;

import ru.Main.GamePanel;

import java.awt.*;

// super class for all objects
public abstract class MapObject{
    public int saved_x;
    public int saved_y;


    // position and vector
    protected  double x;
    protected  double y;
    protected  double dx;
    protected  double dy;

    // dimensions, reading sprite sheets
    protected int width;
    protected int height;

    // collision box
    protected int cwidth;
    protected int cheight;



    // next position
    protected double xdest;
    protected double ydest;

    protected double xtemp;
    protected double ytemp;

    // collision points
    protected boolean topLeft;
    protected boolean topRight;
    protected boolean bottomLeft;
    protected boolean bottomRight;

    //animation
    public Animation animation;
    protected int currentAction;
    protected int previousAction;
    protected boolean facingRight; // for flip sprite


    // movement attributes
    protected double moveSpeed; // how fast object accelerates
    protected double maxSpeed;
    protected double stopSpeed; // deceleration speed
    protected double fallSpeed; // gravity;
    protected double maxFallSpeed;
    protected double jumpStart; // how high object can jump
    protected double stopJumpSpeed;

    //constructor
    public MapObject(){

    }

    public boolean intersects(MapObject o){
        //rectangle collision
        Rectangle r1 = getRectangle();
        Rectangle r2 = o.getRectangle();
        return r1.intersects(r2); // built-in method
    }

    public Rectangle getRectangle(){
        return new Rectangle(
                (int)x - cwidth, (int)y-cheight,cwidth,cheight);
    }

    public int getX(){return (int)x;}
    public int getY(){return (int)y;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public int getCHeight(){return cheight;}
    public int getCWidth(){return cwidth;}

    // global position
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setVector(double dx, double dy){
        this.dx = dx;
        this.dy = dy;
    }
    public double getDx(){
        return dx;
    }
    public double getDy(){
        return dy;
    }

}
