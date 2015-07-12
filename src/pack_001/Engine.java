package pack_001;

import java.util.HashSet;
import java.util.Set;

public class Engine {
	private static Engine instance;
	private final Set<Laser1> coliderSet = new HashSet<Laser1>();
	
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
	
	public synchronized boolean addColider(Laser1 target){
		return coliderSet.add(target);
	}
	
	public synchronized boolean removeColider(Laser1 target){
		return coliderSet.remove(target);
	}
	
	public Set<Laser1> getColiderSet(){
		return coliderSet;
	}
	
	public void loopColider(){
		if(coliderSet.size() > 0){
//			System.out.println("coliderSet.size() : " + coliderSet.size());
		}
		Set<Laser1> coliderSet2 = new HashSet<Laser1>(coliderSet);
		
		for(Laser1 c : coliderSet2){
			if(c.getTrigger()){
				if(dm.getPlayer().collision(c.getBounds())){
					System.out.println("colide");
				}
			}
				
			c.count();
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