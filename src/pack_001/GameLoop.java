package pack_001;

final class GameLoop implements Runnable{
	private String name;
	private int interval = 0, playTime = 0;
	
	private Screen sc;
	private Engine engine;
	private DataManagement dm;
	private GameLevel gameLevel;
	
	GameLoop(int interval){
		this.interval = interval;
		sc = Screen.getInstance();
		engine = Engine.getInstance();
		dm = DataManagement.getInstance();
		
		playTime = engine.getPlayTime();
		
		gameLevel = dm.getGameLevel();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				FPScounter.StartCounter();
				
				sc.repaint();
				
				engine.loopColider();
				dm.getPlayer().work();
				
				engine.setPlayTime(playTime += 1);
				gameLevel.levelStart();
				
				if(playTime % 20 == 0){
					dm.countCoolTime();
					dm.addMoney(1);
					dm.addScore(1);
				}
				
				engine.workMessage();
				
				FPScounter.StopAndPost();
				interval = (1000 / engine.getFps()) - FPScounter.getElapsedTime();
				
				if(interval < 5){
					interval = 20;
				}
				
				Thread.sleep(interval);
				engine.killThread();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setInterval(int val){
		interval = val;
	}
}