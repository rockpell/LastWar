package pack_001;

import java.awt.geom.Rectangle2D;

abstract class Colider
{
	float x, y; // 오브젝트 화면 위치
	int cx = 0, cy = 0; // 충돌 박스의 오브젝트에 대한 상대 위치
	int width, height;
	float cwidth = 0, cheight = 0; // 충돌 박스 크기
	protected boolean trigger = false; // 충돌 가능 여부

	public Colider()
	{

	}

	public void setBox(int x, int y, float width, float height)
	{
		this.cx = x;
		this.cy = y;
		this.cwidth = width;
		this.cheight = height;
	}

	public boolean getTrigger()
	{
		return trigger;
	}

	abstract public Rectangle2D.Float getBounds();

	abstract public boolean collision(Rectangle2D.Float target);

}