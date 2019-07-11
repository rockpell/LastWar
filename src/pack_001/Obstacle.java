package pack_001;

public interface Obstacle {
	
	public void dead();
	public void work();

	public void setSize(int width, int height);

	public void setPosition(int x, int y);
}