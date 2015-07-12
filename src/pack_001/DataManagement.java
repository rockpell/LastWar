package pack_001;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataManagement {
	private static DataManagement instance = DataManagement.getInstance();
	public static DataManagement getInstance(){
		if(instance == null){
			instance = new DataManagement();
		}
		
		return instance;
	}
	
	private final Set<LaserArrow> arrowSet = new HashSet<LaserArrow>();
	private Player player;
	
	public final int screenWidth = 1200, screenHeight = 800;
	public final int rowNumber = 11, colNumber = 21;
	public final int rowStartX1 = 20, rowStartX2 = screenWidth - 50, rowStartY = 110, colStartX = 90, colStartY1 = 50, colStartY2 = screenHeight - 150;
	
	private DataManagement(){
		player = new Player();
		player.setPosition(550, 350);
		player.setSize(48, 48);
		
		initArrow();
		
		JPaser abc = new JPaser();
	}
	
	public Player getPlayer(){
		return player;
	}
	
	private void initArrow(){
		for(int i = 0; i < 4; i++){
			int limit = 0;
			
			if(i < 2){
				limit = rowNumber;
			} else {
				limit = colNumber;
			}
			
			for(int v = 0; v < limit; v++){
				arrowSet.add(new LaserArrow(i, v));
			}
		}
	}
	
	public LaserArrow findArrow(int x, int y){
		for (LaserArrow lar : arrowSet){
			if(lar.equals(new LaserArrow(x, y)) ){
				return lar;
			}
		}
		
		return null;
	}
	
	public Set<LaserArrow> getArrowSet(){
		return arrowSet;
	}
}

class Player extends Colider implements Unit{
	private float x, y;
	private float dx = 0, dy = 0;
	private int width, height;
	private int hp = 100;
	private int speed = 5;
	private int swidth = 1100, sheight = 630;
	
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
		if(x + dx > 50 && x + dx < swidth){
			x += dx;
		}
		if(y + dy > 50 && y + dy < sheight){
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
	private int count = 0, countLimit = 50, deadLimit = 75;
	private boolean trigger = false;
	private int indexX, indexY;
	
	private DataManagement dm;
	private LaserArrow linkLar;
	
	public Laser1(int x, int y){
		dm = DataManagement.getInstance();
		if(Engine.getInstance().addColider(this)){
			linkLar = dm.findArrow(x, y);
			linkLar.setExist(true);
		}
		
		setPosition(x, y);
		
		indexX = x;
		indexY = y;
		
		setSize(width, height);
		setBox(0, 0, width, height);
	}
	
	
	public void count(){
		count++;
		if(count > countLimit && !trigger){
			trigger = true;
		} else if(count > deadLimit){
			dead();
		}
		
	}
	
	@Override
	public void dead() {
		// TODO Auto-generated method stub
		if(Engine.getInstance().removeColider(this)){
			linkLar.setExist(false);
		}
		
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setPosition(int x, int y) {
//		this.x = x;
//		this.y = y;
		
		if(x == 0 || x == 1){
			name = "row" + (x+1);
			width = 1150 - 32;
			height = 10;
			this.x = dm.rowStartX1 + 32;
			this.y = dm.rowStartY + y*50 + 16;
		} else if(x == 2 || x == 3){
			name = "col" + (x-1);
			width = 10;
			height = 600 - 32;
			this.x = dm.colStartX + y*50 + 16;
			this.y = dm.colStartY1 + 32;
		}
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

class LaserArrow {
	private int indexX, indexY;
	private boolean exist = false;
	
	LaserArrow(int x, int y){
		indexX = x;
		indexY = y;
	}
	
	public int getIndexX(){
		return indexX;
	}
	
	public int getIndexY(){
		return indexY;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder().append(indexX).append(indexY).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof LaserArrow))
			return false;
		if(obj == this)
			return true;
		
		LaserArrow lar = (LaserArrow)obj;
		return new EqualsBuilder().append(indexX, lar.indexX).append(indexY, lar.indexY).isEquals();
		
	}
	
	public boolean isExist(){
		return exist;
	}
	
	public void setExist(boolean mod){
		exist = mod;
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
				targetIndex = 2;
			} else if(targetIndex == 2){
				targetIndex = 0;
			}
		}
		pattern1();
//		if(targetIndex == 0){
//			pattern1();
//		} else if(targetIndex == 1) {
//			pattern2();
//		} else if(targetIndex == 2) {
//			pattern3();
//		}
		
	}
	
	private void pattern1(){
		int time = engine.getPlayTime();
		int itime = engine.getInvokeTime();
		
		switch(time - itime){
		case 60 :
			new Laser1(0, 1);
			new Laser1(0, 5);
			new Laser1(0, 8);
			break;
		case 100 :
			new Laser1(2, 2);
			new Laser1(2, 0);
			new Laser1(2, 5);
			break;
		case 170 :
			new Laser1(3, 2);
			new Laser1(3, 5);
			break;
		case 220 :
			new Laser1(3, 15);
			break;
		case 270 :
			refresh = true;
			break;
		}
	}
	
//	private void pattern1(){
//		int time = engine.getPlayTime();
//		int itime = engine.getInvokeTime();
//		
//		switch(time - itime){
//		case 60 :
//			new Laser1(150, 0);
//			break;
//		case 160 :
//			new Laser1(190, 0);
//			break;
//		case 300 :
//			new Laser1(0, 300);
//			new Laser1(0, 500);
//			break;
//		case 400 :
//			new Laser1(200, 0);
//			new Laser1(0, 700);
//		case 500 :
//			new Laser1(180, 0);
//			new Laser1(0, 400);
//			break;
//		case 700 :
//			new Laser1(100, 0);
//			new Laser1(400, 0);
//			new Laser1(0, 200);
//			new Laser1(0, 500);
//			break;
//		case 800 :
//			refresh = true;
//			break;
//		}
//	}
//	
//	private void pattern2(){
//		int time = engine.getPlayTime();
//		int itime = engine.getInvokeTime();
//		
//		switch(time - itime){
//		case 60 :
//			new Laser1(150, 0);
//			new Laser1(250, 0);
//			new Laser1(350, 0);
//			new Laser1(450, 0);
//			break;
//		case 200 :
//			new Laser1(180, 0);
//			new Laser1(0, 250);
//			break;
//		case 250 :
//			new Laser1(100, 0);
//			new Laser1(0, 700);
//			break;
//		case 300 :
//			new Laser1(0, 200);
//			new Laser1(0, 300);
//			new Laser1(0, 400);
//			new Laser1(0, 500);
//			break;
//		case 400 :
//			refresh = true;
//			break;
//		}
//	}
//	
//	private void pattern3(){
//		int time = engine.getPlayTime();
//		int itime = engine.getInvokeTime();
//		
//		switch(time - itime){
//		case 60 :
//			new Laser1(150, 0);
//			break;
//		case 150 :
//			new Laser1(200, 0);
//			break;
//		case 200 :
//			new Laser1(250, 0);
//			break;
//		case 250 :
//			new Laser1(300, 0);
//			break;
//		case 300 :
//			new Laser1(350, 0);
//			break;
//		case 350 :
//			new Laser1(400, 0);
//			break;
//		case 450 :
//			refresh = true;
//			break;
//		}
//	}
}

class JPaser {
	
	JPaser(){
		File abc = new File("resource/last_war.json");
		
		JSONParser jsonParser = new JSONParser();
		try{
			
			File bhc = new File(abc.getAbsolutePath());
			
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(bhc));
			Set<String> keys = jsonObject.keySet();
			
			for(String jso : keys){
				System.out.println(jso);
				System.out.println(jsonObject.get(jso));
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
