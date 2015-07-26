package pack_001;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;


public interface Obstacle {
	
	public void dead();
	public void count();
	public void setSize(int width, int height);
	public void setPosition(int x, int y);
}

interface Laser extends Obstacle {
	public void setImage(Image image);
}

interface Explosion extends Obstacle {
	
}

interface Wall extends Obstacle {
	
}

abstract class Colider {
	float x, y; // ������Ʈ ȭ�� ��ġ
	int cx = 0, cy = 0; // �浹 �ڽ��� ������Ʈ�� ���� ��� ��ġ
	float cwidth = 0, cheight = 0; // �浹 �ڽ� ũ��
	protected boolean trigger = false; // �浹 ���� ����
	
	public Colider(){
		
	}
	
	public void setBox(int x, int y, float width, float height){
		this.cx = x;
		this.cy = y;
		this.cwidth = width;
		this.cheight = height;
	}
	
	public boolean getTrigger(){
		return trigger;
	}
	
	abstract public Rectangle2D.Float getBounds();
	abstract public boolean collision(Rectangle2D.Float target);
	
}