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
			if(dm.getCost(0) > dm.getMoney()){
				Engine.getInstance().settingMessage();
				return;
			}
			if(!Screen.getInstance().getStopOn()){
				if(dm.getCoolTimeLeft() == 0 && dm.getPlayer().isWallAble()){
					if(!dm.getPlayer().getOutTrigger()){
						if(dm.getWallSetCount() < dm.getWallLimit()){
							dm.addWall(dm.getPlayer().getPosition().x, dm.getPlayer().getPosition().y);
							dm.subMoney(dm.getCost(0));
							dm.upCost(0);
						}
					}
					dm.getPlayer().setOutTrigger(true);
					dm.initCoolTime();
				}
			}
		} else if(name.equals("heal")){
			if(dm.getCost(1) > dm.getMoney()){
				Engine.getInstance().settingMessage();
				return;
			} else if(dm.getPlayer().getHp() == dm.getPlayer().getMaxHp()){
				return;
			} 
			dm.getPlayer().heal();
			dm.subMoney(dm.getCost(1));
			dm.upCost(1);
		} else if(name.equals("hp")){
			if(dm.getCost(2) > dm.getMoney()){
				Engine.getInstance().settingMessage();
				return;
			}
			dm.getPlayer().maxHpUp();
			dm.subMoney(dm.getCost(2));
			dm.upCost(2);
		} else if(name.equals("wallhp")){
			if(dm.getCost(3) > dm.getMoney()){
				Engine.getInstance().settingMessage();
				return;
			}
			dm.subMoney(dm.getCost(3));
			dm.upCost(3);
			dm.plusHp();
		} else if(name.equals("wallcool")){
			if(dm.getCost(4) > dm.getMoney()){
				Engine.getInstance().settingMessage();
				return;
			}
			dm.subMoney(dm.getCost(4));
			dm.upCost(4);
			dm.minusWallCoolTime(1);
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