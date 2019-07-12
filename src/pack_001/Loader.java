package pack_001;

final class Loader implements Runnable
{ // 이미지 로딩용
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		DataManagement.getInstance().loadImage();
		Screen.getInstance().loadImage();
	}
}