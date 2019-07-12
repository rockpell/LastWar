package pack_001;

import java.util.Timer;
import java.util.TimerTask;

final public class GameManager
{
	private static GameManager instance;

	public static GameManager getInstance()
	{
		if (instance == null)
		{
			instance = new GameManager();
		}
		return instance;
	}

	private int playTime = 0, levelStartTime = 0;
	private int countdownTime = 0;
	private int fps = 51;
	
	private boolean isStop = false;

	private GameLoop gameLoop;
	private Thread th1;
	private Timer jobScheduler;
	
	private GameState nowState;
	
	private GameManager()
	{
		nowState = new MenuState();
	}
	
	public boolean ChageState(GameState state)
	{
		nowState = state;
		System.out.println("state: " + state.toString());
		return true;
	}
	
	public void newLoop()
	{
		gameLoop = null;
		gameLoop = new GameLoop(20);
	}

	public void initLoop()
	{
		gameLoop = null;
	}

	public void startLoop()
	{
		jobScheduler = new Timer();
		jobScheduler.schedule(new StartCountdown(1000), 1000);
	}

	public void gameStart()
	{
		nowState.exit(this);
		nowStartLoop();
		DataManagement.getInstance().getAudio().play();
		setTempTime(0);
		stopSchedule();
	}

	public void nowStartLoop()
	{
		if (gameLoop == null)
		{
			gameLoop = new GameLoop(20);
		}

		th1 = new Thread(gameLoop);
		th1.start();
	}

	public void addSchedule(TimerTask task, long time)
	{
		jobScheduler.schedule(task, time);
	}

	public void stopSchedule()
	{
		jobScheduler.cancel();
	}

	public void stopLoop()
	{
		isStop = true;
	}

	public void loadThread() // 이미지 로드용 스레드 실행
	{
		Thread _lth1 = new Thread(new Loader());
		_lth1.start();
	}

	public void killThread()
	{
		if (isStop)
		{
			th1.interrupt();
			isStop = false;
			Screen.getInstance().repaint();
		}
	}

	public int getPlayTime()
	{
		return playTime;
	}

	public void setPlayTime(int value)
	{
		playTime = value;
	}

	public void refreshLevelStartTime()
	{
		levelStartTime = playTime;
	}

	public int getLevelStartTime()
	{
		return levelStartTime;
	}

	public void workCountdown()
	{
		countdownTime += 1;
	}

	public int getCountdownTime()
	{
		return countdownTime;
	}

	public void setTempTime(int value)
	{
		countdownTime = value;
	}

	public void setFps(int value)
	{
		fps = value;
	}

	public int getFps()
	{
		return fps;
	}
	
	public GameState getNowState()
	{
		return nowState;
	}
}