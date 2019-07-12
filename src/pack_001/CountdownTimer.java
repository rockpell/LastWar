package pack_001;

import java.util.TimerTask;

final class CountdownTimer extends TimerTask
{ // 321 카운트 다운 타이머
	private int delay;

	CountdownTimer(int delay)
	{
		this.delay = delay;
	}

	@Override
	public void run()
	{
		GameManager.getInstance().workCountdown();
		Screen.getInstance().repaint();

		if (GameManager.getInstance().getCountdownTime() < 3)
		{
			GameManager.getInstance().addSchedule(new CountdownTimer(delay), delay);
		}
		else
		{
			GameManager.getInstance().gameStart();
		}
	}
}