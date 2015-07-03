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
	float x, y; // 오브젝트 화면 위치
	int cx = 0, cy = 0; // 충돌 박스의 오브젝트에 대한 상대 위치
	int cwidth = 0, cheight = 0; // 충돌 박스 크기
	
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
//		if(this.x + this.cx <= x && x <= this.x + this.cx + cwidth){ // 오른쪽에서 부딪침
//			xc = true;
//		} else if(x <= this.x + this.cx && this.x + this.cx <= x + width){ // 왼쪽에서 부딪침
//			xc = true;
//		}
//		
//		if(this.y + this.cy <= y && y <= this.y + this.cy + this.cheight){ // 밑에서 부딪침
//			yc = true;
//		} else if(y <= this.y + this.cy && this.y + this.cy <= y + height){ // 위에서 부딪침
//			yc = true;
//		}
//		
//		return xc && yc;
//	}
}