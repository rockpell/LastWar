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
	
	private Engine(){

	}
	
	public void startLoop(){
		Thread th1 = new Thread(new Looper("what", 20));
		th1.start();
		dm = DataManagement.getInstance();
	}
	
	
	public void loopColider(){
		if(dm.getColiderSet().size() > 0){
//			System.out.println("coliderSet.size() : " + coliderSet.size());
		}
		Set<Laser1> coliderSet2 = new HashSet<Laser1>(dm.getColiderSet());
		Set<Wall1> wallSet2 = new HashSet<Wall1>(dm.getWallSet());
		
		for(Laser1 c : coliderSet2){
			
			for(Wall1 w : wallSet2){
				if(w.collision(c.getBounds())){
//					System.out.println(w.getBounds().x + "    :    " + w.getBounds().y);
					if(!c.getWallColide()){ // 레이저는 벽과 한 번만 충돌 가능
//						c.setWallColide(w.getBounds()); // laser와 wall 충돌
						dm.findColiderLaser(c).setWallColide(w.getBounds());
					}
				}
			}
			
			if(c.getTrigger()){
				if(dm.getPlayer().collision(c.getBounds())){
					System.out.println("colide");
//					System.out.println(c.getBounds());
//					System.out.println(dm.getPlayer().getBounds());
				}
			}
			
			c.count();
		}
		
		for(Wall1 w : wallSet2){
			if(w.getTrigger()){
				if(dm.getPlayer().collision(w.getBounds())){
//					System.out.println("colide wall");
					if(w.getOutTrigger())
						w.setOutTriggerMoment(true);
					if(w.getOutTriggerMoment())
						dm.getPlayer().checkMoveable(w.getBounds());
				} else {
					w.setOutTrigger(true);
				}
			}
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
//				System.out.println("running");
				engine.loopColider();
				dm.getPlayer().work();
				Thread.sleep(interval);
				engine.setPlayTime(playTime += 1);
				gameLevel.levelStart();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}