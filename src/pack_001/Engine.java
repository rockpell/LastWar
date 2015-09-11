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
	
	private DataManagement dm;
	private int playTime = 0, invokeTime = 0;
	private boolean stopOn = false;
	private Looper game_loop;
	private Thread th1;
	
	private Engine(){

	}
	
	public void newLoop(){
		game_loop = null;
		game_loop = new Looper("restart", 20);
	}
	
	public void initLoop(){
		game_loop = null;
	}
	
	public void startLoop(){
//		if(game_loop == null){
//			game_loop = new Looper("what", 20);
//			dm = DataManagement.getInstance();
//		}
//		
//		th1 = new Thread(game_loop);
//		th1.start();
		Timer jobScheduler = new Timer();
		jobScheduler.schedule(new TempStoper(), 3000);
	}
	
	public void nowStartLoop(){
		if(game_loop == null){
			game_loop = new Looper("what", 20);
			dm = DataManagement.getInstance();
		}
		
		th1 = new Thread(game_loop);
		th1.start();
	}
	
	public void stopLoop(){
		stopOn = true;
	}
	
	public void loadThread(){
		Thread lth1 = new Thread(new Loader());
		lth1.start();
	}
	
	public void killThread(){
		if(stopOn){
			th1.interrupt();
//			th1.stop();
			stopOn = false;
			Screen.getInstance().repaint();
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
					if(!c.getWallColide()){ // �������� ���� �� ���� �浹 ����
//						c.setWallColide(w.getBounds()); // laser�� wall �浹
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
		
		if(player.collision(dm.getWarpGate().getBounds()) ){
			player.setWallAble(false);
		} else {
			player.setWallAble(true);
		}
		
		dm.getWarpGate().work();
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
		
		gameLevel = dm.getGameLevel();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				FPScounter.StartCounter();
				
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
				
				FPScounter.StopAndPost();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Loader implements Runnable {
	
	Loader(){
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DataManagement.getInstance().loadImage();
		Screen.getInstance().loadImage();
		Engine.getInstance().startLoop();
//		DataManagement.getInstance().getAudio().play();
		DataManagement.getInstance().setGameStart(true);
		Screen.getInstance().stopScreenOn();
		
		Screen.getInstance().repaint();
	}
}

final class FPScounter {  
    private static int startTime;  
    private static int endTime;  
    private static int frameTimes = 0;  
    private static short frames = 0;  
  
//    /** Start counting the fps**/  
    public final static void StartCounter()  {  
        //get the current time  
        startTime = (int) System.currentTimeMillis();  
    }  
  
//    /**stop counting the fps and display it at the console*/  
    public final static void StopAndPost(){  
        //get the current time  
        endTime = (int) System.currentTimeMillis();  
        //the difference between start and end times  
        frameTimes = frameTimes + endTime - startTime;  
        //count one frame  
        ++frames;  
        //if the difference is greater than 1 second (or 1000ms) post the results  
        if(frameTimes >= 1000){  
            //post results at the console  
            System.out.println("FPS : " + Long.toString(frames));  
            //reset time differences and number of counted frames  
            frames = 0;  
            frameTimes = 0;  
        }  
    }  
}

class TempStoper extends TimerTask {
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Engine.getInstance().nowStartLoop();
		DataManagement.getInstance().getAudio().play();
//		DataManagement.getInstance().setGameStart(true);
//		Screen.getInstance().stopScreenOn();
	}
	
}