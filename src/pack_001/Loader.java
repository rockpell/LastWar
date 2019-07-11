package pack_001;

final class Loader implements Runnable
{

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		DataManagement.getInstance().loadImage();
		Screen.getInstance().loadImage();
		GameManager.getInstance().startLoop();
		DataManagement.getInstance().setIsGameStart(true);
		GameManager.getInstance().pauseScreenOn();

		Screen.getInstance().repaint();
	}
}