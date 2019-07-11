package pack_001;

import java.util.HashSet;
import java.util.Set;

final class GameLoop implements Runnable{
	private int interval = 0, playTime = 0;
	
	private Screen sc;
	private Engine engine;
	private DataManagement dm;
	private GameLevel gameLevel;
	
	GameLoop(int interval){
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
				
				loopColider();
				dm.getPlayer().work();
				
				engine.setPlayTime(playTime += 1);
				gameLevel.levelStart();
				
				if(playTime % 20 == 0){
					dm.countCoolTime();
					dm.addMoney(1);
					dm.addScore(1);
				}
				
				workMessage();
				
				FPScounter.StopAndPost();
				interval = (1000 / engine.getFps()) - FPScounter.getElapsedTime();
				
				if(interval < 5){
					interval = 20;
				}
				
				Thread.sleep(interval);
				engine.killThread();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setInterval(int val){
		interval = val;
	}
	
	public void workMessage(){
		int _messageTextDuration = Screen.getInstance().getMessageTextDuration();
		if(Screen.getInstance().getMessageTextDuration() != 0){
			Screen.getInstance().addMessageTextDuration(-1);
			if(_messageTextDuration <= 0){
				Screen.getInstance().initMessage();
			}
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
}