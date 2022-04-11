package ru.Entity;

import ru.Main.Game;
import ru.Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Man extends MapObject{

    public boolean onRaft;
    public static BufferedImage manIcon;
    public Man(){
        onRaft = false;
        cwidth = 15;
        cheight = 15;
        setVector(0,0);
    }

    public void update(){
        if(onRaft){
            setVector(GamePanel.raft.getDx(),0);
        }
        x+=dx;
    }
    public void draw(Graphics2D g){
        g.drawImage(manIcon,(int)x,(int)y,null);
    }
}
