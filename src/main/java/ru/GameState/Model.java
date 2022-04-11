package ru.GameState;

import ru.Entity.Coast;
import ru.Entity.Man;
import ru.Entity.Raft;
import ru.Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Model extends GameState{
    // массив берегов
    private ArrayList<Coast> coasts;
    public static Raft raft;

    private ArrayList<Man> people;
    private ArrayList<Man> peopleToCarry;

    private int NUM_TO_CARRY;
    private int NUM_PEOPLE;
    public Model(GameStateManager gsm){
        init();
        this.gsm = gsm;
    }
    public void init(){
        coasts = new ArrayList<Coast>();

        coasts.add(new Coast());
        coasts.get(0).setPosition(0 + coasts.get(0).getCWidth(), GamePanel.HEIGHT/2 + coasts.get(0).getCHeight());
        coasts.add(new Coast());
        coasts.get(1).setPosition(GamePanel.WIDTH, GamePanel.HEIGHT/2+ coasts.get(1).getCHeight());

        raft = new Raft();
        raft.setPosition(0 + coasts.get(0).getCWidth() + raft.getCWidth(), GamePanel.HEIGHT/2 + raft.getCHeight());
        raft.setVector(0.5,0);

        NUM_TO_CARRY = randomWithRange(1,10);
        NUM_PEOPLE = 40;

        people = new ArrayList<Man>();
        peopleToCarry = new ArrayList<Man>();
        for(int i = 0; i < NUM_PEOPLE; i++){
            people.add(new Man());
        }
        //загрузка картинки для человека. 15 - размер изображения человека
        try {
            Man.manIcon = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/Man.png"));
            Man.manIcon = GamePanel.resize(Man.manIcon,15,15);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //заполнение массива перевозимых людей
        Man m;
        for(int i = 0; i < NUM_TO_CARRY; i++){
            m = people.get(i);
            m.setPosition(GamePanel.WIDTH - coasts.get(0).getCWidth() + i*15,GamePanel.HEIGHT/2-m.getCHeight());
            peopleToCarry.add(m);
            people.remove(i);
        }
    }

    @Override
    public void update() {
        raft.update(coasts);
        Man m;
        //Установка количества перевозимых на плоте людей
        int toCarry;
        if (raft.MAX_TO_CARRY > peopleToCarry.size()){
            toCarry = peopleToCarry.size();
        }
        else {
            toCarry = raft.MAX_TO_CARRY;
        }
        // дошел до правого берега
        if (!raft.canGo && !raft.isRight){
            for(int i = 0; i < toCarry; i++){
                m = peopleToCarry.get(i);
                if(!m.onRaft){
                    m.setVector(-0.3,0);
                }
                if(m.getX() == raft.getX() - (i+1)*m.getCWidth()){
                    m.onRaft = true;
                }
            }
        }
        //Если всех людей подобрали, можно плыть
        int counter = 0;
        if(!raft.isRight){
            for(Man man:peopleToCarry){
                if(man.onRaft){
                    counter++;
                }
            }
            if (counter == toCarry){
                if(!raft.canGo){
                    raft.setPosition(raft.getX()-1,raft.getY());
                }
                raft.canGo = true;
            }
        }
        // плот дошел до левого берега
        if(raft.isRight && !raft.canGo){
            for(int i = 0; i < toCarry; i++){
                m = peopleToCarry.get(i);
                m.onRaft = false;
                m.setVector(-0.3,0);
            }
            //Как только последний человек сошел с плота, плот отправляется обратно
            if(peopleToCarry.get(0).getX() + peopleToCarry.get(0).getCWidth() < raft.getX()-raft.getCWidth()){
                raft.setPosition(raft.getX()+1,raft.getY());
                raft.canGo = true;
            }
        }
        // удаление человека если за пределами экрана слева
        int j = 0;
        while(j != peopleToCarry.size()){
            m = peopleToCarry.get(j);
            if (m.getX() + m.getCWidth() < 0){
                peopleToCarry.remove(j);
                j--;
            }
            j++;
        }

        //Обновление состояния людей
        for(Man man:peopleToCarry){
            man.update();
        }
        //Создание новой партии людей для перевозки
        if (peopleToCarry.isEmpty()){
            NUM_TO_CARRY = randomWithRange(1,10);
            if (NUM_TO_CARRY > people.size()){
                NUM_TO_CARRY = people.size();
            }
            for(int i = 0; i < NUM_TO_CARRY; i++){
                m = people.get(i);
                m.setPosition(GamePanel.WIDTH - coasts.get(0).getCWidth() + i*15,GamePanel.HEIGHT/2-m.getCHeight());
                peopleToCarry.add(m);
                people.remove(i);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.fillRect(0,0,GamePanel.WIDTH,GamePanel.HEIGHT);
        Coast c;
        Rectangle r;
        g.setColor(Color.BLACK);
        for(int i = 0; i < coasts.size(); i++){
            c = coasts.get(i);
            r = c.getRectangle();
            g.fillRect(r.x,r.y,r.width,r.height);
        }
        g.setColor(Color.GRAY);
        raft.draw(g);
        g.setColor(Color.BLUE);
        g.fillRect(
                coasts.get(0).getCWidth(),
                GamePanel.HEIGHT/2 + raft.getCHeight(),
                GamePanel.WIDTH - coasts.get(0).getCWidth()*2,
                GamePanel.HEIGHT/2-raft.getCHeight()
        );
        //отрисовка людей
        for(Man m:peopleToCarry){
            m.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void keyPressed(int k) {

    }

    @Override
    public void keyReleased(int k) {

    }

    public static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
}
