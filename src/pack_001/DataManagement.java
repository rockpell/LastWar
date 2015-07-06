package pack_001;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.parser.JSONParser;

public class DataManagement {
	private static DataManagement instance;
	public static DataManagement getInstance(){
		if(instance == null){
			instance = new DataManagement();
		}
		
		return instance;
	}
	
	private Player player;
	
	private DataManagement(){
		player = new Player();
		player.setPosition(50, 50);
		player.setSize(48, 48);
		
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
		if(y + dy > 50){
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
	private int count = 0, countLimit = 60, deadLimit = 85;
	private boolean trigger = false;
	
	public Laser1(int x, int y){
		Engine.getInstance().addColider(this);
		setPosition(x, y);
		
		System.out.println("create Laser1");
		
		if(x == 0){
			name = "row";
			width = 1200;
			height = 10;
		} else if(y == 0){
			name = "col";
			width = 10;
			height = 1000;
		}
		
		setSize(width, height);
		setBox(0, 0, width, height);
	}
	
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
		if(count > countLimit && !trigger){
			trigger = true;
//			new Laser1(0, 400, 1200, 1);
		} else if(count > deadLimit){
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
	
	public int calLaserSize(String text){
		if(text.equals("height")){
			int temp = countLimit - height;
			return count - temp;
		} else if(text.equals("width")){
			int temp = countLimit - width;
			return count - temp;
		}
		
		return 0;
	}
	
}

class GameLevel {
	private Engine engine;
	private int nowLevel = 0;
	private boolean levelUp = true, refresh = false, patternChange = false;
	private int targetIndex = 0;
	
	public GameLevel(){
		engine = Engine.getInstance();
	}
	
	public void levelStart(){
		if(levelUp){
			nowLevel += 1;
			levelUp = false;
			engine.refreshInvoke();
		}
		
		if(refresh){
			refresh = false;
			engine.refreshInvoke();
			patternChange = true;
		}
		
		if(patternChange){
			patternChange = false;
			if(targetIndex == 0){
				targetIndex = 1;
			} else if(targetIndex == 1){
				targetIndex = 0;
			}
		}
		
		if(targetIndex == 0){
			pattern1();
		} else if(targetIndex == 1) {
			pattern2();
		}
		
	}
	
	private void pattern1(){
		int time = engine.getPlayTime();
		int itime = engine.getInvokeTime();
		
		switch(time - itime){
		case 100 :
			new Laser1(150, 0);
			break;
		case 160 :
			new Laser1(190, 0);
			break;
		case 300 :
			new Laser1(0, 300);
			new Laser1(0, 500);
			break;
		case 400 :
			new Laser1(200, 0);
			new Laser1(0, 700);
		case 500 :
			new Laser1(180, 0);
			new Laser1(0, 400);
			break;
		case 700 :
			new Laser1(100, 0);
			new Laser1(400, 0);
			new Laser1(0, 200);
			new Laser1(0, 500);
			break;
		case 850 :
			refresh = true;
			break;
		}
	}
	
	private void pattern2(){
		int time = engine.getPlayTime();
		int itime = engine.getInvokeTime();
		
		switch(time - itime){
		case 100 :
			new Laser1(150, 0);
			new Laser1(250, 0);
			new Laser1(350, 0);
			new Laser1(450, 0);
			break;
		case 200 :
			new Laser1(180, 0);
			new Laser1(0, 250);
			break;
		case 250 :
			new Laser1(100, 0);
			new Laser1(0, 700);
			break;
		case 300 :
			new Laser1(0, 200);
			new Laser1(0, 300);
			new Laser1(0, 400);
			new Laser1(0, 500);
			break;
		case 450 :
			refresh = true;
			break;
		}
	}
}

class JPaser {
	
	JPaser(){
		JSONParser jsonParser = new JSONParser();
	}
}
