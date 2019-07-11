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
	private boolean isCountdown = false;

	public boolean isStroyStart = false, isStroyEnd = false, isTutorialStart = false;
	public boolean isPause = true, isBeforeStart = false;

	private GameLoop gameLoop;
	private Thread th1;
	private Timer jobScheduler;

	private GameManager()
	{

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
		setIsCountdown(true);
	}

	public void gameStart()
	{
		nowStartLoop();
		DataManagement.getInstance().getAudio().play();
		setIsCountdown(false);
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

	public void loadThread()
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

	public void setIsCountdown(boolean value)
	{
		isCountdown = value;
	}

	public boolean getIsCountDown()
	{
		return isCountdown;
	}

	public void pauseScreenOn()
	{
		isPause = !isPause;
	}

	public void beforeStartOn()
	{
		isBeforeStart = !isBeforeStart;
	}

	public void setIsBeforeStart(boolean value) {
		isBeforeStart = value;
	}
	
	public boolean getIsBeforeStart()
	{
		return isBeforeStart;
	}

	public void setIsPause(boolean value) {
		isPause = value;
	}
	
	public boolean getIsPause()
	{
		return isPause;
	}

	public void setIsStoryEnd(boolean value)
	{
		isStroyEnd = value;
	}

	public boolean getIsStroyEnd()
	{
		return isStroyEnd;
	}
	
	public void setIsStroyEnd(boolean value) {
		isStroyEnd = value;
	}

	public void setIsTutorialStart(boolean value) {
		isTutorialStart = value;
	}
	
	public boolean getIsTutorialStart()
	{
		return isTutorialStart;
	}

	public void setIsStroyStart(boolean value) {
		isStroyStart = value;
	}
	
	public boolean getIsStroyStart()
	{
		return isStroyStart;
	}
}