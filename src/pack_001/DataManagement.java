package pack_001;

import java.awt.Image;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;

public class DataManagement {
	private static DataManagement instance = DataManagement.getInstance();
	public static DataManagement getInstance(){
		if(instance == null){
			instance = new DataManagement();
		}
		
		return instance;
	}
	
	private Set<LaserArrow> arrowSet = new HashSet<LaserArrow>();
	private Set<Wall> wallSet = new HashSet<Wall>();
	private Set<Laser> coliderSet = new HashSet<Laser>();
	private Set<Enemy> enemySet = new HashSet<Enemy>();
	private Set<AlarmText> alarm_list = new HashSet<AlarmText>();
	private JParser gameScenario;
	private Player player;
	private WarpGate warp_gate;
	private AudioManagement audioManagement;
	private GameLevel gameLevel;
	private Skill[] skill = new Skill[5];
	
	public Image mshi, hp_potion, hp_plus, wall_hp, wall_time;
	public Image arrow_right, arrow_left, arrow_up, arrow_down;
	public Image arrow_right_red, arrow_left_red, arrow_up_red, arrow_down_red;
	public Image excavator_001, excavator_002, brick_black;
	public Image closed_door, open_door;
	public Image t_skill, t_02, t_03, t_05, t_06;
	
	public final int screenWidth = 1200, screenHeight = 800;
	public final int rowNumber = 11, colNumber = 22;
	public final int rowStartX1 = 20, rowStartX2 = screenWidth - 50, rowStartY = 110, colStartX = 60, colStartY1 = 50, colStartY2 = screenHeight - 150;
	
	private int coolTime = 15, coolTimeLeft = 0;
	private int money = 0, score = 0;
	private int[] cost = new int[5];
	private int wall_plus_hp = 0;
	
	private boolean isGameStart = false, gameEnd = false;
	
	private DataManagement(){
		initData();
		skill[0] = new WallSkill(100, 725, 48, 48);
		skill[1] = new HealSkill(160, 725, 48, 48);
		skill[2] = new HpSkill(220, 725, 48, 48);
		skill[3] = new WallHpSkill(280, 725, 48, 48);
		skill[4] = new WallCoolSkill(340, 725, 48, 48);
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
		wallSet.add(new Wall(x, y));
		return false;
	}
	
	public synchronized boolean removeWall(Wall target){
		return wallSet.remove(target);
	}
	
	public Set<LaserArrow> getArrowSet(){
		return arrowSet;
	}
	
	public Set<Wall> getWallSet(){
		return wallSet;
	}
	
	public JParser getScenario(){
		return gameScenario;
	}
	
	public synchronized boolean addColider(Laser target){
		return coliderSet.add(target);
	}
	
	public synchronized boolean removeColider(Laser target){
		return coliderSet.remove(target);
	}
	
	public Set<Laser> getColiderSet(){
		return coliderSet;
	}
	
	public synchronized boolean addEnemy(Enemy target){
		return enemySet.add(target);
	}
	
	public synchronized boolean removeEnemy(Enemy target){
		return enemySet.remove(target);
	}
	
	public Set<Enemy> getEnemySet(){
		return enemySet;
	}
	
	public Laser findColiderLaser(Laser target){
		for(Laser la : coliderSet){
			if(la.equals(target)){
				return la;
			}
		}
		return null;
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
		
		isGameStart = false;
		gameEnd = false;
		
		money = 0;
		score = 0;
		
		Engine.getInstance().setPlayTime(0);
		Engine.getInstance().refreshLevelStartTime();
		
		initCost();
	}
	
	public void loadImage() {
		mshi = new ImageIcon("resource/people_001.png").getImage();
		arrow_right = new ImageIcon("resource/arrow_right.png").getImage();
		arrow_left = new ImageIcon("resource/arrow_left.png").getImage();
		arrow_up = new ImageIcon("resource/arrow_up.png").getImage();
		arrow_down = new ImageIcon("resource/arrow_down.png").getImage();
		arrow_right_red = new ImageIcon("resource/arrow_right_red.png").getImage();
		arrow_left_red = new ImageIcon("resource/arrow_left_red.png").getImage();
		arrow_up_red = new ImageIcon("resource/arrow_up_red.png").getImage();
		arrow_down_red = new ImageIcon("resource/arrow_down_red.png").getImage();
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
	
	public void tloadImage(){
		mshi = new ImageIcon("resource/people.png").getImage();
		brick_black = new ImageIcon("resource/brick_black.png").getImage();
		excavator_001 = new ImageIcon("resource/excavator_001.png").getImage();
		t_skill = new ImageIcon("resource/t_skill.png").getImage();
		t_02 = new ImageIcon("resource/t_02.png").getImage();
		t_03 = new ImageIcon("resource/t_03.png").getImage();
		t_05 = new ImageIcon("resource/t_05.png").getImage();
		t_06 = new ImageIcon("resource/t_06.png").getImage();
	}
	
	public AudioManagement getAudio(){
		if(audioManagement == null){
			audioManagement = new AudioManagement();
		}
		
		return audioManagement;
	}
	
	public boolean getIsGameStart(){
		return isGameStart;
	}
	
	public void setIsGameStart(boolean val){
		isGameStart = val;
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
		cost[2] = 180;
		cost[3] = 100;
		cost[4] = 200;
	}
	
	public void upCost(int value){
		switch(value){
		case 1:
			if(cost[value] < 90){ // heal
				cost[value] += 30;
			} else {
				cost[value] += 50;
			}
			break;
		case 2:
			if(cost[value] < 200){ // hp max up
				cost[value] += 40;
			} else {
				cost[value] += 80;
			}
			break;
		case 3:
			if(cost[value] < 210){ // wall hp max up
				cost[value] += 40;
			} else {
				cost[value] += 80;
			}
			break;
		case 4:
			if(cost[value] < 210){ // wall cooldown decrease
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
	
	public int getScore(){
		return score;
	}
	
	public void addScore(int value){
		score += value;
	}
}