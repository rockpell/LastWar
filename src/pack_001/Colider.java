package pack_001;

import java.awt.geom.Rectangle2D;

abstract class Colider
{
	float x, y; // ������Ʈ ȭ�� ��ġ
	int cx = 0, cy = 0; // �浹 �ڽ��� ������Ʈ�� ���� ��� ��ġ
	int width, height;
	float cwidth = 0, cheight = 0; // �浹 �ڽ� ũ��
	protected boolean trigger = false; // �浹 ���� ����

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