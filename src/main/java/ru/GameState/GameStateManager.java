/**
 * Класс отвечающий за контроль и выбор состояний игры
 */
package ru.GameState;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameStateManager {
	
	public ArrayList<GameState> gameStates; // массив состояний игры
	public static int currentState; // текущее состояние игры
	
	public static final int INPUT = 1;
	public static final int MODEL = 0;

	public GameStateManager() {

		gameStates = new ArrayList<GameState>();
		
		currentState = MODEL;
		gameStates.add(new Model(this));
	}

	// Метод установки состояния игры
	public void setState(int state) {
		currentState = state;
		gameStates.get(currentState).init();
	}

	// Метод обновления
	public void update() {
		gameStates.get(currentState).update();
	}

	// Метод отрисовки
	public void draw(java.awt.Graphics2D g) {

		gameStates.get(currentState).draw(g);
	}


	// Обработка пользовательского ввода
	public void keyPressed(int k) {

		gameStates.get(currentState).keyPressed(k);
	}
	
	public void keyReleased(int k) {

		gameStates.get(currentState).keyReleased(k);
	}

	public void mouseClicked(MouseEvent e){
		gameStates.get(currentState).mouseClicked(e);
	}

}









