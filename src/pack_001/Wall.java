package pack_001;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

final class Wall extends Colider implements Obstacle {
	
	private float x, y;
	private int width, height;
//	private int count;
	private int hp = 1, maxHp = 1;
	private boolean outTrigger = false, outTriggerMoment = false; // 물체 내부에서 나간 후에 충돌 처리
	
	Wall(float x, float y){
		setPosition(x, y);
		setSize(48, 48);
		trigger = true; // 충돌 가능
		
		maxHp += DataManagement.getInstance().getPlusHp();
		hp = maxHp;
	}
	
	@Override
	public void dead() {
		// TODO Auto-generated method stub
		DataManagement.getInstance().removeWall(this);
	}

	public void work() {
		if(hp <= 0){
			dead();
		}
	}
	
	public void damage(){
		hp -= 1;
	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
		setBox(0, 0, width, height);
	}
	
	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getPosition(String text){
		if(text.equals("x")){
			return x;
		} else {
			return y;
		}
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	
	@Override
	public Float getBounds() {
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target) {
		return target.intersects(this.getBounds());
	}
	
	public boolean getOutTrigger(){
		return outTrigger;
	}
	
	public void setOutTrigger(boolean value){
		outTrigger = value;
	}
	
	public boolean getOutTriggerMoment(){
		return outTriggerMoment;
	}
	
	public void setOutTriggerMoment(boolean value){
		outTriggerMoment = value;
	}
	
	public int getHp(){
		return hp;
	}
	
	public int getMaxHp(){
		return maxHp;
	}
	
	public int getSize(String text){
		if(text.equals("height")){
			return height;
		} else {
			return width;
		}
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}