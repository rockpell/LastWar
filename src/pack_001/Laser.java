package pack_001;

import java.awt.geom.Rectangle2D;


final class Laser extends Colider implements Obstacle{
	private String name;
	private float x = 0, y = 0, width = 0, height = 0;
	private int count = 0, countLimit = 50, deadLimit = 75;
	private boolean wallColide = false, is_active = false;
	private Wall targetWall;
	
	private DataManager dm;
	private LaserArrow linkLar;
	
	public Laser(int x, int y){
		dm = DataManager.getInstance();
		
		if(dm.addColider(this)){
			linkLar = dm.findArrow(x, y);
		}
		setPosition(linkLar.getIndexX(), linkLar.getIndexY());
		setBox(0, 0, width, height);
	}
	
	
	public void work(){
		count++;
		if(count > countLimit / 10){
			is_active = true;
			linkLar.setExist(true);
		}
		
		if(count > countLimit && !trigger){
			trigger = true;
		} else if(count > deadLimit){
			dead();
		}
		
	}
	
	@Override
	public void dead() {
		// TODO Auto-generated method stub
		if(dm.removeColider(this)){
			linkLar.setExist(false);
		}
		
		// wall에 데미지 입히는것 여기서 실행
		// 충돌 처리가 여러번 일어나기 때문에 여기서 실행해야함
		if(targetWall != null){
			targetWall.damage();
		}
		
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setPosition(int x, int y) {
		
		if(x == 0 || x == 1){ // 분리
			name = "row" + (x+1);
			width = 1150 - 32;
			height = 10;
			this.x = Screen.rowStartX1 + 32;
			this.y = Screen.rowStartY + y*50 + 16;
			
		} else if(x == 2 || x == 3){
			name = "col" + (x-1);
			width = 10;
			height = 600 - 32;
			this.x = Screen.colStartX + y*50 + 16;
			this.y = Screen.colStartY1 + 32;
			
		}
	}

	@Override
	public Rectangle2D.Float getBounds() {
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public float getPosition(String name){
		if(name.equals("x")){
			return x;
		} else {
			return y;
		}
	}
	
	public float getSize(String name){
		if(name.equals("width")){
			return width;
		} else {
			return height;
		}
	}
	
	public String getName(){
		return name;
	}
	
	public boolean getIsActive(){
		return is_active;
	}
	
	public float calLaserSize(String text){
		if(text.equals("height")){
			float temp = countLimit - height;
			return count - temp;
		} else if(text.equals("width")){
			float temp = countLimit - width;
			return count - temp;
		}
		
		return 0;
	}
	
	public void setWallColide(Wall w){
		targetWall = w;
		setSizeWhileColide();
	}
	
	private void setSizeWhileColide(){
		Rectangle2D.Float wallRectange = targetWall.getBounds();
		
		if(name.equals("row1")){
			width = wallRectange.x - Screen.rowStartX1 - 32;
			cwidth = width;
		} else if(name.equals("row2")){
			width = 1150 - 32;
			x = wallRectange.x + wallRectange.width;
			width -= (x - 32);
			cwidth = width;
		} else if(name.equals("col1")){
			height = wallRectange.y - Screen.colStartY1 - 32;
			cheight = height;
		} else if(name.equals("col2")){
			height = 600 - 32;
			y = wallRectange.y + wallRectange.height;
			height =  height - (y - 32*2 - 16);
			cheight = height;
		}
//		System.out.println("dm :    " + this.getBounds());
	}
	
	public boolean getWallColide(){
		return wallColide;
	}
	
	public Wall getTargetWall(){
		return targetWall;
	}
}