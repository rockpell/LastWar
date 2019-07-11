package pack_001;

final class Loader implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DataManagement.getInstance().loadImage();
		Screen.getInstance().loadImage();
		Engine.getInstance().startLoop();
		DataManagement.getInstance().setIsGameStart(true);
		Screen.getInstance().pauseScreenOn();
		
		Screen.getInstance().repaint();
	}
}