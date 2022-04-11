/**
 * Cупер класс для всех состояний игры.
 * Содержит главные методы для всех состояний и экземпляр класса управляющего состояниями
 * @version 0.1
 */
package ru.GameState;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class GameState {

	public static boolean isPaused;
	public static boolean isStartPause;
	
	protected GameStateManager gsm;
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);


	public abstract void mouseClicked(MouseEvent e);

}
