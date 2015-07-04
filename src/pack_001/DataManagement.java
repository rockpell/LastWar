package pack_001;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class DataManagement {
	private static DataManagement instance;
	public static DataManagement getInstance(){
		if(instance == null){
			instance = new DataManagement();
		}
		
		return instance;
	}
	
	private Player player;
	private GameLevel gameLevel;
	
	private DataManagement(){
		player = new Player();
		player.setPosition(50, 50);
		player.setSize(48, 48);
		
		gameLevel = new GameLevel();
		gameLevel.pattern1();
//		new Laser1(0, 200, 1200, 1);
//		new Laser1(0, 600, 1200, 1);
	}
	
	public Player getPlayer(){
		return player;
	}
}

class Player extends Colider implements Unit{
	private float x, y;
	private float dx = 0, dy = 0;
	private int width, height;
	private int hp = 100;
	private int speed = 5;
	
	Engine engine;
	
	public Player(){
		engine = Engine.getInstance();
	}
	
	public void move(String dir){
		if(dir.equals("up")){
			dy = -speed;
			dx = 0;
		} else if(dir.equals("down")){
            dy = speed;
            dx = 0;
		} else if(dir.equals("right")){
			dx = speed;
			dy = 0;
		} else if(dir.equals("left")){
			dx = -speed;
			dy = 0;
		} else if(dir.contains("up") && dir.contains("right")){
			dy = -(float) (speed / Math.sqrt(2));
			dx = (float) (speed / Math.sqrt(2));
		} else if(dir.contains("up") && dir.contains("left")){
			dy = -(float) (speed / Math.sqrt(2));
			dx = -(float) (speed / Math.sqrt(2));
		} else if(dir.contains("down") && dir.contains("right")){
			dy = (float) (speed / Math.sqrt(2));
			dx = (float) (speed / Math.sqrt(2));
		} else if(dir.contains("down")  && dir.contains("left")){
			dy = (float) (speed / Math.sqrt(2));
			dx = -(float) (speed / Math.sqrt(2));
		} else if(dir.equals("stop")){
			dx = 0;
			dy = 0;
		}
	}
	
	@Override
	public void work() {
		// TODO Auto-generated method stub
		if(x + dx > 0){
			x += dx;
		}
		if(y + dy > 35){
			y += dy;
		}
		
	}

	@Override
	public void dead() {
		// TODO Auto-generated method stub
	}

	@Override
	public int getHp() {
		// TODO Auto-generated method stub
		return hp;
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		setBox(0, 0, width, height);
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPosition(Point2D.Float point){
		this.x = point.x;
		this.y = point.y;
	}
	
	public void setPosition(Point point){
		this.x = point.x;
		this.y = point.y;
	}
	
	public Point2D.Float getPosition(){
		return new Point2D.Float(x, y);
	}
	
	public Rectangle2D.Float getBounds() {
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}
	
	public boolean collision(Rectangle2D.Float target){
		return target.intersects(this.getBounds());
	}
	
}

class Laser1 extends Colider implements Laser{
	private String name;
	private int x = 0, y = 0, width = 0, height = 0;
	private int count = 0;
	private boolean trigger = false;
	
	public Laser1(int x, int y, int width, int height){
		Engine.getInstance().addColider(this);
		setSize(width, height);
		setPosition(x, y);
		setBox(0, 0, width, height);
		System.out.println("create Laser1");
		
		if(x == 0){
			name = "row";
		} else if(y == 0){
			name = "col";
		}
	}
	
	public void count(){
		count++;
		if(count > 80 && !trigger){
			trigger = true;
//			new Laser1(0, 400, 1200, 1);
		} else if(count > 120){
			dead();
		}
		
	}
	
	@Override
	public void dead() {
		// TODO Auto-generated method stub
		Engine.getInstance().removeColider(this);
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setImage(Image image) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle2D.Float getBounds() {
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int getPosition(String name){
		if(name.equals("x")){
			return x;
		} else {
			return y;
		}
	}
	
	public int getSize(String name){
		if(name.equals("width")){
			return width;
		} else {
			return height;
		}
	}
	
	public boolean getTrigger(){
		return trigger;
	}
	
	public String getName(){
		return name;
	}
	
}

class GameLevel {
	private Engine engine;
	private int nowLevel = 0;
	
	public GameLevel(){
		engine = Engine.getInstance();
	}
	
	void levelStart(){
		
	}
	
	void pattern1(){
		int time = engine.getPlayTime();
		int itime = engine.getInvokeTime();
		
//		if(time - itime )
		Timer timer = new Timer();
		
		TimerTask myTask = new TimerTask() {
		    public void run() {
		    	new Laser1(150, 0, 1, 1000);
		    }
		};
		
		TimerTask myTask2 = new TimerTask() {
		    public void run() {
		    	new Laser1(0, 300, 1200, 1);
		    }
		};
		
		
		timer.schedule(myTask, 1000);
		timer.schedule(myTask2, 3000);
	}
}