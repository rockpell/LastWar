package pack_001;

public class UI {
	protected int x, y;
	protected int width, height;
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}

class Skill extends UI{
	String name;
	
	Skill(String text, int x, int y, int width, int height){
		name = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void skillExcute(){
		DataManagement dm = DataManagement.getInstance();
		
		if(name.equals("wall")){
			if(!Screen.getInstance().getStopOn()){
				if(dm.getCoolTimeLeft() == 0 && dm.getPlayer().isWallAble()){
					if(!dm.getPlayer().getOutTrigger()){
						if(dm.getWallSetCount() < dm.getWallLimit())
							dm.addWall(dm.getPlayer().getPosition().x, dm.getPlayer().getPosition().y);
					}
					dm.getPlayer().setOutTrigger(true);
					dm.initCoolTime();
				}
			}
		} else if(name.equals("heal")){
			dm.getPlayer().heal();
		} else if(name.equals("hp")){
			dm.getPlayer().maxHpUp();
		}
	}
	
	public void skillClick(int x, int y){
		if(pointCheck(x, y)){
			skillExcute();
		}
	}
	
	private boolean pointCheck(int x, int y){
		if(this.x < x && x < this.x + this.width){
			if(this.y < y && y < this.y + this.height){
				return true;
			}
		}
		return false;
	}
}