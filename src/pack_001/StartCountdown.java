package pack_001;

import java.util.TimerTask;

final class StartCountdown extends TimerTask
{ // 321 ī��Ʈ �ٿ� Ÿ�̸�
	private int delay;
	
	StartCountdown(int delay)
	{
		this.delay = delay;
	}
	
	@Override
	public void run()
	{
		Engine.getInstance().workCountdown();
		Screen.getInstance().repaint();
		
		if(Engine.getInstance().getCountdownTime() < 3)
		{
			Engine.getInstance().addSchedule(new StartCountdown(delay), delay);
		}
		else
		{
			Engine.getInstance().gameStart();
		}
	}
}