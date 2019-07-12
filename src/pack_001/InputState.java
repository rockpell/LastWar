package pack_001;

class InputState
{
	public void selectKey(InputManager inputManager, int selectIndex) {}
	public void escKey(InputManager inputManager) {}
	public void arrowKey(InputManager inputManager, int x, int y) {}
	public void exit(InputManager inputManager) {}
}

class MenuState extends InputState
{
	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		inputManager.menuSelect();
		if (selectIndex == -1) // tutorial
		{ 
			inputManager.ChageState(new TutorialState());
		}
		else if (selectIndex == 0) // story mode
		{
			inputManager.ChageState(new StoryState());
		}
		else if (selectIndex == 1) // never ending mode
		{
			inputManager.ChageState(new CountDownState());
		}
	}

	@Override
	public void arrowKey(InputManager inputManager, int x, int y)
	{
		inputManager.selectControl(y);
	}
}

class TutorialState extends InputState
{
	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		inputManager.addTutorialPage(1);
	}

	@Override
	public void escKey(InputManager inputManager)
	{
		GameManager.getInstance().setIsTutorialStart(false);
		Screen.getInstance().repaint();
		inputManager.ChageState(new MenuState());
	}
}

class StoryState extends InputState
{
	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		GameManager.getInstance().loadThread();
		GameManager.getInstance().setIsBeforeStart(true);
		DataManagement.getInstance().createGameLevel(0);
		GameManager.getInstance().setIsStroyStart(false);
		inputManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(InputManager inputManager)
	{

	}
}

class ProgressState extends InputState
{
	@Override
	public void escKey(InputManager inputManager)
	{
//		inputManager.ChageState(new PauseState());
	}

	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		// TODO Auto-generated method stub
		GameManager.getInstance().pauseScreenOn();
		GameManager.getInstance().stopLoop();
		DataManagement.getInstance().getAudio().stop();
		
		inputManager.ChageState(new PauseState());
	}
	
	@Override
	public void arrowKey(InputManager inputManager, int x, int y)
	{
		DataManagement.getInstance().getPlayer().move(x, y);
	}
}

class PauseState extends InputState
{
	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		GameManager.getInstance().pauseScreenOn();
		GameManager.getInstance().startLoop();
		inputManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(InputManager inputManager)
	{
		DataManagement.getInstance().initData();
		GameManager.getInstance().initLoop();
		GameManager.getInstance().setIsBeforeStart(false);
		Screen.getInstance().repaint();
		inputManager.ChageState(new MenuState());
	}
}

class CountDownState extends InputState
{
	public void exit(InputManager inputManager)
	{
		inputManager.ChageState(new ProgressState());
	}
}

class DeadState extends InputState
{
	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		DataManagement.getInstance().initData();
		DataManagement.getInstance().setIsGameStart(true);
		
		GameManager.getInstance().newLoop();
		GameManager.getInstance().startLoop();
		GameManager.getInstance().setIsPause(false);
		GameManager.getInstance().setIsBeforeStart(true);
		GameManager.getInstance().setIsStoryEnd(false);
				
		inputManager.ChageState(new CountDownState());
	}

	@Override
	public void escKey(InputManager inputManager)
	{
		DataManagement.getInstance().initData();
		GameManager.getInstance().setIsPause(true);
		GameManager.getInstance().initLoop();
		GameManager.getInstance().setIsStoryEnd(false);
		
		Screen.getInstance().repaint();
		
		inputManager.ChageState(new MenuState());
	}
}

class StoryEndState extends InputState
{
	@Override
	public void selectKey(InputManager inputManager, int selectIndex)
	{
		DataManagement.getInstance().initData();
		GameManager.getInstance().setIsPause(true);
		GameManager.getInstance().initLoop();
		GameManager.getInstance().setIsStoryEnd(false);
		
		Screen.getInstance().repaint();
		
		inputManager.ChageState(new MenuState());
	}

	@Override
	public void escKey(InputManager inputManager)
	{

	}
}