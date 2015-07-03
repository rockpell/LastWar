package pack_001;

public interface Unit {
	int x = 0, y = 0;
	int width = 0, height = 0;
	int hp = 0;
	
	public void work();
	public void dead();
	public int getHp();
	public void setSize(int width, int height);
	public void setPosition(int x, int y);
}