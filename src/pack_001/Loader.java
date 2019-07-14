package pack_001;

final class Loader implements Runnable
{ // 이미지 로딩용
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		DataManager.getInstance().imageLoad();
		DataManager.getInstance().tutorialImageLoad();
		Screen.getInstance().imageLoad();
		Screen.getInstance().tutorialImageLoad();
	}
}