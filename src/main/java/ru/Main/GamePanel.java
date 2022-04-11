package ru.Main;

import ru.Entity.Coast;
import ru.Entity.Man;
import ru.Entity.Raft;
import ru.GameState.GameStateManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
	
	// Размерности
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 4;
	
	// Игровой поток (thread)
	// нужен для регулировния скорости игры
	// Скорость зависит от производительности компьютера, если её не регулировать, будет слишком быстро
	private Thread thread;
	private boolean running;
	private int FPS = 70;
	private long targetTime = 1000 / FPS;
	
	// image
	private BufferedImage image;
	private Graphics2D g;
	private GameStateManager gsm;


	public GamePanel() {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true); // позволяет фокусироваться на объекте GamePanel. Необходимо для обработки пользовательского ввода
		requestFocus(); // получает фокус
		init();
	}

	// addNotify - Делает этот компонент JPanel отображаемым, подключая его к собственному экранному ресурсу.
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this); // Создание потока с передачей в него экземпляра GamePanel
			thread.start(); // Запуск потока с передачей в него экземпляра GamePanel
		}
	}

	// Метод инициализации
	private void init() {
		// Создания холста
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		//Создание пера для холста image
		g = (Graphics2D) image.getGraphics();
		gsm = new GameStateManager(this);
		running = true;
	}

	// Запуск задачи потока
	public void run() {

		long start;
		long elapsed;
		long wait;
		
		// game loop
		while(running) {
			
			start = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			
			elapsed = System.nanoTime() - start;

			// переменная wait отвечает за то время, которое нужно подождать потоку
			// прежде чем запускать задачу потока снова
			// Это позволяет регулировать скорость игры
			wait = targetTime - elapsed / 1000000;
			if(wait < 0) wait = 5;
			try {
				Thread.sleep(wait);
			}
			catch(Exception e) {
				e.printStackTrace(); // e.pritStackTrace() выводит стек ошибок. Удобно для отладки проекта
			}
		}
		
	}

	// Метод обновления компонентов игры
	private void update() {
		//Обновление состояния плота
		gsm.update();
	}

	// Метод отрисовки компонентов игры.
	// Компоненты игры сначала отрисовываются на холст image в методе draw
	private void draw() {
		//draw bg
		gsm.draw(g);
	}

	// Этот метод отвечает уже за отрисовку холста на экран
	// Такой подход называется double buffering (двойная буферизация)
	// Это позволяет снизить блики, мерцания и нечеткости итоговой картинки
	private void drawToScreen() {
		if(!(GameStateManager.currentState == GameStateManager.INPUT)){
			Graphics g2 = getGraphics();
			g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
			g2.dispose();
		}
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

}