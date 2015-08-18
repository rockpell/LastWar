package pack_001;

import java.util.HashSet;
import java.util.Set;

public class Engine {
	private static Engine instance;
	
	
	public static Engine getInstance(){
		if(instance == null){
			instance = new Engine();
		}
		return instance;
	}
	
	private DataManagement dm;
	private int playTime = 0, invokeTime = 0;
	private boolean stopOn = false;
	Looper game_loop;
	Thread th1;
	
	private Engine(){

	}
	
	public void startLoop(){
		if(game_loop == null){
			game_loop = new Looper("what", 20);
		}
		
		th1 = new Thread(game_loop);
		th1.start();
		
		dm = DataManagement.getInstance();
	}
	
	public void stopLoop(){
		stopOn = true;
	}
	
	public void killThread(){
		if(stopOn){
			th1.interrupt();
//			th1.stop();
			stopOn = false;
		}
		
	}
	
	public void loopColider(){
		if(dm.getColiderSet().size() > 0){
//			System.out.println("coliderSet.size() : " + coliderSet.size());
		}
		Set<Laser1> coliderSet2 = new HashSet<Laser1>(dm.getColiderSet());
		Set<Wall1> wallSet2 = new HashSet<Wall1>(dm.getWallSet());
		Set<Enemy1> enemySet2 = new HashSet<Enemy1>(dm.getEnemySet());
		Player player = dm.getPlayer();
		
		for(Laser1 c : coliderSet2){
			
			for(Wall1 w : wallSet2){
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
		
		for(Wall1 w : wallSet2){
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
		
		for(Enemy1 en : enemySet2){
			
			
			for(Wall1 wa : wallSet2){
				if(wa.getTrigger()){
					if(en.collision(wa.getBounds())){
						en.checkMoveable(wa.getBounds());
					}
				}
			}
			
			for(Laser1 la : coliderSet2){
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
	
	public int getInvokeTime(){
		return invokeTime;
	}
}

class Looper implements Runnable{
	private String name;
	private int interval = 0, playTime = 0;
	
	private Screen sc;
	private Engine engine;
	private DataManagement dm;
	private GameLevel gameLevel;
	
	Looper(String name, int interval){
		this.name = name;
		this.interval = interval;
		sc = Screen.getInstance();
		engine = Engine.getInstance();
		dm = DataManagement.getInstance();
		
		playTime = engine.getPlayTime();
		
		gameLevel = new GameLevel();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				sc.repaint();
				
				engine.loopColider();
				dm.getPlayer().work();
				
				engine.setPlayTime(playTime += 1);
				gameLevel.levelStart();
				
				if(playTime % 20 == 0){
					dm.countCoolTime();
				}
				
				Thread.sleep(interval);
				engine.killThread();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}