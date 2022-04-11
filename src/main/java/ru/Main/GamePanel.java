package ru.Main;

import ru.Entity.Coast;
import ru.Entity.Man;
import ru.Entity.Raft;

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

	// массив берегов
	private ArrayList<Coast> coasts;
	public static Raft raft;

	private ArrayList<Man> people;
	private ArrayList<Man> peopleToCarry;

	private int NUM_TO_CARRY;
	private int NUM_PEOPLE;

	public GamePanel() {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true); // позволяет фокусироваться на объекте GamePanel. Необходимо для обработки пользовательского ввода
		requestFocus(); // получает фокус
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
		running = true;

		coasts = new ArrayList<Coast>();

		coasts.add(new Coast());
		coasts.get(0).setPosition(0 + coasts.get(0).getCWidth(),GamePanel.HEIGHT/2 + coasts.get(0).getCHeight());
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

	// Запуск задачи потока
	public void run() {
		
		init();
		
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

	// Метод отрисовки компонентов игры.
	// Компоненты игры сначала отрисовываются на холст image в методе draw
	private void draw() {
		//draw bg
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

	// Этот метод отвечает уже за отрисовку холста на экран
	// Такой подход называется double buffering (двойная буферизация)
	// Это позволяет снизить блики, мерцания и нечеткости итоговой картинки
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static int randomWithRange(int min, int max)
	{
		int range = (max - min) + 1;
		return (int)(Math.random() * range) + min;
	}
}