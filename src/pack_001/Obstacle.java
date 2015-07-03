package pack_001;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;


public interface Obstacle {
	
	public void dead();
	public void setSize(int width, int height);
	public void setPosition(int width, int height);
}

interface Laser extends Obstacle {
	public void setImage(Image image);
}

interface Explosion extends Obstacle {
	
}

abstract class Colider {
	float x, y; // ������Ʈ ȭ�� ��ġ
	int cx = 0, cy = 0; // �浹 �ڽ��� ������Ʈ�� ���� ��� ��ġ
	int cwidth = 0, cheight = 0; // �浹 �ڽ� ũ��
	
	public Colider(){
		
	}
	
	public void setBox(int x, int y, int width, int height){
		this.cx = x;
		this.cy = y;
		this.cwidth = width;
		this.cheight = height;
	}
	
	abstract public Rectangle2D.Float getBounds();
	abstract public boolean collision(Rectangle2D.Float target);
	
//	public boolean check(int x, int y, int width, int height){
//		boolean xc = false , yc = false;
//		
//		if(this.x + this.cx <= x && x <= this.x + this.cx + cwidth){ // �����ʿ��� �ε�ħ
//			xc = true;
//		} else if(x <= this.x + this.cx && this.x + this.cx <= x + width){ // ���ʿ��� �ε�ħ
//			xc = true;
//		}
//		
//		if(this.y + this.cy <= y && y <= this.y + this.cy + this.cheight){ // �ؿ��� �ε�ħ
//			yc = true;
//		} else if(y <= this.y + this.cy && this.y + this.cy <= y + height){ // ������ �ε�ħ
//			yc = true;
//		}
//		
//		return xc && yc;
//	}
}