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
		gameManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(GameManager inputManager)
	{
		inputManager.ChageState(new MenuState());
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
		GameManager.getInstance().stopGameLoop();
		DataManager.getInstance().getAudio().stop();
		
		gameManager.ChageState(new PauseState());
	}
	
	@Override
	public void arrowKey(GameManager gameManager, int x, int y)
	{
		DataManager.getInstance().getPlayer().move(x, y);
	}
}

class PauseState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		gameManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(GameManager gameManager)
	{
		DataManager.getInstance().initData();
		Screen.getInstance().repaint();
		gameManager.ChageState(new MenuState());
	}
}

class CountDownState extends GameState
{
	public CountDownState()
	{
		GameManager.getInstance().starCountdown();
	}
	public void exit(GameManager gameManager)
	{
		gameManager.ChageState(new ProgressState());
	}
}

class DeadState extends GameState
{
	public DeadState()
	{
		GameManager.getInstance().stopGameLoop();
	}
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		DataManager.getInstance().initData();
				
		gameManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(GameManager gameManager)
	{
		DataManager.getInstance().initData();
		
		Screen.getInstance().repaint();
		
		gameManager.ChageState(new MenuState());
	}
}

class StoryEndState extends GameState
{
	@Override
	public void selectKey(GameManager gameManager, int selectIndex)
	{
		DataManager.getInstance().initData();
		
		Screen.getInstance().repaint();
		
		gameManager.ChageState(new MenuState());
	}
}