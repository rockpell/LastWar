package pack_001;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

final public class InputManager
{
	private static InputManager instance;

	public static InputManager getInstance()
	{
		if (instance == null)
		{
			instance = new InputManager();
		}

		return instance;
	}

	private DataManager dm;
	private GameManager gameManager;

	private int selectIndex = 0, tutorialPage = 0;

	InputManager()
	{
		dm = DataManager.getInstance();
		gameManager = GameManager.getInstance();
	}

	public KeyListener keyBind()
	{
		KeyListener result = new KeyListener()
		{
			private final Set<Integer> _keyList = new HashSet<Integer>();

			@Override
			public synchronized void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT)
				{
					_keyList.add(e.getKeyCode());
				}
				else if (e.getKeyCode() == KeyEvent.VK_A)
				{
					dm.getSkill(0).skillExcute();
				}
				else if (e.getKeyCode() == KeyEvent.VK_S)
				{
					dm.getSkill(1).skillExcute();
				}
				else if (e.getKeyCode() == KeyEvent.VK_D)
				{
					dm.getSkill(2).skillExcute();
				}
				else if (e.getKeyCode() == KeyEvent.VK_F)
				{
					dm.getSkill(3).skillExcute();
				}
				else if (e.getKeyCode() == KeyEvent.VK_G)
				{
					dm.getSkill(4).skillExcute();
				}

				if (_keyList.size() > 0)
				{
					int _xValue = 0, _yValue = 0;
					for (int it : _keyList)
					{
						switch (it)
						{
						case KeyEvent.VK_UP:
							_yValue -= 1;
							break;
						case KeyEvent.VK_DOWN:
							_yValue += 1;
							break;
						case KeyEvent.VK_RIGHT:
							_xValue += 1;
							break;
						case KeyEvent.VK_LEFT:
							_xValue -= 1;
							break;
						}
					}
					gameManager.getNowState().arrowKey(GameManager.getInstance(), _xValue, _yValue);
				}

				if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					gameManager.getNowState().selectKey(GameManager.getInstance(), selectIndex);
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{ // return main menu
					gameManager.getNowState().escKey(GameManager.getInstance());
				}
			}

			@Override
			public synchronized void keyReleased(KeyEvent e)
			{ // 키 입력 해제 후에도 해당 방향으로 이동하지 않게 해줌
				_keyList.remove(e.getKeyCode());

				for (int it : _keyList)
				{
					switch (it)
					{
					case KeyEvent.VK_UP:
						gameManager.getNowState().arrowKey(GameManager.getInstance(), 0, -1);
						break;
					case KeyEvent.VK_DOWN:
						gameManager.getNowState().arrowKey(GameManager.getInstance(), 0, 1);
						break;
					case KeyEvent.VK_RIGHT:
						gameManager.getNowState().arrowKey(GameManager.getInstance(), 1, 0);
						break;
					case KeyEvent.VK_LEFT:
						gameManager.getNowState().arrowKey(GameManager.getInstance(), -1, 0);
						break;
					}
				}
				if (_keyList.size() == 0)
				{
					gameManager.getNowState().arrowKey(GameManager.getInstance(), 0, 0);
				}
			}
			@Override
			public synchronized void keyTyped(KeyEvent e){}
		};

		return result;
	}

	public MouseAdapter mouseBind()
	{
		MouseAdapter result = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent arg0)
			{
				dm.getSkill(0).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				dm.getSkill(1).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				dm.getSkill(2).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				dm.getSkill(3).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				dm.getSkill(4).skillClick(arg0.getPoint().x, arg0.getPoint().y);
			}
		};
		return result;
	}

	public void selectControl(int upDown)
	{
		if (upDown == 1 || upDown == -1)
		{
			selectIndex += upDown;
		}

		if (selectIndex > 2)
			selectIndex = 2;
		else if (selectIndex < -1)
			selectIndex = -1;
		Screen.getInstance().repaint();
	}

	public boolean menuSelect()
	{
		if (selectIndex == -1)
		{ // show tutorial image
			tutorialPage = 0;
		}
		else if (selectIndex == 0)
		{ // story mode
			dm.createGameLevel(0);
		}
		else if (selectIndex == 1)
		{ // never ending mode
			dm.createGameLevel(1);
		}
		else if (selectIndex == 2)
		{ // game exit
			System.exit(0);
		}
		selectIndex = 0;
		Screen.getInstance().repaint();
		return true;
	}

	public int getSelectIndex()
	{
		return selectIndex;
	}

	public int getTutorialPage()
	{
		return tutorialPage;
	}

	public void setTutorialPage(int value)
	{
		tutorialPage = value;
	}

	public void addTutorialPage(int value)
	{
		tutorialPage += value;
	}
}