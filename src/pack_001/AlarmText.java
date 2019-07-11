package pack_001;

final class AlarmText extends UI
{
	private String text;
	private int time;
	private float fx, fy;

	AlarmText(float x, float y)
	{
		this.fx = x;
		this.fy = y;
	}

	public void setText(String text, int value)
	{
		this.text = String.valueOf(value) + text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void work()
	{
		time += 1;
		fy -= 0.3f;
		if (time > 100)
		{
			DataManagement.getInstance().removeAlarm(this);
		}
	}

	public String getText()
	{
		return text;
	}

	public float getFX()
	{
		return fx;
	}

	public float getFY()
	{
		return fy;
	}

	public int getTime()
	{
		return time;
	}
}