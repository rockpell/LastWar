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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.IntegerSyntax;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
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
	private final Set<Wall1> wallSet = new HashSet<Wall1>();
	private final Set<Laser1> coliderSet = new HashSet<Laser1>();
	private JParser gameScenario;
	private Player player;
	
	public final int screenWidth = 1200, screenHeight = 800;
	public final int rowNumber = 11, colNumber = 21;
	public final int rowStartX1 = 20, rowStartX2 = screenWidth - 50, rowStartY = 110, colStartX = 90, colStartY1 = 50, colStartY2 = screenHeight - 150;
	
	private DataManagement(){
		gameScenario = new JParser();
		player = new Player();
		player.setPosition(550, 350);
		player.setSize(48, 48);
		
		initArrow();
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
	
	public synchronized boolean addWall(float x, float y){
		wallSet.add(new Wall1(x, y));
		return false;
	}
	
	public synchronized boolean removeWall(Wall1 target){
		return wallSet.remove(target);
	}
	
	public Set<LaserArrow> getArrowSet(){
		return arrowSet;
	}
	
	public Set<Wall1> getWallSet(){
		return wallSet;
	}
	
	public JParser getScenario(){
		return gameScenario;
	}
	
	public synchronized boolean addColider(Laser1 target){
		return coliderSet.add(target);
	}
	
	public synchronized boolean removeColider(Laser1 target){
		return coliderSet.remove(target);
	}
	
	public Set<Laser1> getColiderSet(){
		return coliderSet;
	}
	
	public Laser1 findColiderLaser(Laser1 target){
		for(Laser1 la : coliderSet){
			if(la.equals(target)){
				return la;
			}
		}
		return null;
	}
}

class Player extends Colider implements Unit{
	private float x, y;
	private float dx = 0, dy = 0;
	private int width, height;
	private int hp = 100;
	private int speed = 5;
	private int swidth = 1100, sheight = 600;
	private boolean isMoveUp = false, isMoveDown = false, isMoveLeft = false, isMoveRight = false;
	private boolean outTrigger = false;
	
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
		if(y + dy > 80 && y + dy < sheight){
			y += dy;
		}
		
		if(isMoveRight){
			x -= speed;
			isMoveRight = false;
		}
		if(isMoveLeft){
			x += speed;
			isMoveLeft = false;
		}
		if(isMoveUp){
			y += speed;
			isMoveUp = false;
		}
		if(isMoveDown){
			y -= speed;
			isMoveDown = false;
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
	
	public void checkMoveable(Rectangle2D.Float target){
//		target
		if( (x + cx) + cwidth >= target.x && x + cx < target.x 
				&& y + cy < target.y + target.height && y + cy + cheight > target.y){ // 물체를 기준으로 왼쪽에서 충돌
//			System.out.println("colide left");
			isMoveRight = true;
		}
		if( x + cx <= target.x + target.width && (x + cx + cwidth) > target.x + target.width
				&& y + cy < target.y + target.height && y + cy + cheight > target.y){ // 물체 오른쪽에 충돌
//			System.out.println("colide right");
			isMoveLeft = true;
		}
		if( y + cy < target.y && y + cy + cwidth >= target.y
				&& x + cx < target.x + target.width && x + cx + cwidth > target.x){ // 물체 위쪽에 충돌
//			System.out.println("colide up");
			isMoveDown = true;
		}
		if( y + cy <= target.y + target.height && y + cy + cheight > target.y + target.height
				&& x + cx < target.x + target.width && x + cx + cwidth > target.x){ // 물체 아래쪽에 충돌
//			System.out.println("colide down");
			isMoveUp = true;
		}
	}
	
	public boolean getOutTrigger(){
		return outTrigger;
	}
	
	public void setOutTrigger(boolean value){
		outTrigger = value;
	}
}

class Wall1 extends Colider implements Wall {
	
	private float x, y;
	private int width, height;
	private int count;
	private int hp = 5;
	private boolean outTrigger = false, outTriggerMoment = false; // 물체 내부에서 나간 후에 충돌 처리
	
	Wall1(float x, float y){
		setPosition(x, y);
		setSize(48, 48);
		trigger = true; // 충돌 가능
	}
	
	@Override
	public void dead() {
		// TODO Auto-generated method stub
		DataManagement.getInstance().removeWall(this);
	}

	public void work() {
		if(hp <= 0){
			dead();
		}
	}
	
	public void damage(){
		hp -= 1;
		System.out.println("wall damaged  :  " + hp);
	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
		setBox(0, 0, width, height);
	}
	
	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getPosition(String text){
		if(text.equals("x")){
			return x;
		} else {
			return y;
		}
	}
	
	
	@Override
	public Float getBounds() {
		// TODO Auto-generated method stub
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target) {
		// TODO Auto-generated method stub
		return target.intersects(this.getBounds());
	}
	
	public boolean getOutTrigger(){
		return outTrigger;
	}
	
	public void setOutTrigger(boolean value){
		outTrigger = value;
	}
	
	public boolean getOutTriggerMoment(){
		return outTriggerMoment;
	}
	
	public void setOutTriggerMoment(boolean value){
		outTriggerMoment = value;
	}
	
	public int getHp(){
		return hp;
	}
	
	public int getSize(String text){
		if(text.equals("height")){
			return height;
		} else {
			return width;
		}
	}
}

class Laser1 extends Colider implements Laser{
	private String name;
	private float x = 0, y = 0, width = 0, height = 0;
	private int count = 0, countLimit = 50, deadLimit = 75;
	private int indexX, indexY;
	private boolean wallColide = false;
	private Wall1 targetWall;
	
	private DataManagement dm;
	private LaserArrow linkLar;
	
	public Laser1(int x, int y){
		dm = DataManagement.getInstance();
		
		if(dm.addColider(this)){
			linkLar = dm.findArrow(x, y);
			linkLar.setExist(true);
		}
		
		setPosition(x, y);
		
		indexX = x;
		indexY = y;
		
//		setSize(width, height); // setPosition에서 size도 입력
		setBox(0, 0, width, height);
	}
	
	
	public void work(){
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
		if(dm.removeColider(this)){
			linkLar.setExist(false);
		}
		
		// wall에 데미지 입히는것 여기서 실행
		// 충돌 처리가 여러번 일어나기 때문에 여기서 실행해야함
		if(targetWall != null){
			targetWall.damage();
		}
		
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setPosition(int x, int y) {
		
		if(x == 0 || x == 1){ // 분리
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
	
	public float getPosition(String name){
		if(name.equals("x")){
			return x;
		} else {
			return y;
		}
	}
	
	public float getSize(String name){
		if(name.equals("width")){
			return width;
		} else {
			return height;
		}
	}
	
	public String getName(){
		return name;
	}
	
	public float calLaserSize(String text){
		if(text.equals("height")){
			float temp = countLimit - height;
			return count - temp;
		} else if(text.equals("width")){
			float temp = countLimit - width;
			return count - temp;
		}
		
		return 0;
	}
	
	public void setWallColide(Wall1 w){
		targetWall = w;
		setSizeWhileColide();
	}
	
	private void setSizeWhileColide(){
		Rectangle2D.Float wallRectange = targetWall.getBounds();
		
		if(name.equals("row1")){
			width = wallRectange.x - dm.rowStartX1 - 32;
			cwidth = width;
		} else if(name.equals("row2")){
			width = 1150 - 32;
			x = wallRectange.x + wallRectange.width;
			width -= (x - 32);
			cwidth = width;
		} else if(name.equals("col1")){
			height = wallRectange.y - dm.colStartY1 - 32;
			cheight = height;
		} else if(name.equals("col2")){
			height = 600 - 32;
			y = wallRectange.y + wallRectange.height;
			height =  height - (y - 32*2 - 16);
			cheight = height;
		}
//		System.out.println("dm :    " + this.getBounds());
	}
	
	public boolean getWallColide(){
		return wallColide;
	}
	
	public Wall1 getTargetWall(){
		return targetWall;
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
	private DataManagement dm;
	
	private Map<String, ArrayList<String>> sequenceData;
	
	private int nowLevel = 0, nowSequence = 0;
	private boolean levelUp = true, refresh = false, patternChange = false;
	private int targetIndex = 0;
	private String patternName = null;
	
	public GameLevel(){
		engine = Engine.getInstance();
		dm = DataManagement.getInstance();
		sequenceData =  dm.getScenario().getSequenceData();
		
		patternName = sequenceData.get("1").get(0);
		
		targetIndex = 1;
		patternParser();
	}
	
	public void levelStart(){
		if(levelUp){
			nowLevel += 1;
			nowSequence += 1;
			levelUp = false;
			engine.refreshInvoke();
			System.out.println("nowSequence : "+nowSequence);
			if(nowSequence > 4){
				nowSequence = 1;
			}
		}
		
		if(refresh){
			refresh = false;
			engine.refreshInvoke();
			patternChange = true;
		}
		
		if(patternChange){
			ArrayList<String> tempList = sequenceData.get(""+nowSequence);
			patternChange = false;
			
			patternName = tempList.get(targetIndex);
			
			targetIndex += 1;
			
			if(tempList.size() <= targetIndex){
				targetIndex = 0;
				levelUp = true;
			}
			
		}
		
		patternParser();
		
	}
	
	private void patternParser(){
		Map<String, ArrayList<Point>> patternMap = dm.getScenario().getPatternData().get(patternName).getMap();
		int time = engine.getPlayTime();
		int itime = engine.getInvokeTime();
		String timeText = "" + (time - itime);
		
		if(patternMap.containsKey(timeText)){
			ArrayList<Point> tempList = patternMap.get(timeText);
			
			if(tempList.size() == 0){
				refresh = true;
			}
			
			for(int i = 0; i < tempList.size(); i++){
				Point tempPoint = tempList.get(i);
				new Laser1(tempPoint.x, tempPoint.y);
			}
		}
		
	}
	
}

class JParser {
	
	private Map<String, ArrayList<String>> sequenceData = new HashMap<String, ArrayList<String>>();
	private Map<String, JsonPattern> patternData = new HashMap<String, JsonPattern>();

	JParser(){
		File abc = new File("resource/last_war.json");
		
		JSONParser jsonParser = new JSONParser();
		try{
			
			File bhc = new File(abc.getAbsolutePath());
			
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(bhc));
			
			Set<String> keys = jsonObject.keySet();
			
			for(String jso : keys){
				JSONObject jsonTemp = (JSONObject)jsonObject.get(jso);
				System.out.println(jso);
				
				System.out.println(jsonObject.get(jso));
				
				if(jso.equals("sequence")){
					JSONObject jsonTemp2 = (JSONObject)jsonObject.get(jso);
					seqenceToMap(jsonTemp2);
					
				} else {
					JSONObject jsonTemp2 = (JSONObject)jsonObject.get(jso);
					
					patternData.put(jso, new JsonPattern(jsonTemp2));
					
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	void seqenceToMap(Object target){
		if(target instanceof JSONObject){
			JSONObject tempObject = (JSONObject)target;
			Set<String> keys = tempObject.keySet();
			
			for(String text : keys){
				sequenceData.put(text, seqenceToArray(tempObject.get(text)) );
			}
		}
	}
	
	ArrayList<String> seqenceToArray(Object target){
		ArrayList<String> result = new ArrayList<String>();
		
		if(target instanceof JSONArray){
			JSONArray tempArray = (JSONArray)target;
			for(int i = 0; i < tempArray.size(); i++){
				result.add((String) tempArray.get(i));
			}
			
		}
		
		return result;
	}
	
	Map<String, ArrayList<String>> getSequenceData(){
		return sequenceData;
	}
	
	Map<String, JsonPattern> getPatternData(){
		return patternData;
	}
}

class JsonPattern {
	private Map<String, ArrayList<Point>> map = new HashMap<String, ArrayList<Point>>();
	
	JsonPattern(Object target){
		if(target instanceof JSONObject){
			JSONObject temp1 = ((JSONObject)target);
			Set<String> keys = temp1.keySet();
			
			for(String text : keys){
				map.put(text, toArray(temp1.get(text)) );
			}
			
		}
//		System.out.println(map);
	}
	
	ArrayList<Point> toArray(Object target){
		ArrayList<Point> result = new ArrayList<Point>();
		
		if(target instanceof JSONArray){
			JSONArray temp1 = ((JSONArray)target);
			
			for(int i = 0; i< temp1.size(); i++){
				JSONObject tempObject = (JSONObject)temp1.get(i);
				
				int x = Integer.parseInt("" + tempObject.get("x"));
				int y = Integer.parseInt("" + tempObject.get("y"));
				result.add(new Point(x, y));
			}
		}
		
		return result;
	}
	
	Map<String, ArrayList<Point>> getMap(){
		return map;
	}
}
