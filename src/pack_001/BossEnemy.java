package pack_001;

final class BossEnemy extends Enemy{
	BossEnemy(int hp) {
		super(hp);
		setMoeny(200);
		addVision(120);
		addSpeed(-0.3f);
		setTypeName("boss");
		setSize(72, 72);
		if(hp == 0){
			setHp(10);
		}
	}
	
	public void dead(){
		if(DataManagement.getInstance().getGameLevel().getMode() == 0){
			GameManager.getInstance().setIsStoryEnd(true);
			DataManagement.getInstance().getPlayer().dead();
		} else {
			super.dead();
		}
		System.out.println("boss dead");	
	}
}