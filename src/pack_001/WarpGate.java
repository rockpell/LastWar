package pack_001;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

final class WarpGate extends Colider implements Obstacle{
	private boolean is_open = false;
	private int open_count = 0;
	
	WarpGate(){
		setPosition(560, 346);
		setSize(64, 64);
	}
	
	@Override
	public void dead() {
		
	}

	@Override
	public void work() {
		if(!is_open){
			return;
		} else {
			open_count += 1;
			if(open_count > 40){
				open_count = 0;
				is_open = false;
			}
		}
	}
	
	public void setOpen(){
		is_open = true;
		open_count = 0;
	}
	
	public boolean isOpen(){
		return is_open;
	}
	
	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		setBox(0, 0, width, height);
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Float getBounds() {
		// TODO Auto-generated method stub
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}
	
	@Override
	public boolean collision(Rectangle2D.Float target) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
}