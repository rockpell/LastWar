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
	
	private DataManagement dm;
	private GameManager gameManager;
	private Player player;
	
	private boolean pup = false, pdown = false; // game before control screen value
	private int selectIndex = 0, tutorialPage = 0;
	
	InputManager(){
		dm = DataManagement.getInstance();
		gameManager = GameManager.getInstance();
		player = dm.getPlayer();
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

				if (_keyList.size() > 1)
				{
					String _keyValue = "";
					int _count = 0;
					for (int it : _keyList)
					{
						switch (it)
						{
						case KeyEvent.VK_UP:
							_keyValue += "up";
							_count++;
							break;
						case KeyEvent.VK_DOWN:
							_keyValue += "down";
							_count++;
							break;
						case KeyEvent.VK_RIGHT:
							_keyValue += "right";
							_count++;
							break;
						case KeyEvent.VK_LEFT:
							_keyValue += "left";
							_count++;
							break;
						}
					}

					if (_count > 1)
					{
						player.move(_keyValue);
						return;
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_UP)
				{
					player.move("up");
					beforeControl(0);
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					player.move("down");
					beforeControl(1);
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					player.move("right");
				}
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				{
					player.move("left");
				}
				else if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (gameManager.getIsTutorialStart())
					{
						tutorialPage += 1;
					}
					else if (!dm.getGameEnd())
					{
						if (gameManager.getIsPause())
						{
							if (!dm.getIsGameStart())
							{ // before game
								if (gameManager.getIsStroyStart())
								{ // story screen after
									gameManager.loadThread();
									gameManager.setIsBeforeStart(true);
									dm.createGameLevel(0);
									gameManager.setIsStroyStart(false);
									return;
								}
								
								if (selectIndex == 0)
								{ // story mode
									gameManager.setIsStroyStart(true);
								}
								else if (selectIndex == 1)
								{ // never ending mode
									GameManager.getInstance().loadThread();
									gameManager.setIsBeforeStart(true);
									dm.createGameLevel(1);
									System.out.println("gameManager.getIsbeforeStart: " + gameManager.getIsBeforeStart());
								}
								else if (selectIndex == 2)
								{ // game exit
									System.exit(0);
								}
								else if (selectIndex == -1)
								{ // show tutorial image
									gameManager.setIsTutorialStart(true);
									tutorialPage = 0;
									Screen.getInstance().tloadImage();
								}
							}
							else
							{ // restart
								// am.play();
								if (!gameManager.getIsCountDown())
								{
									gameManager.pauseScreenOn();
									gameManager.startLoop();
								}
							}

						}
						else
						{ // game stop
							if (!gameManager.getIsCountDown())
							{
								gameManager.pauseScreenOn();
								gameManager.stopLoop();
								dm.getAudio().stop();
							}
						}
					}
					else
					{ // game end after replay
						if (gameManager.getIsStroyEnd())
						{
							return;
						}
						dm.initData();
						dm.setIsGameStart(true);
						gameManager.newLoop();
						gameManager.startLoop();
						gameManager.setIsPause(false);
						
						player = dm.getPlayer();
						
						gameManager.setIsBeforeStart(true);
						gameManager.setIsStoryEnd(false);
					}
					Screen.getInstance().repaint();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{ // return main menu
					if (dm.getGameEnd())
					{
						dm.initData();
						gameManager.setIsPause(true);
						gameManager.initLoop();
						gameManager.setIsStoryEnd(false);
						player = dm.getPlayer();
						selectIndex = 0;
						Screen.getInstance().repaint();
					}
					else if (gameManager.getIsPause() && !gameManager.getIsTutorialStart())
					{
						dm.initData();
						GameManager.getInstance().initLoop();
						player = dm.getPlayer();
						selectIndex = 0;
						gameManager.setIsBeforeStart(false);
						Screen.getInstance().repaint();
					}
					else if (gameManager.getIsTutorialStart())
					{
						gameManager.setIsTutorialStart(false);
						tutorialPage = 0;
						Screen.getInstance().repaint();
					}
				}

			}

			@Override
			public synchronized void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub

				_keyList.remove(e.getKeyCode());

				for (int it : _keyList)
				{
					switch (it)
					{
					case KeyEvent.VK_UP:
						player.move("up");
						break;
					case KeyEvent.VK_DOWN:
						player.move("down");
						break;
					case KeyEvent.VK_RIGHT:
						player.move("right");
						break;
					case KeyEvent.VK_LEFT:
						player.move("left");
						break;
					}
					return;
				}

				if (_keyList.size() == 0)
				{
					player.move("stop");
				}
			}

			@Override
			public synchronized void keyTyped(KeyEvent e)
			{ // not working arrow key
				// TODO Auto-generated method stub
				// System.out.println(e.getKeyChar());
			}

		};

		return result;
	}
	
	public MouseAdapter mouseBind() {
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
	
	private void controlPosition()
	{
		if (pup)
		{
			pup = false;
			if (selectIndex > -1)
			{
				selectIndex -= 1;
			}
		}

		if (pdown)
		{
			pdown = false;
			if (selectIndex < 2)
			{
				selectIndex += 1;
			}
		}

	}

	private void beforeControl(int type)
	{
		if (!dm.getIsGameStart() && !dm.getGameEnd() && !gameManager.getIsBeforeStart() && !gameManager.getIsStroyEnd())
		{
			if (type == 0)
			{
				pup = true;
			}
			else if (type == 1)
			{
				pdown = true;
			}
		}

		controlPosition();
		Screen.getInstance().repaint();
	}
	
	public int getSelectIndex() {
		return selectIndex;
	}
	
	public int getTutorialPage() {
		return tutorialPage;
	}
}
