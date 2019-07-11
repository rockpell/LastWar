package pack_001;

import java.util.HashSet;
import java.util.Set;

final class GameLoop implements Runnable
{
	private int interval = 0, playTime = 0;
	
	private Screen sc;
	private GameManager gameManager;
	private DataManagement dm;
	private GameLevel gameLevel;
	
	GameLoop(int interval)
	{
		this.interval = interval;
		sc = Screen.getInstance();
		gameManager = GameManager.getInstance();
		dm = DataManagement.getInstance();
		
		playTime = gameManager.getPlayTime();
		
		gameLevel = dm.getGameLevel();
	}
	
	@Override
	public void run() 
	{
		while(!Thread.currentThread().isInterrupted()){
			try 
			{
				FPScounter.StartCounter();
				
				sc.repaint();
				
				loopColider();
				dm.getWarpGate().work();
				dm.getPlayer().work();
				
				gameManager.setPlayTime(playTime += 1);
				gameLevel.levelStart();
				
				if(playTime % 20 == 0){
					dm.countCoolTime();
					dm.addMoney(1);
					dm.addScore(1);
				}
				
				workMessage();
				
				FPScounter.StopAndPost();
				interval = (1000 / gameManager.getFps()) - FPScounter.getElapsedTime();
				
				if(interval < 5)
				{
					interval = 20;
				}
				
				Thread.sleep(interval);
				gameManager.killThread();
				
			} 
			catch (InterruptedException e) 
			{
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
		if(Screen.getInstance().getMessageTextDuration() != 0)
		{
			Screen.getInstance().addMessageTextDuration(-1);
			if(_messageTextDuration <= 0)
			{
				Screen.getInstance().initMessage();
			}
		}
	}
	
	public void loopColider(){
		Set<Laser> _coliderSet = new HashSet<Laser>(dm.getColiderSet());
		Set<Wall> _wallSet = new HashSet<Wall>(dm.getWallSet());
		Set<Enemy> _enemySet = new HashSet<Enemy>(dm.getEnemySet());
		Player _player = dm.getPlayer();
		boolean _colideCheck = false;
		
		Set<AlarmText> atl = new HashSet<AlarmText>(dm.getAlarmList());
		for(AlarmText at : atl)
		{
			at.work();
		}
		
		for(Laser c : _coliderSet)
		{
			
			for(Wall w : _wallSet)
			{
				if(w.collision(c.getBounds()))
				{
					if(!c.getWallColide())
					{ // 레이저는 벽과 한 번만 충돌 가능
//						c.setWallColide(w.getBounds()); // laser와 wall 충돌
						dm.findColiderLaser(c).setWallColide(w);
					}
				}
			}
			if(c.getTrigger())
			{
				if(_player.collision(c.getBounds()))
				{
					_player.damaged();
				}
			}
			
			c.work();
		}
		
		for(Wall w : _wallSet)
		{
			if(w.getTrigger())
			{
				if(_player.collision(w.getBounds()))
				{
					if(w.getOutTrigger())
						w.setOutTriggerMoment(true);
//					if(player.getOutTrigger())
//						w.setOutTriggerMoment(true);
						
					if(w.getOutTriggerMoment())
						_player.checkMoveable(w.getBounds());
					
					_colideCheck = true;
				} 
				else 
				{
					w.setOutTrigger(true);
				}
			}
			w.work();
		}
		
		if(!_colideCheck)
		{
			_player.setOutTrigger(false);
		}
		
		for(Enemy en : _enemySet)
		{
			
			for(Wall wa : _wallSet)
			{
				if(wa.getTrigger())
				{
					if(en.collision(wa.getBounds()))
					{
						en.checkMoveable(wa.getBounds());
					}
				}
			}
			
			for(Laser la : _coliderSet)
			{
				if(la.getTrigger())
				{
					if(en.getDamageable())
					{
						if(en.collision(la.getBounds()))
						{
							en.damaged(); 
						}
					}
				}
				
			}
			
			if(en.collision(_player.getBounds()))
			{
				en.checkMoveable(_player.getBounds());
				_player.checkMoveable(en.getBounds());
				_player.damaged();
			}
			
			en.work();
		}
		
		if(_player.collision(dm.getWarpGate().getBounds()) )
		{
			_player.setWallAble(false);
		}
		else 
		{
			_player.setWallAble(true);
		}
	}
}