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
		return true;
	}

	public void starCountdown()
	{
		jobScheduler = new Timer();
		jobScheduler.schedule(new CountdownTimer(1000), 1000);
	}

	public void gameStart()
	{
		nowState.exit(this); // CountDownState를 벗어나기 위한 함수
		startGameLoop();
		DataManagement.getInstance().getAudio().play();
		countdownTime = 0;
		stopSchedule();
	}

	private void startGameLoop()
	{
		gameLoop = new GameLoop(20);

		th1 = new Thread(gameLoop);
		th1.start();
	}

	public void stopGameLoop()
	{
		isStop = true;
	}

	public void addSchedule(TimerTask task, long time)
	{
		jobScheduler.schedule(task, time);
	}

	private void stopSchedule()
	{
		jobScheduler.cancel();
	}

	public void loadThread() // 이미지 로드용 스레드 실행
	{
		Thread _lth1 = new Thread(new Loader());
		_lth1.start();
	}

	public void killThread()
	{
		th1.interrupt();
		isStop = false;
		Screen.getInstance().repaint();
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

	public boolean getIsStop()
	{
		return isStop;
	}
}