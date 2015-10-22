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
		Engine.getInstance().initMessage2();
		if(Screen.getInstance().getStopOn() || Screen.getInstance().getTempStoper()){
			System.out.println("stop");
			return;
		}
		
		if(name.equals("wall")){
			if(dm.getCost(0) > dm.getMoney()){
				Engine.getInstance().settingMessage("point");
				return;
			}
			
			if(dm.getCoolTimeLeft() == 0 && dm.getPlayer().isWallAble()){
				if(!dm.getPlayer().getOutTrigger()){
					dm.addWall(dm.getPlayer().getPosition().x, dm.getPlayer().getPosition().y);
					dm.subMoney(dm.getCost(0));
				}
				dm.getPlayer().setOutTrigger(true);
				dm.initCoolTime();
			} else if(dm.getCoolTimeLeft() != 0){
				Engine.getInstance().settingMessage("cool");
			}
			
		} else if(name.equals("heal")){
			if(dm.getCost(1) > dm.getMoney()){
				Engine.getInstance().settingMessage("point");
				return;
			} else if(dm.getPlayer().getHp() == dm.getPlayer().getMaxHp()){
				Engine.getInstance().settingMessage("heal");
				return;
			}
			
			int cost1 = dm.getCost(1);
			
			dm.getPlayer().heal();
			dm.subMoney(cost1);
			dm.upCost(1);
			Engine.getInstance().settingMessage("heal_ok");
			Engine.getInstance().settingMessage2(dm.getCost(1) - cost1);
		} else if(name.equals("hp")){
			if(dm.getCost(2) > dm.getMoney()){
				Engine.getInstance().settingMessage("point");
				return;
			}
			
			int cost1 = dm.getCost(2);
			
			dm.getPlayer().maxHpUp();
			dm.subMoney(cost1);
			dm.upCost(2);
			Engine.getInstance().settingMessage("hp_ok");
			Engine.getInstance().settingMessage2(dm.getCost(2) - cost1);
		} else if(name.equals("wallhp")){
			if(dm.getCost(3) > dm.getMoney()){
				Engine.getInstance().settingMessage("point");
				return;
			}
			
			int cost1 = dm.getCost(3);
			
			dm.subMoney(cost1);
			dm.upCost(3);
			dm.plusHp();
			Engine.getInstance().settingMessage("wall_hp_ok");
			Engine.getInstance().settingMessage2(dm.getCost(3) - cost1);
		} else if(name.equals("wallcool")){
			if(dm.getCost(4) > dm.getMoney()){
				Engine.getInstance().settingMessage("point");
				return;
			}
			
			int cost1 = dm.getCost(4);
			
			dm.subMoney(cost1);
			dm.upCost(4);
			dm.minusWallCoolTime(1);
			Engine.getInstance().settingMessage("wall_cool_ok");
			Engine.getInstance().settingMessage2(dm.getCost(4) - cost1);
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

class AlarmText extends UI{
	private String text;
	private int time;
	private float fx, fy;
	
	AlarmText(float x, float y){
		this.fx = x;
		this.fy = y;
	}
	
	public void setText(String text, int value){
		this.text = String.valueOf(value) + text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public void work(){
		time += 1;
		fy -= 0.3f;
		if(time > 100){
			DataManagement.getInstance().removeAlarm(this);
		}
	}
	
	public String getText(){
		return text;
	}
	
	public float getFX(){
		return fx;
	}
	
	public float getFY(){
		return fy;
	}
	
	public int getTime(){
		return time;
	}
}