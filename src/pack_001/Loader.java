package pack_001;

final class Loader implements Runnable
{ // �̹��� �ε���
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		DataManagement.getInstance().loadImage();
		Screen.getInstance().loadImage();
	}
}