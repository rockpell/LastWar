package pack_001;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;


public interface Obstacle {
	
	public void dead();
	public void work();
	public void setSize(int width, int height);
	public void setPosition(int x, int y);
}