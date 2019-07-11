package pack_001;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Engine {
	private static Engine instance;
	
	public static Engine getInstance(){
		if(instance == null){
			instance = new Engine();
		}
		return instance;
	}
	
	private int playTime = 0, invokeTime = 0;
	private int temp_time = 0;
	private int fps = 51;
	private int message_time = 0, message_time_max = 50;
	private String message_text, message_text2;
	private boolean isStop = false;
	private boolean temp_stoper = false;
	
	private GameLoop gameLoop;
	private Thread th1;
	private Timer jobScheduler;
	private DataManagement dm;
	
	private Engine(){

	}
	
	public void newLoop(){
		gameLoop = null;
		gameLoop = new GameLoop(20);
	}
	
	public void initLoop(){
		gameLoop = null;
	}
	
	public void startLoop(){
		jobScheduler = new Timer();
		jobScheduler.schedule(new TempStoper(), 3000);
		jobScheduler.schedule(new TempStoper2(), 1000);
		setTempStoper(true);
	}
	
	public void nowStartLoop(){
		if(gameLoop == null){
			gameLoop = new GameLoop(20);
			dm = DataManagement.getInstance();
		}
		
		th1 = new Thread(gameLoop);
		th1.start();
	}
	
	public void addSchedule(TimerTask task, long time){
		jobScheduler.schedule(task, time);
	}
	
	public void stopSchedule(){
		jobScheduler.cancel();
	}
	
	public void workTemp(){
		temp_time += 1;
	}
	
	public void stopLoop(){
		isStop = true;
	}
	
	public void loadThread(){
		Thread lth1 = new Thread(new Loader());
		lth1.start();
	}
	
	public void killThread(){
		if(isStop){
			th1.interrupt();
			isStop = false;
			Screen.getInstance().repaint();
		}
		
	}
	
	public void loopColider(){
		Set<Laser> coliderSet2 = new HashSet<Laser>(dm.getColiderSet());
		Set<Wall> wallSet2 = new HashSet<Wall>(dm.getWallSet());
		Set<Enemy> enemySet2 = new HashSet<Enemy>(dm.getEnemySet());
		Player player = dm.getPlayer();
		
		Set<AlarmText> atl = new HashSet<AlarmText>(dm.getAlarmList());
		for(AlarmText at : atl){
			at.work();
		}
		
		for(Laser c : coliderSet2){
			
			for(Wall w : wallSet2){
				if(w.collision(c.getBounds())){
//					System.out.println(w.getBounds().x + "    :    " + w.getBounds().y);
					if(!c.getWallColide()){ // 레이저는 벽과 한 번만 충돌 가능
//						c.setWallColide(w.getBounds()); // laser와 wall 충돌
						dm.findColiderLaser(c).setWallColide(w);
					}
				}
			}
			
			if(c.getTrigger()){
				if(player.collision(c.getBounds())){
					player.damaged();
				}
			}
			
			c.work();
		}
		
		boolean colideCheck = false;
		
		for(Wall w : wallSet2){
			if(w.getTrigger()){
				if(player.collision(w.getBounds())){
//					System.out.println("colide wall");
					if(w.getOutTrigger())
						w.setOutTriggerMoment(true);
//					if(player.getOutTrigger())
//						w.setOutTriggerMoment(true);
						
					if(w.getOutTriggerMoment())
						player.checkMoveable(w.getBounds());
					
					colideCheck = true;
				} else {
					w.setOutTrigger(true);
				}
			}
			w.work();
		}
		
		if(!colideCheck){
			player.setOutTrigger(false);
		}
		
		for(Enemy en : enemySet2){
			
			for(Wall wa : wallSet2){
				if(wa.getTrigger()){
					if(en.collision(wa.getBounds())){
						en.checkMoveable(wa.getBounds());
					}
				}
			}
			
			for(Laser la : coliderSet2){
				if(la.getTrigger()){
					if(en.getDamageable()){
						if(en.collision(la.getBounds())){
							en.damaged(); 
						}
					}
				}
				
			}
			
			if(en.collision(player.getBounds())){
				en.checkMoveable(player.getBounds());
				player.checkMoveable(en.getBounds());
				player.damaged();
			}
			
			en.work();
		}
		
		if(player.collision(dm.getWarpGate().getBounds()) ){
			player.setWallAble(false);
		} else {
			player.setWallAble(true);
		}
		
		dm.getWarpGate().work();
	}
	
	public void workMessage(){
		if(message_time != 0){
			message_time -= 1;
			if(message_time <= 0){
				initMessage();
			}
		}
	}
	
	public void initMessage(){
		message_time = 0;
	}
	
	public void settingMessage(){
		message_time = message_time_max;
	}
	
	public void settingMessage(String text){
		switch(text){
		case "cool":
			message_text = "Cooldown Time";
			break;
		case "point":
			message_text = "Not enough point";
			break;
		case "heal":
			message_text = "Full hp";
			break;
		case "heal_ok":
			message_text = "Hp heal";
			break;
		case "hp_ok":
			message_text = "Hp Max Up";
			break;
		case "wall_hp_ok":
			message_text = "Wall Max Hp Up";
			break;
		case "wall_cool_ok":
			message_text = "Wall Cooldown reduce";
			break;
		}
		
		message_time = message_time_max;
	}
	
	public void settingMessage2(int value){
		message_text2 = "Cost " + String.valueOf(value) + " Up";
	}
	
	public boolean isMessage(){
		if(message_time > 0){
			return true;
		} else {
			initMessage2();
			return false;
		}
	}
	
	public String getMessage(){
		return message_text;
	}
	
	public String getMessage2(){
		return message_text2;
	}
	
	public void initMessage2(){
		message_text2 = null;
	}
	
	public int getPlayTime(){
		return playTime;
	}
	
	public void setPlayTime(int number){
		playTime = number;
	}
	
	public void refreshInvoke(){
		invokeTime = playTime;
	}
	
	public void initInvokeTime(){
		invokeTime = 0;
	}
	
	public int getInvokeTime(){
		return invokeTime;
	}
	
	public int getTempTime(){
		return temp_time;
	}
	
	public void setTempTime(int val){
		temp_time = val;
	}
	
	public void setFps(int val){
		fps = val;
	}
	
	public int getFps(){
		return fps;
	}
	
	public void setTempStoper(boolean val){
		temp_stoper = val;
	}
	
	public boolean getTempStoper(){
		return temp_stoper;
	}
}

class TempStoper extends TimerTask {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Engine.getInstance().nowStartLoop();
		DataManagement.getInstance().getAudio().play();
		
		Engine.getInstance().setTempStoper(false);
		Engine.getInstance().setTempTime(0);
		Engine.getInstance().stopSchedule();
		System.out.println("TempStoper1");
	}
}

class TempStoper2 extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Engine.getInstance().workTemp();
		Screen.getInstance().repaint();
		
		if(Engine.getInstance().getTempStoper()){
			Engine.getInstance().addSchedule(new TempStoper2(), 1000);
		}
		System.out.println("TempStoper2");
	}
}