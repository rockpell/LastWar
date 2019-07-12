package pack_001;

class GameState
{
	public void selectKey(GameManager gameManager, int selectIndex) {}
	public void escKey(GameManager gameManager) {}
	public void arrowKey(GameManager gameManager, int x, int y) {}
	public void exit(GameManager gameManager) {}
}

class MenuState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		InputManager.getInstance().menuSelect();
		if (selectIndex == -1) // tutorial
		{ 
			gameManager.ChageState(new TutorialState());
		}
		else if (selectIndex == 0) // story mode
		{
			gameManager.ChageState(new StoryState());
		}
		else if (selectIndex == 1) // never ending mode
		{
			gameManager.ChageState(new CountDownState());
		}
	}

	@Override
	public void arrowKey(GameManager gameManager, int x, int y)
	{
		InputManager.getInstance().selectControl(y);
	}
}

class TutorialState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		InputManager.getInstance().addTutorialPage(1);
		Screen.getInstance().repaint();
	}

	@Override
	public void escKey(GameManager gameManager)
	{
		Screen.getInstance().repaint();
		gameManager.ChageState(new MenuState());
	}
}

class StoryState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		GameManager.getInstance().loadThread();
		DataManagement.getInstance().createGameLevel(0);
		gameManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(GameManager inputManager)
	{

	}
}

class ProgressState extends GameState
{
	@Override
	public void escKey(GameManager gameManager)
	{
//		inputManager.ChageState(new PauseState());
	}

	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		// TODO Auto-generated method stub
		GameManager.getInstance().stopLoop();
		DataManagement.getInstance().getAudio().stop();
		
		gameManager.ChageState(new PauseState());
	}
	
	@Override
	public void arrowKey(GameManager gameManager, int x, int y)
	{
		DataManagement.getInstance().getPlayer().move(x, y);
	}
}

class PauseState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		GameManager.getInstance().startLoop();
		gameManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(GameManager gameManager)
	{
		DataManagement.getInstance().initData();
		GameManager.getInstance().initLoop();
		Screen.getInstance().repaint();
		gameManager.ChageState(new MenuState());
	}
}

class CountDownState extends GameState
{
	public void exit(GameManager gameManager)
	{
		gameManager.ChageState(new ProgressState());
	}
}

class DeadState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		DataManagement.getInstance().initData();
		
		GameManager.getInstance().newLoop();
		GameManager.getInstance().startLoop();
				
		gameManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(GameManager gameManager)
	{
		DataManagement.getInstance().initData();
		GameManager.getInstance().initLoop();
		
		Screen.getInstance().repaint();
		
		gameManager.ChageState(new MenuState());
	}
}

class StoryEndState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		DataManagement.getInstance().initData();
		GameManager.getInstance().initLoop();
		
		Screen.getInstance().repaint();
		
		gameManager.ChageState(new MenuState());
	}

	@Override
	public void escKey(GameManager inputManager)
	{

	}
}