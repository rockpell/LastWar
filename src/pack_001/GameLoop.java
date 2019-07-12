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
		while (!Thread.currentThread().isInterrupted())
		{
			try
			{
				FPScounter.StartCounter();

				sc.repaint();
				
				loopColider();
				dm.getWarpGate().work();
				dm.getPlayer().work();

				gameManager.setPlayTime(playTime += 1);
				gameLevel.levelStart();

				if (playTime % 20 == 0)
				{
					dm.countCoolTime();
					dm.addMoney(1);
					dm.addScore(1);
				}

				workAlarmText();
				workSkillMessage();

				FPScounter.StopAndPost();
				interval = (1000 / gameManager.getFps()) - FPScounter.getElapsedTime();

				if (interval < 5)
				{
					interval = 20;
				}

				Thread.sleep(interval);
				if (GameManager.getInstance().getIsStop())
				{
					gameManager.killThread();
				}
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void workSkillMessage()
	{
		int _messageTextDuration = Screen.getInstance().getSkillMessageTextDuration();
		if (Screen.getInstance().getSkillMessageTextDuration() != 0)
		{
			Screen.getInstance().addSkillMessageTextDuration(-1);
			if (_messageTextDuration <= 0)
			{
				Screen.getInstance().initSkillMessage();
			}
		}
	}

	private void workAlarmText()
	{
		Set<AlarmText> _alarmTextSet = new HashSet<AlarmText>(dm.getAlarmList());
		for (AlarmText at : _alarmTextSet)
		{
			at.work();
		}
	}

	private void loopColider()
	{
		Set<Laser> _coliderSet = new HashSet<Laser>(dm.getColiderSet());
		Set<Wall> _wallSet = new HashSet<Wall>(dm.getWallSet());
		Set<Enemy> _enemySet = new HashSet<Enemy>(dm.getEnemySet());
		Player _player = dm.getPlayer();
		boolean _colideCheck = false;

		for (Laser laser : _coliderSet)
		{

			for (Wall wall : _wallSet)
			{
				if (wall.collision(laser.getBounds()))
				{
					if (!laser.getWallColide())
					{ // 레이저는 벽과 한 번만 충돌 가능
						// c.setWallColide(w.getBounds()); // laser와 wall 충돌
						dm.findColiderLaser(laser).setWallColide(wall);
					}
				}
			}
			if (laser.getTrigger())
			{
				if (_player.collision(laser.getBounds()))
				{
					_player.damaged();
				}
			}

			laser.work();
		}

		for (Wall wall : _wallSet)
		{
			if (wall.getTrigger())
			{
				if (_player.collision(wall.getBounds()))
				{
					if (wall.getOutTrigger())
						wall.setOutTriggerMoment(true);

					if (wall.getOutTriggerMoment())
						_player.checkMoveable(wall.getBounds());

					_colideCheck = true;
				}
				else
				{
					wall.setOutTrigger(true);
				}
			}
			wall.work();
		}

		if (!_colideCheck)
		{
			_player.setOutTrigger(false);
		}

		for (Enemy enemy : _enemySet)
		{

			for (Wall wall : _wallSet)
			{
				if (wall.getTrigger())
				{
					if (enemy.collision(wall.getBounds()))
					{
						enemy.checkMoveable(wall.getBounds());
					}
				}
			}

			for (Laser la : _coliderSet)
			{
				if (la.getTrigger())
				{
					if (enemy.getDamageable())
					{
						if (enemy.collision(la.getBounds()))
						{
							enemy.damaged();
						}
					}
				}

			}

			if (enemy.collision(_player.getBounds()))
			{
				enemy.checkMoveable(_player.getBounds());
				_player.checkMoveable(enemy.getBounds());
				_player.damaged();
			}

			enemy.work();
		}

		if (_player.collision(dm.getWarpGate().getBounds()))
		{
			_player.setWallAble(false);
		}
		else
		{
			_player.setWallAble(true);
		}
	}
}