package pack_001;

public interface Unit {
	
	public void work();
	public void dead();
	public int getHp();

	public void setSize(int width, int height);

	public void setPosition(int x, int y);
}