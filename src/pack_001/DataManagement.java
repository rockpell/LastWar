package pack_001;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONArray;
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
	private final Set<Wall1> wallSet = new HashSet<Wall1>();
	private final Set<Laser1> coliderSet = new HashSet<Laser1>();
	private final Set<Enemy1> enemySet = new HashSet<Enemy1>();
	private final Set<AlarmText> alarm_list = new HashSet<AlarmText>();
	private JParser gameScenario;
	private Player player;
	private WarpGate warp_gate;
	private AudioManagement am;
	private GameLevel gameLevel;
//	private Skill skill1, skill2, skill3, skill4, skill5;
	private Skill[] skill = new Skill[5];
	
	public Image mshi, hp_potion, hp_plus, wall_hp, wall_time;
	public Image arrow_right, arrow_left, arrow_up, arrow_down;
	public Image arrow_right_red, arrow_left_red, arrow_up_red, arrow_down_red;
	public Image brick_wall_001, excavator_001, excavator_002, brick_black;
	public Image closed_door, open_door;
	
	public final int screenWidth = 1200, screenHeight = 800;
	public final int rowNumber = 11, colNumber = 21;
	public final int rowStartX1 = 20, rowStartX2 = screenWidth - 50, rowStartY = 110, colStartX = 90, colStartY1 = 50, colStartY2 = screenHeight - 150;
	
	private int wallLimit = 5; // 갯수 제한이 아닌 벽 생성 스킬에 쿨타임 도입
	private int coolTime = 15, coolTimeLeft = 0;
	private int money = 0;
	private int[] cost = new int[5];
	private int wall_plus_hp = 0;
	
	private boolean gameStart = false, gameEnd = false;
	
	private DataManagement(){
		initData();
		skill[0] = new Skill("wall", 100, 725, 48, 48);
		skill[1] = new Skill("heal", 160, 725, 48, 48);
		skill[2] = new Skill("hp", 220, 725, 48, 48);
		skill[3] = new Skill("wallhp", 280, 725, 48, 48);
		skill[4] = new Skill("wallcool", 340, 725, 48, 48);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public WarpGate getWarpGate(){
		return warp_gate;
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
	
	public synchronized boolean addEnemy(Enemy1 target){
		return enemySet.add(target);
	}
	
	public synchronized boolean removeEnemy(Enemy1 target){
		return enemySet.remove(target);
	}
	
	public Set<Enemy1> getEnemySet(){
		return enemySet;
	}
	
	public Laser1 findColiderLaser(Laser1 target){
		for(Laser1 la : coliderSet){
			if(la.equals(target)){
				return la;
			}
		}
		return null;
	}
	
	public int getWallLimit(){
		return wallLimit;
	}
	
	public void setWallLimit(int value){
		wallLimit = value;
	}
	
	public void minusWallCoolTime(int value){
		coolTime -= value;
	}
	
	public int getWallSetCount(){
		return wallSet.size();
	}
	
	public int getMaxCoolTime(){
		return coolTime;
	}
	
	public int getCoolTimeLeft(){
		return coolTimeLeft;
	}
	
	public void initCoolTime(){
		coolTimeLeft = coolTime;
	}
	
	public void countCoolTime(){
		if(coolTimeLeft > 0){
			coolTimeLeft -= 1;
		}
		
	}
	
	public void initData(){
		coliderSet.removeAll(coliderSet);
		enemySet.removeAll(enemySet);
		wallSet.removeAll(wallSet);
		arrowSet.removeAll(arrowSet);
		
		gameScenario = new JParser();
		player = null;
		player = new Player();
		player.setPosition(566, 350);
		player.setSize(48, 48);
		
		initArrow();
		warp_gate = new WarpGate();
		warp_gate.setOpen();
		
		wall_plus_hp = 0;
		
		if(gameLevel != null)
			gameLevel.init();
		
		coolTime = 15;
		coolTimeLeft = 0;
		
		gameStart = false;
		gameEnd = false;
		
		money = 0;
		Engine.getInstance().setPlayTime(0);
		Engine.getInstance().initInvokeTime();
		
		initCost();
	}
	
	public void loadImage() {
		mshi = new ImageIcon("resource/people.png").getImage();
		arrow_right = new ImageIcon("resource/arrow_right.png").getImage();
		arrow_left = new ImageIcon("resource/arrow_left.png").getImage();
		arrow_up = new ImageIcon("resource/arrow_up.png").getImage();
		arrow_down = new ImageIcon("resource/arrow_down.png").getImage();
		arrow_right_red = new ImageIcon("resource/arrow_right_red.png").getImage();
		arrow_left_red = new ImageIcon("resource/arrow_left_red.png").getImage();
		arrow_up_red = new ImageIcon("resource/arrow_up_red.png").getImage();
		arrow_down_red = new ImageIcon("resource/arrow_down_red.png").getImage();
		brick_wall_001 = new ImageIcon("resource/brick_001.png").getImage();
		excavator_001 = new ImageIcon("resource/excavator_001.png").getImage();
		excavator_002 = new ImageIcon("resource/excavator_002.png").getImage();
		closed_door = new ImageIcon("resource/closed_door.png").getImage();
		open_door = new ImageIcon("resource/open_door.png").getImage();
		hp_potion = new ImageIcon("resource/hp_potion.png").getImage();
		hp_plus = new ImageIcon("resource/hp_plus.png").getImage();
		brick_black = new ImageIcon("resource/brick_black.png").getImage();
		wall_hp = new ImageIcon("resource/wall_hp.png").getImage();
		wall_time = new ImageIcon("resource/wall_time.png").getImage();
	}
	
	public AudioManagement getAudio(){
		if(am == null){
			am = new AudioManagement();
		}
		
		return am;
	}
	
	public boolean getGameStart(){
		return gameStart;
	}
	
	public void setGameStart(boolean val){
		gameStart = val;
	}
	
	public boolean getGameEnd(){
		return gameEnd;
	}
	
	public void setGameEnd(boolean val){
		gameEnd = val;
	}
	
	public void createGameLevel(int type){
		gameLevel = null;
		gameLevel = new GameLevel(type);
	}
	
	public GameLevel getGameLevel(){
		return gameLevel;
	}
	
	public Skill getSkill(int number){
		return skill[number];
	}
	
	public int getMoney(){
		return money;
	}
	
	public void addMoney(int value){
		money += value;
	}
	
	public void subMoney(int value){
		money -= value;
	}
	
	public int getCost(int value){
		return cost[value];
	}
	
	public void initCost(){
		cost[0] = 10;
		cost[1] = 50;
		cost[2] = 100;
		cost[3] = 200;
		cost[4] = 200;
	}
	
	public void upCost(int value){
		switch(value){
		case 1:
			if(cost[value] < 100){
				cost[value] += 30;
			} else {
				cost[value] += 50;
			}
			break;
		case 2:
			if(cost[value] < 200){
				cost[value] += 40;
			} else {
				cost[value] += 80;
			}
			break;
		case 3:
			if(cost[value] < 210){
				cost[value] += 40;
			} else {
				cost[value] += 80;
			}
			break;
		case 4:
			if(cost[value] < 210){
				cost[value] += 40;
			} else {
				cost[value] += 80;
			}
			break;
		}
	}
	
	public int getPlusHp(){
		return wall_plus_hp;
	}
	
	public void plusHp(){
		this.wall_plus_hp += 1;
	}
	
	public synchronized void addAlaram(AlarmText data){
		alarm_list.add(data);
	}
	
	public synchronized void removeAlarm(AlarmText data){
		alarm_list.remove(data);
	}
	
	public Set<AlarmText> getAlarmList(){
		return alarm_list;
	}
	
}

class Player extends Colider implements Unit{
	private float x, y;
	private float dx = 0, dy = 0;
	private int width, height;
	private int hp = 3, maxHp = 3;
	private int speed = 5;
	private int swidth = 1100, sheight = 600;
	private boolean isMoveUp = false, isMoveDown = false, isMoveLeft = false, isMoveRight = false;
	private boolean outTrigger = false, wall_able = false;
	private boolean damaged = false; // damaged == 피해 입음 상태 표시
	private int damage_count = 0;
	
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
		if(damaged){
			damage_count += 1;
			if(damage_count > 50) {
				damage_count = 0;
				damaged = false;
			}
		}
		
		if(x + dx > 50 && x + dx < swidth){
			x += dx;
		}
		if(y + dy > 80 && y + dy < sheight){
			y += dy;
		}
		
		if(x <= 50){
			x += speed;
		}
		if(x >= swidth){
			x -= speed;
		}
		if(y >= sheight){
			y -= speed;
		}
		if(y <= 80){
			y += speed;
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
	
	public void damaged(){
		if(!damaged){
			hp -= 1;
			damaged = true;
			if(hp <= 0){
				dead();
			}
		}
		
	}
	
	@Override
	public void dead() {
		DataManagement dm = DataManagement.getInstance();
		dm.getAudio().stop();
		dm.setGameStart(false);
		dm.setGameEnd(true);
		
		Screen.getInstance().beforeStartOn();
		
		Engine.getInstance().stopLoop();
		
		System.out.println("dead");
	}

	@Override
	public int getHp() {
		// TODO Auto-generated method stub
		return hp;
	}
	
	public int getMaxHp(){
		return maxHp;
	}
	
	public boolean isWallAble(){
		return wall_able;
	}
	
	public void setWallAble(boolean target){
		wall_able = target;
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
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void heal(){
		hp = maxHp;
	}
	
	public void maxHpUp(){
		maxHp += 1;
	}
}

class Enemy1 extends Colider implements Unit {
	private String type_name = "enemy1"; 
	private float x, y; 
	private float dx, dy;
	private int width, height;
	private float speed = 1.5f;
	private int hp = 5, maxHp = 5;
	private int money = 100;
	private int swidth = 1100, sheight = 600;
	private boolean isMoveUp = false, isMoveDown = false, isMoveLeft = false, isMoveRight = false;
	private boolean randMove = true; // randMove == 랜덤 이동(플레이어 인식 못할 경우 랜덤 이동) 
	private boolean damageable = false, damaged = false; // damageable == 플레이어 인식 시에만 데미지 입음, damaged == 피해 입음 상태 표시
	private boolean is_active = false; // 유닛 활성화 여부
	private Point2D.Float movePoint = null;
	private int rand_number = 0, direction = 0, damage_count = 0, active_count = 0;
	private float vision = 200.0f;
	
	private DataManagement dm;
	
	Enemy1(int hp){
		dm = DataManagement.getInstance();
		
		setHp(hp);
		
		setPosition(566.0f, 350.0f);
		setSize(48, 48);
		
		dm.addEnemy(this);
		dm.getWarpGate().setOpen();
	}
	
//	Enemy1(float x, float y){
//		dm = DataManagement.getInstance();
//		
//		dm.addEnemy(this);
//		dm.getWarpGate().setOpen();
//		
//		setPosition(x, y);
//		setSize(48, 48);
//	}
	
	public void move(Point2D.Float target){
		movePoint = target;
		
		if(target.x > this.x){
			dx = speed;
		} else if(target.x < this.x){
			dx = -speed;
		} else {
			dx = 0;
		}
		
		if(target.y > this.y){
			dy = speed;
		} else if(target.y < this.y){
			dy = -speed;
		} else {
			dy = 0;
		}
		
		if(dx != 0 && dy != 0){
			dx = (float)(dx / Math.sqrt(2));
			dy = (float)(dy / Math.sqrt(2));
		}
	}
	
	private void randomMove(){
		rand_number += 1;
		
		switch(direction){ // enemy의 이동 방향
		case 0 : //left
			dx = -speed;
			dy = 0;
			break;
		case 1 : // right
			dx = speed;
			dy = 0;
			break;
		case 2 : // up
			dy = -speed;
			dx = 0;
			break;
		case 3 : // down
			dy = speed;
			dx = 0;
			break;
		}
		
		if(rand_number > 100){
			directionChange();
			rand_number = 0;
		}
	}
	
	private void directionChange(){
		direction = (int)(Math.random() * 3);
	}
	
	private void calDistance(){
		Point2D.Float target = DataManagement.getInstance().getPlayer().getPosition();
		float x1 = (float) Math.pow((this.x - target.x), 2);
		float x2 = (float) Math.pow((this.y - target.y), 2);
		
		if(Math.sqrt(x1 + x2) < vision){
			randMove = false;
			damageable = true;
		} else {
			randMove = true;
			damageable = false;
		}
		
	}
	
	@Override
	public void work() {
		
		if(!is_active){
			active_count += 1;
			if(active_count > 20){
				is_active = true;
			}
			return;
		}
		
		calDistance();
		
		if(damaged){
			damage_count += 1;
			if(damage_count > 50) {
				damage_count = 0;
				damaged = false;
			}
		}
		
		if(x - speed < movePoint.x && movePoint.x < x + speed){
			dx = 0;
		}
		
		if(y - speed < movePoint.y && movePoint.y < y + speed){
			dy = 0;
		}
		
		if(!randMove){
			move(DataManagement.getInstance().getPlayer().getPosition());
		} else {
			randomMove();
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
		
		if(x + dx > 50 && x + dx < swidth){
			x += dx;
		} else if(x + dx < 50 || x + dx < swidth){
			directionChange();
		}
		
		if(y + dy > 80 && y + dy < sheight){
			y += dy;
		} else if(y + dy < 80 || y + dy > sheight){
			directionChange();
		}
		
		if(x <= 50){
			x += speed;
		}
		if(x >= swidth){
			x -= speed;
		}
		if(y >= sheight){
			y -= speed;
		}
		if(y <= 80){
			y += speed;
		}
	}
	
	public void checkMoveable(Rectangle2D.Float target){
		if( (x + cx) + cwidth >= target.x && x + cx < target.x 
				&& y + cy < target.y + target.height && y + cy + cheight > target.y){ // 물체를 기준으로 왼쪽에서 충돌
			isMoveRight = true;
		}
		if( x + cx <= target.x + target.width && (x + cx + cwidth) > target.x + target.width
				&& y + cy < target.y + target.height && y + cy + cheight > target.y){ // 물체 오른쪽에 충돌
			isMoveLeft = true;
		}
		if( y + cy < target.y && y + cy + cwidth >= target.y
				&& x + cx < target.x + target.width && x + cx + cwidth > target.x){ // 물체 위쪽에 충돌
			isMoveDown = true;
		}
		if( y + cy <= target.y + target.height && y + cy + cheight > target.y + target.height
				&& x + cx < target.x + target.width && x + cx + cwidth > target.x){ // 물체 아래쪽에 충돌
			isMoveUp = true;
		}
	}

	@Override
	public void dead() {
		// TODO Auto-generated method stub
		dm.removeEnemy(this);
		dm.addMoney(money);
		AlarmText at = new AlarmText(this.x, this.y);
		at.setText("P", money);
		dm.addAlaram(at);
	}
	
	public void damaged(){
		if(!damaged){
			hp -= 1;
			damaged = true;
			if(hp <= 0){
				dead();
			}
		}
	}
	
	public void addVision(float value){
		vision += value;
	}
	
	public void addSpeed(float value){
		speed += value;
	}
	
	public void setHp(int val){
		this.maxHp = val;
		this.hp = val;
	}
	
	protected void setMoeny(int value){
		this.money = value;
	}
	
	@Override
	public int getHp() {
		// TODO Auto-generated method stub
		return hp;
	}
	
	public int getMaxHp(){
		return maxHp;
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
		movePoint = new Point2D.Float(x, y);
	}
	
	public void setPosition(float x, float y){
		this.x = x;
		this.y = y;
		movePoint = new Point2D.Float(x, y);
	}
	
	public void setTypeName(String name){
		type_name = name;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	@Override
	public Float getBounds() {
		// TODO Auto-generated method stub
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target) {
		if(!is_active){
			return false;
		}
		
		return target.intersects(this.getBounds());
	}
	
	public boolean getRandMove(){
		return randMove;
	}
	
	public boolean getDamageable(){
		return damageable;
	}
	
	public boolean isActive(){
		return is_active;
	}
	
	public String getTypeName(){
		return type_name;
	}
	
	public int getMoney(){
		return money;
	}
}

class BossEnemy extends Enemy1{
	BossEnemy(int hp) {
		super(hp);
		setMoeny(300);
		addVision(120);
		addSpeed(-0.3f);
		setTypeName("boss");
		setSize(72, 72);
		if(hp == 0){
			setHp(10);
		}
	}
	
	public void dead(){
		if(DataManagement.getInstance().getGameLevel().getMode() == 0){
			Screen.getInstance().setStoryEnd(true);
			DataManagement.getInstance().getPlayer().dead();
		} else {
			super.dead();
		}
		System.out.println("boss dead");
		
	}
}

class Wall1 extends Colider implements Wall {
	
	private float x, y;
	private int width, height;
//	private int count;
	private int hp = 5, maxHp = 5;
	private boolean outTrigger = false, outTriggerMoment = false; // 물체 내부에서 나간 후에 충돌 처리
	
	Wall1(float x, float y){
		setPosition(x, y);
		setSize(48, 48);
		trigger = true; // 충돌 가능
		
		maxHp += DataManagement.getInstance().getPlusHp();
		hp = maxHp;
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
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
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
	
	public int getMaxHp(){
		return maxHp;
	}
	
	public int getSize(String text){
		if(text.equals("height")){
			return height;
		} else {
			return width;
		}
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}

class Laser1 extends Colider implements Laser{
	private String name;
	private float x = 0, y = 0, width = 0, height = 0;
	private int count = 0, countLimit = 50, deadLimit = 75;
//	private int indexX, indexY;
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
		
		setPosition(linkLar.getIndexX(), linkLar.getIndexY());
		
//		indexX = x;
//		indexY = y;
		
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
	private int targetIndex = 0;
	private int mode_type = 0;
	private boolean levelUp = true, refresh = false, patternChange = false;
	private String patternName = null;
	
	public GameLevel(int type){
		engine = Engine.getInstance();
		dm = DataManagement.getInstance();
		
		mode_type = type;
		
		if(type == 0){
			sequenceData =  dm.getScenario().getSequenceData();
		} else if(type == 1){
			sequenceData =  dm.getScenario().getSequenceData2();
		}
		
		patternName = sequenceData.get("1").get(0);
		
//		patternParser(0);
	}
	
	public void init(){
		nowSequence = 0;
		targetIndex = 1;
		levelUp = true;
		refresh = false;
		patternChange = false;
		patternName = sequenceData.get("1").get(0);
	}
	
	public void levelStart(){
		if(levelUp){
//			nowLevel += 1;
			if(nowSequence != 0) patternChange = true;
			
			nowSequence += 1;
			levelUp = false;
			
			System.out.println("nowSequence : "+nowSequence);
			if(nowSequence > sequenceData.size()){
				if(mode_type == 0){
					Screen.getInstance().setStoryEnd(true);
					dm.getPlayer().dead();
					return;
				} else {
					nowSequence = 1;
				}
			}
		}

		if(refresh){
			refresh = false;
			patternChange = true;
		}
		
		if(patternChange){
			ArrayList<String> tempList = sequenceData.get(""+nowSequence);
			patternChange = false;
			targetIndex += 1;

			if(tempList.size() <= targetIndex){
				targetIndex = -1;
				levelUp = true;
				return;
			}
			
			patternName = tempList.get(targetIndex);
			System.out.println("patternName : " + patternName);
			
			engine.refreshInvoke();
		}
		
		patternParser(mode_type);
	}
	
	private void patternParser(int type){
		Map<String, ArrayList<Point>> patternMap = null;
		int time = engine.getPlayTime();
		int itime = engine.getInvokeTime();
		String timeText = "" + (time - itime);
		
		if(type == 0){
			patternMap = dm.getScenario().getPatternData().get(patternName).getMap();
		} else if(type == 1){
			patternMap = dm.getScenario().getPatternData2().get(patternName).getMap();
		}
		
		if(patternMap.containsKey(timeText)){
			ArrayList<Point> tempList = patternMap.get(timeText);
			
			if(tempList.size() == 0){
				refresh = true;
			}
			
			for(int i = 0; i < tempList.size(); i++){
				Point tempPoint = tempList.get(i);
				if(tempPoint.x == 99 && tempPoint.y == 99){
					new Enemy1(3);
				} else if(tempPoint.x == 99 && tempPoint.y == 100){
					new Enemy1(4);
				} else if(tempPoint.x == 99 && tempPoint.y == 101){
					new Enemy1(5);
				} else if(tempPoint.x == 100 && tempPoint.y == 99){
					new BossEnemy(0);
				}
				else {
					new Laser1(tempPoint.x, tempPoint.y);
				}
				
			}
		}
	}
	
	public int getMode(){
		return mode_type;
	}
}

class JParser {
	
	private Map<String, ArrayList<String>> sequenceData = new HashMap<String, ArrayList<String>>();
	private Map<String, JsonPattern> patternData = new HashMap<String, JsonPattern>();
	
	private Map<String, ArrayList<String>> sequenceData2 = new HashMap<String, ArrayList<String>>();
	private Map<String, JsonPattern> patternData2 = new HashMap<String, JsonPattern>();

	JParser(){
		File abc = new File("resource/last_war.json");
		
		JSONParser jsonParser = new JSONParser();
		try{
			
			File bhc = new File(abc.getAbsolutePath());
			
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(bhc));
			
			Set<String> keys = jsonObject.keySet();
			
			for(String name : keys){
				JSONObject temp = (JSONObject)jsonObject.get(name);
				Set<String> keys2 = temp.keySet();
				
				if(name.equals("story")){
					for(String jso : keys2){
						JSONObject jsonTemp = (JSONObject)temp.get(jso);
						System.out.println(jso);
						System.out.println(temp.get(jso));
						
						if(jso.equals("sequence")){
//							JSONObject jsonTemp2 = (JSONObject)jsonObject.get(jso);
							seqenceToMap(sequenceData, jsonTemp);
							
						} else if(jso.contains("pattern")){
//							JSONObject jsonTemp2 = (JSONObject)jsonObject.get(jso);
							
							patternData.put(jso, new JsonPattern(jsonTemp));
							
						}
					}
				} else if(name.equals("never")){
					for(String jso : keys2){
						JSONObject jsonTemp = (JSONObject)temp.get(jso);
						System.out.println(jso);
						System.out.println(temp.get(jso));
						
						if(jso.equals("sequence")){
							seqenceToMap(sequenceData2, jsonTemp);
						} else if(jso.contains("pattern")){
							patternData2.put(jso, new JsonPattern(jsonTemp));
						}
					}
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
	
	void seqenceToMap(Map<String, ArrayList<String>> map, Object target){
		if(target instanceof JSONObject){
			JSONObject tempObject = (JSONObject)target;
			Set<String> keys = tempObject.keySet();
			
			for(String text : keys){
				map.put(text, seqenceToArray(tempObject.get(text)) );
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
	
	Map<String, ArrayList<String>> getSequenceData2(){
		return sequenceData2;
	}
	
	Map<String, JsonPattern> getPatternData2(){
		return patternData2;
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

class WarpGate extends Colider implements Obstacle{
	private boolean is_open = false;
	private int open_count = 0;
	
	WarpGate(){
		setPosition(560, 346);
		setSize(64, 64);
	}
	
	@Override
	public void dead() {
		
	}

	@Override
	public void work() {
		if(!is_open){
			return;
		} else {
			open_count += 1;
			if(open_count > 40){
				open_count = 0;
				is_open = false;
			}
		}
	}
	
	public void setOpen(){
		is_open = true;
		open_count = 0;
	}
	
	public boolean isOpen(){
		return is_open;
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

	@Override
	public Float getBounds() {
		// TODO Auto-generated method stub
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
}

class AudioManagement{
	Clip clip;
	URL url;
	
	AudioManagement(){
//		url = this.getClass().getClassLoader().getResource("game_music/perfect_crime.wav");
		url = this.getClass().getClassLoader().getResource("game_music/hit_and_run.wav");
			
	}
	
	public void play(){
		playSound(url.getPath());
	}
	
	public void stop(){
		clip.stop();
	}
	
	private void playSound(String file_path){
        try {
            File file = new File(file_path);
            clip = AudioSystem.getClip();
            
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event){
                    //CLOSE, OPEN, START, STOP
                    if (event.getType() == LineEvent.Type.STOP)
                        clip.close();
                }

            });

            clip.open(AudioSystem.getAudioInputStream(file));

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(0f);
            
            clip.loop(clip.LOOP_CONTINUOUSLY);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}