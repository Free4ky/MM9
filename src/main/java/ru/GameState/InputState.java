package ru.GameState;

import ru.Main.Game;
import ru.Main.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;

public class InputState extends GameState{

    private GamePanel gamePanel;
    private JPanel derived;
    private JButton start;
    private JTextField input;
    public static int num_people = 0;
    private long delay;
    private long timer;
    JPanel[] p;
    JLabel errorMsg;
    boolean hasError;
    public InputState(GameStateManager gsm){
        this.gsm = gsm;
        this.gamePanel = gsm.gamePanel;
        init();
    }
    public void init(){
        hasError = false;
        delay = 600;
        derived = new JPanel();
        derived.setPreferredSize(new Dimension(GamePanel.WIDTH*GamePanel.SCALE, GamePanel.HEIGHT * GamePanel.SCALE));
        derived.setBackground(Color.RED);
        derived.setLayout(new GridLayout(5,1));
        p = new JPanel[10];
        for(int i = 0; i < 5; i++){
            p[i] = new JPanel(new FlowLayout());
            derived.add(p[i]);
        }
        input = new JTextField(10);
        start = new JButton("Start model");
        start.setPreferredSize(new Dimension(150,40));
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isNumeric(input.getText())){
                    num_people = Integer.parseInt(input.getText());
                    gsm.gameStates.get(GameStateManager.MODEL).init();
                    gsm.setState(GameStateManager.MODEL);
                    gamePanel.remove(derived);
                }
                else{
                    errorMsg.setText("Wrong input");
                    hasError = true;
                    timer = System.nanoTime();
                }
            }
        });
        errorMsg = new JLabel("");
        errorMsg.setFont(new Font("Serif", Font.PLAIN, 24));
        p[1].add(errorMsg);
        JLabel title = new JLabel("Raft Model");
        title.setFont(new Font("Serif", Font.PLAIN, 84));
        p[0].add(title);
        p[4].add(start);
        JLabel act = new JLabel("Введите размер популяции: ");
        p[3].add(act);
        p[3].add(input);
        gamePanel.add(derived);
    }
    public int getNum_people(){
        return num_people;
    }
    public void update(){
        long elapsed = (System.nanoTime() - timer)/1000000;
        if ((elapsed > delay ) && hasError){
            errorMsg.setText("");
            hasError = !hasError;
        }
    }
    public void draw(Graphics2D g){
    }

    @Override
    public void keyPressed(int k) {

    }
    @Override
    public void keyReleased(int k) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
