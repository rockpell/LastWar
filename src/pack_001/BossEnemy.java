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
		if(DataManager.getInstance().getGameLevel().getMode() == 0){
			DataManager.getInstance().getAudio().stop();
			
			GameManager.getInstance().ChageState(new StoryEndState());
		} else {
			super.dead();
		}
	}
}