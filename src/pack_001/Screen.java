package pack_001;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

final public class Screen extends JFrame
{
	private static final long serialVersionUID = 1L;

	private static Screen instance;

	public static Screen getInstance()
	{
		if (instance == null)
		{
			instance = new Screen();
		}

		return instance;
	}

	public static final int screenWidth = 1200, screenHeight = 800;
	public static final int rowStartX1 = 20, rowStartX2 = screenWidth - 50, rowStartY = 110, colStartX = 60, colStartY1 = 50,
			colStartY2 = screenHeight - 150;
	private final int gui_x = screenWidth / 2 - 140;
	private final int gui_y1 = 430, gui_y2 = 500, gui_y3 = 570;
	
	BufferedImage memoryimage;
	Graphics2D mgc;

	private Player player;
	private DataManager dataManager;
	private GameManager gameManager;

	private Image playerImage, hp_potion, hp_plus, wall_hp, wall_time;
	private Image arrow_right, arrow_left, arrow_up, arrow_down;
	private Image arrow_right_red, arrow_left_red, arrow_up_red, arrow_down_red;
	private Image excavator_001, excavator_002, brick_black;
	private Image closed_door, open_door;
	private Image t_skill, t_02, t_03, t_05, t_06;

	private int skillMessageTextDuration = 0, skillMessageTextMaxDuration = 50;
	private String messageText, costMessageText;

	private Screen()
	{
		super("Last War");

		dataManager = DataManager.getInstance();
		gameManager = GameManager.getInstance();
		player = dataManager.getPlayer();

		setSize(screenWidth, screenHeight);

		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void imageLoad()
	{
		playerImage = dataManager.mshi;
		arrow_right = dataManager.arrow_right;
		arrow_left = dataManager.arrow_left;
		arrow_up = dataManager.arrow_up;
		arrow_down = dataManager.arrow_down;
		arrow_right_red = dataManager.arrow_right_red;
		arrow_left_red = dataManager.arrow_left_red;
		arrow_up_red = dataManager.arrow_up_red;
		arrow_down_red = dataManager.arrow_down_red;
		excavator_001 = dataManager.excavator_001;
		excavator_002 = dataManager.excavator_002;
		closed_door = dataManager.closed_door;
		open_door = dataManager.open_door;
		hp_potion = dataManager.hp_potion;
		hp_plus = dataManager.hp_plus;
		brick_black = dataManager.brick_black;
		wall_hp = dataManager.wall_hp;
		wall_time = dataManager.wall_time;
	}

	public void tutorialImageLoad()
	{
		brick_black = dataManager.brick_black;
		excavator_001 = dataManager.excavator_001;
		t_skill = dataManager.t_skill;
		t_02 = dataManager.t_02;
		t_03 = dataManager.t_03;
		t_05 = dataManager.t_05;
		t_06 = dataManager.t_06;
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	public void paint(Graphics g)
	{
		if (mgc == null)
		{
			try
			{
				memoryimage = new BufferedImage(screenWidth, screenHeight, 1);
				mgc = memoryimage.createGraphics();
				mgc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				mgc.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				mgc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
			catch (Exception e)
			{
				mgc = null;
			}
		}

		mgc.setBackground(Color.white);
		mgc.clearRect(0, 0, screenWidth, screenHeight);

		if (gameManager.getNowState() instanceof ProgressState || gameManager.getNowState() instanceof PauseState
				|| gameManager.getNowState() instanceof CountDownState)
		{
			warpGate();
			drawWall();
			drawLaser(mgc);
			drawEnemy();
			drawPlayer();
			drawAlarmText();

			mgc.setColor(Color.black);
			mgc.setFont(new Font("default", Font.BOLD, 16));

			mgc.drawString("Time:  " + GameManager.getInstance().getPlayTime() / 10, 800, screenHeight - 80);
			mgc.drawString("Score: " + String.valueOf(dataManager.getScore()), 800, screenHeight - 50);
			mgc.drawString("Point: " + String.valueOf(dataManager.getMoney()), 800, screenHeight - 20);

			mgc.drawString("fps: " + GameManager.getInstance().getFps(), 20, 60);

			mgc.setColor(Color.LIGHT_GRAY);
			mgc.fillRect(50, screenHeight - 100, screenWidth - 800, 100);

			drawArrow();
			drawSkillUI();
			drawSkillMessageText();
		}

		pauseScreen();
		menuScreen();
		storyScreen();
		deadScreen();
		stroyEndingScreen();
		tutorialScreen();

		g.drawImage(memoryimage, 0, 0, this);
	}

	private void drawPlayer()
	{
		player = dataManager.getPlayer();
		Point2D.Float point = player.getPosition();
		AffineTransform t = new AffineTransform();
		t.translate(point.x, point.y); // x/y set here
		t.scale(1, 1); // scale = 1

		float opacity = 1 - (float) (dataManager.getPlayer().getDamageCount() % 10) / 10;
		mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

		mgc.drawImage(playerImage, t, null);
		mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		Rectangle2D out_line1 = new Rectangle2D.Float(player.getPosition().x, player.getPosition().y - 15, 45, 15);
		Rectangle2D in_line1 = new Rectangle2D.Float(player.getPosition().x, player.getPosition().y - 15,
				45 * ((float) player.getHp() / (float) player.getMaxHp()), 15);

		mgc.setColor(Color.gray);
		mgc.fill(out_line1);

		if (!(player.getHp() <= 0))
		{
			mgc.setColor(Color.red);
			mgc.fill(in_line1);
		}

		mgc.setColor(Color.black);
		mgc.draw(out_line1);

		int hp_x = 12;

		if (dataManager.getPlayer().getMaxHp() >= 10)
		{
			if (dataManager.getPlayer().getHp() >= 10)
			{
				hp_x = 20;
			}
			else
			{
				hp_x = 16;
			}
		}

		mgc.setFont(new Font("default", Font.PLAIN, 12));
		mgc.setColor(Color.white);
		mgc.drawString(String.valueOf(player.getHp()) + " / " + String.valueOf(player.getMaxHp()),
				player.getPosition().x + player.getWidth() / 2 - hp_x, player.getPosition().y - 3);
	}

	private void drawWall()
	{
		for (Wall wa : dataManager.getWallSet())
		{
			float wx = wa.getPosition("x");
			float wy = wa.getPosition("y");

			AffineTransform t = new AffineTransform();
			t.translate(wx, wy); // x/y set here
			t.scale(1, 1);

			mgc.drawImage(brick_black, t, null);

			Rectangle2D out_line1 = new Rectangle2D.Float(wa.getX(), wa.getY() - 18, wa.getWidth(), 15);
			Rectangle2D in_line1 = new Rectangle2D.Float(wa.getX(), wa.getY() - 18,
					wa.getWidth() * ((float) wa.getHp() / (float) wa.getMaxHp()), 15);

			mgc.setColor(Color.gray);
			mgc.fill(out_line1);

			if (!(wa.getHp() <= 0))
			{
				mgc.setColor(Color.red);
				mgc.fill(in_line1);
			}

			mgc.setColor(Color.black);
			mgc.draw(out_line1);

			mgc.setColor(Color.white);
			mgc.setFont(new Font("default", Font.PLAIN, 12));
			mgc.drawString(String.valueOf(wa.getHp()) + " / " + String.valueOf(wa.getMaxHp()),
					wa.getX() + wa.getWidth() / 2 - 12, wa.getY() - 6);
		}
	}

	private void drawArrow()
	{
		for (LaserArrow lar : dataManager.getArrowSet())
		{
			switch (lar.getIndexX())
			{
			case 0:
				if (lar.isExist())
				{
					mgc.drawImage(arrow_right_red, rowStartX1, rowStartY + lar.getIndexY() * 50, null);
				}
				else
				{
					mgc.drawImage(arrow_right, rowStartX1, rowStartY + lar.getIndexY() * 50, null);
				}
				break;
			case 1:
				if (lar.isExist())
				{
					mgc.drawImage(arrow_left_red, rowStartX2, rowStartY + lar.getIndexY() * 50, null);
				}
				else
				{
					mgc.drawImage(arrow_left, rowStartX2, rowStartY + lar.getIndexY() * 50, null);
				}

				break;
			case 2:
				if (lar.isExist())
				{
					mgc.drawImage(arrow_down_red, colStartX + lar.getIndexY() * 50, colStartY1, null);
				}
				else
				{
					mgc.drawImage(arrow_down, colStartX + lar.getIndexY() * 50, colStartY1, null);
				}

				break;
			case 3:
				if (lar.isExist())
				{
					mgc.drawImage(arrow_up_red, colStartX + lar.getIndexY() * 50, colStartY2, null);
				}
				else
				{
					mgc.drawImage(arrow_up, colStartX + lar.getIndexY() * 50, colStartY2, null);
				}
			}
		}

	}

	private void drawLaser(Graphics2D g)
	{
		for (Laser la : dataManager.getColiderSet())
		{
			if (!la.getIsActive())
			{
				continue;
			}

			if (!la.getTrigger())
			{
				g.setColor(Color.red);
				if (la.getName().contains("row"))
				{
					float temp = la.calLaserSize("height");
					float temp2 = 1;
					if (temp >= 1)
					{
						temp2 = temp;
					}

					float laX = 0, laY = 0, laH = 0, laW = 0;

					laX = la.getPosition("x");
					laY = la.getPosition("y") - temp2 / 2;
					laW = la.getSize("width");
					laH = temp2;

					g.fill(new Rectangle2D.Float(laX, laY, laW, laH));
				}
				else
				{
					float temp = la.calLaserSize("width");
					float temp2 = 1;
					if (temp >= 1)
					{
						temp2 = temp;
					}

					g.fill(new Rectangle2D.Float(la.getPosition("x") - temp2 / 2, la.getPosition("y"), temp2,
							la.getSize("height")));

				}
			}
			else
			{
				g.setColor(Color.blue);

				if (la.getName().contains("row"))
				{
					g.fill(new Rectangle2D.Float(la.getPosition("x"), la.getPosition("y") - la.getSize("height") / 2,
							la.getSize("width"), la.getSize("height")));
				}
				else
				{
					g.fill(new Rectangle2D.Float(la.getPosition("x") - la.getSize("width") / 2, la.getPosition("y"),
							la.getSize("width"), la.getSize("height")));
				}
			}

		}
	}

	private void drawEnemy()
	{
		for (Enemy en : dataManager.getEnemySet())
		{
			int bar_x = 0;
			AffineTransform t = new AffineTransform();
			t.translate(en.getX(), en.getY());

			float opacity = 1 - (float) (en.getDamageCount() % 10) / 10;
			mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

			if (en instanceof Enemy)
			{
				mgc.drawImage(excavator_001, t, null);
			}
			else if (en instanceof BossEnemy)
			{
				mgc.drawImage(excavator_002, t, null);
				bar_x = 12;
			}

			mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

			mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));

			float dia = en.getVision() * 2;
			mgc.setColor(Color.LIGHT_GRAY);
			Ellipse2D.Double circle = new Ellipse2D.Double(en.getX() + en.getWidth() / 2 - dia / 2,
					en.getY() + en.getHeight() / 2 - dia / 2, dia, dia);
			mgc.fill(circle);

			mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

			if (!en.getRandMove())
			{

				Rectangle2D out_line1 = new Rectangle2D.Float(en.getX() + bar_x, en.getY() - 18, 45, 15);
				Rectangle2D in_line1 = new Rectangle2D.Float(en.getX() + bar_x, en.getY() - 18,
						45 * ((float) en.getHp() / (float) en.getMaxHp()), 15);

				mgc.setColor(Color.gray);
				mgc.fill(out_line1);

				if (!(en.getHp() <= 0))
				{
					mgc.setColor(Color.red);
					mgc.fill(in_line1);
				}

				mgc.setColor(Color.black);
				mgc.draw(out_line1);

				int enemey_hp_x = 12;

				if (en.getMaxHp() >= 10)
				{
					if (en.getHp() >= 10)
					{
						enemey_hp_x = 20;
					}
					else
					{
						enemey_hp_x = 16;
					}
				}

				mgc.setColor(Color.white);
				mgc.setFont(new Font("default", Font.PLAIN, 12));
				mgc.drawString(String.valueOf(en.getHp()) + " / " + String.valueOf(en.getMaxHp()),
						en.getX() + en.getWidth() / 2 - enemey_hp_x, en.getY() - 6);

				mgc.setColor(Color.black);
				mgc.setFont(new Font("default", Font.PLAIN, 12));
				mgc.drawString("Lv " + String.valueOf(en.getLevel()), en.getX(), en.getY() - 20);
			}

		}
	}

	private void drawSkillUI()
	{
		Skill skill1 = dataManager.getSkill(0);
		Skill skill2 = dataManager.getSkill(1);
		Skill skill3 = dataManager.getSkill(2);
		Skill skill4 = dataManager.getSkill(3);
		Skill skill5 = dataManager.getSkill(4);

		mgc.setColor(Color.black);
		mgc.drawRect(skill1.getX() - 2, skill1.getY() - 3, skill1.getWidth() + 4, skill1.getHeight() + 4);
		mgc.drawRect(skill2.getX() - 2, skill2.getY() - 3, skill2.getWidth() + 4, skill2.getHeight() + 4);
		mgc.drawRect(skill3.getX() - 2, skill3.getY() - 3, skill3.getWidth() + 4, skill3.getHeight() + 4);
		mgc.drawRect(skill4.getX() - 2, skill4.getY() - 3, skill4.getWidth() + 4, skill4.getHeight() + 4);
		mgc.drawRect(skill5.getX() - 2, skill5.getY() - 3, skill5.getWidth() + 4, skill5.getHeight() + 4);

		ImageManagement _block = new ImageManagement(brick_black);

		mgc.drawImage(_block.grayImage(), skill1.getX(), skill1.getY(), null);
		mgc.drawImage(hp_potion, skill2.getX(), skill2.getY(), null);
		mgc.drawImage(hp_plus, skill3.getX(), skill3.getY(), null);
		mgc.drawImage(wall_hp, skill4.getX(), skill4.getY(), null);
		mgc.drawImage(wall_time, skill5.getX(), skill5.getY(), null);

		mgc.setFont(new Font("default", Font.BOLD, 14));
		mgc.drawString("A", skill1.getX() + 18, skill1.getY() + 62);
		mgc.drawString("S", skill2.getX() + 18, skill2.getY() + 62);
		mgc.drawString("D", skill3.getX() + 18, skill3.getY() + 62);
		mgc.drawString("F", skill4.getX() + 18, skill4.getY() + 62);
		mgc.drawString("G", skill5.getX() + 18, skill5.getY() + 62);

		mgc.drawString(String.valueOf(dataManager.getCost(0)) + "P", skill1.getX(), skill1.getY() - 2);
		mgc.drawString(String.valueOf(dataManager.getCost(1)) + "P", skill2.getX(), skill2.getY() - 2);
		mgc.drawString(String.valueOf(dataManager.getCost(2)) + "P", skill3.getX(), skill3.getY() - 2);
		mgc.drawString(String.valueOf(dataManager.getCost(3)) + "P", skill4.getX(), skill4.getY() - 2);
		mgc.drawString(String.valueOf(dataManager.getCost(4)) + "P", skill5.getX(), skill5.getY() - 2);

		if (dataManager.getCoolTimeLeft() != 0)
		{
			mgc.setFont(new Font("default", Font.PLAIN, 12));
			mgc.drawString(String.valueOf(dataManager.getCoolTimeLeft()), skill1.getX() + 18, skill1.getY() - 13);
		}
	}

	private void drawSkillMessageText()
	{
		Skill skill5 = dataManager.getSkill(4);

		if (isAppearMessage())
		{
			// mgc.setColor(Color.red);
			mgc.setFont(new Font("default", Font.BOLD, 20));
			mgc.drawString(messageText, skill5.getX() + 180, skill5.getY() + 10);
			if (getCostMessageText() != null)
				mgc.drawString(getCostMessageText(), skill5.getX() + 180, skill5.getY() + 38);
		}
		else
		{
			initCostMessageText();
		}

		if (dataManager.getGameLevel().getMode() == 0)
		{
			String ntext = "left wave : "
					+ String.valueOf(dataManager.getScenario().getPatterns() - dataManager.getGameLevel().getCumulativePartternIndex());
			mgc.setFont(new Font("default", Font.BOLD, 20));
			mgc.drawString(ntext, skill5.getX() + 180, skill5.getY() + 60);
		}
	}

	private void drawAlarmText()
	{
		mgc.setColor(Color.black);
		mgc.setFont(new Font("default", Font.PLAIN, 12));
		for (AlarmText at : dataManager.getAlarmList())
		{
			mgc.drawString(at.getText(), at.getFX(), at.getFY());
		}
	}

	private void pauseScreen()
	{
		if (gameManager.getNowState() instanceof PauseState)
		{
			mgc.setColor(Color.black);
			mgc.fillRect(screenWidth / 2 - 150, screenHeight / 2 - 100, 300, 200);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 70));
			mgc.setColor(Color.white);
			mgc.drawString("PAUSE", screenWidth / 2 - 115, screenHeight / 2 - 20);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 30));
			mgc.drawString("Return Main Menu?", screenWidth / 2 - 135, screenHeight / 2 + 30);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
			mgc.drawString("Press the Esc Key", screenWidth / 2 - 110, screenHeight / 2 + 60);

			mgc.setFont(new Font("default", Font.PLAIN, 12));
		}

		if (gameManager.getNowState() instanceof CountDownState)
		{
			int number_time = 3 - GameManager.getInstance().getCountdownTime();
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 150));
			mgc.setColor(Color.red);
			mgc.drawString(String.valueOf(number_time), screenWidth / 2 - 40, screenHeight / 2);

		}
	}

	private void menuScreen()
	{
		if (gameManager.getNowState() instanceof MenuState)
		{
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 85));
			mgc.setColor(Color.black);
			mgc.drawString("Last War", screenWidth / 2 - 180, 160);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Tutorial", gui_x, gui_y1 - 70);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Story Mode", gui_x, gui_y1);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Never Ending Mode", gui_x, gui_y2);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Exit", gui_x, gui_y3);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 30));
			mgc.setColor(Color.red);
			mgc.drawString("Press the SpaceBar Or Enter", screenWidth / 2 - 230, 750);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 45));
			mgc.setColor(Color.red);

			switch (InputManager.getInstance().getSelectIndex())
			{
			case -1:
				mgc.drawString("��", gui_x - 60, gui_y1 - 70);
				break;
			case 0:
				mgc.drawString("��", gui_x - 60, gui_y1);
				break;
			case 1:
				mgc.drawString("��", gui_x - 60, gui_y2);
				break;
			case 2:
				mgc.drawString("��", gui_x - 60, gui_y3);
				break;
			}
			mgc.setFont(new Font("default", Font.PLAIN, 12));
		}
	}

	private void deadScreen()
	{
		if (gameManager.getNowState() instanceof DeadState)
		{
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 80));

			mgc.setColor(Color.red);
			mgc.drawString("Game Over", screenWidth / 2 - 180, 220);

			mgc.setColor(Color.red);
			mgc.drawString("Score : " + String.valueOf(dataManager.getScore()), screenWidth / 2 - 330, 390);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 55));
			mgc.setColor(Color.red);
			mgc.drawString("REPLAY?", screenWidth / 2 - 330, 510);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 40));
			mgc.drawString("Press the SpaceBar Or Enter", screenWidth / 2 - 330, 590);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 55));
			mgc.drawString("Return Main Menu?", screenWidth / 2 - 330, 690);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 40));
			mgc.drawString("Press the Esc Key", screenWidth / 2 - 330, 760);
		}
	}

	private void storyScreen()
	{
		if (gameManager.getNowState() instanceof StoryState)
		{
			String[] text = new String[13];
			text[0] = "������ �긯���� ����� �������̴�.";
			text[1] = "�״� �پ �ؾ��� ������ �Ӵ��� �ڽ��� �Ͽ� ������ ������ �ֱ⿡ ���� ������� �׸� �����̶� �ҷ���.";
			text[2] = "����� �� �Ǽ�ȸ�翡�� �׿��� ���� �ñ�� �Ǵµ�";
			text[3] = "�긯���� �쿬ġ �ʰ� �Ǽ�ȸ�翡�� ��������� �񸮸� �������°��� �˰� �ȴ�.";
			text[4] = "�Ǽ�ȸ�翡���� �긯������ ������ ����ϸ� �񸮿� ���õ� ���� �Ա��� ���� ���������";
			text[5] = "�긯���� �ڽ��� �Ͽ� ���� ������ ������ �ֱ⿡ �̸� �����Ѵ�.";
			text[6] = "���� �긯���� �Ǽ�ȸ�翡�� ������ �񸮸� �����ϰڴٰ� �����ع�����.";
			text[7] = "�긯���� ���� ����鿡�� �����̶� �˷����ֱ� ������";
			text[8] = "�긯���� ���η� ���� �Ǽ�ȸ���� ���� ���ش� ������ ���̶� ����Ǿ���.";
			text[9] = "�Ǽ�ȸ��� �긯���� ���θ� ���� ���� �긯���� �ƹ��� �𸣰� ó���Ϸ� �Ѵ�.";
			text[10] = "�긯���� �Ǽ�ȸ�翡�� ���� �Ǽ� �κ����� ������ ���� �񸮸� �����Ϸ� �ϳ� ��Ȳ�� ����ġ �ʴ�.";
			text[11] = "��� ���� �긯���� �Ա� �ϳ��� �����ϴ� ������ ������ �κ����� ����鿩";
			text[12] = "��� óġ�� �� ���� �������� �񸮸� �����ϱ�� �����Ѵ�....";

			mgc.setColor(Color.black);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 20));

			for (int i = 0; i < text.length; i++)
			{
				mgc.drawString(text[i], 40, 100 + 40 * i);
			}

			mgc.setColor(Color.red);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
			mgc.drawString("Next?", 820, 700);
			mgc.drawString("Press SpaceBar Or Enter Key", 820, 750);
		}
	}

	private void stroyEndingScreen()
	{
		if (gameManager.getNowState() instanceof StoryEndState)
		{
			String[] text = new String[9];

			text[0] = "�긯���� Ż�⿡ �����Ͽ���.";
			text[1] = "Ż�⿡ ������ �긯���� �Ǽ�ȸ���� �񸮸� �����ϴµ� �����Ͽ� �Ǽ�ȸ��� ó���� �ް� �Ǿ���.";
			text[2] = "�׷��� ������ �񸮸� ������ �Ϳ� ���� �ʹ����� ���� ������ ���� �ȴ�.";
			text[3] = "�긯���� �׸� �����ϴ� ������� �δ��� ó���̸�";
			text[4] = "�Ǽ�ȸ�縦 ��ü Ȥ�� ������ ���ݰ� å���ڸ� ������ ���� ���� �䱸������";
			text[5] = "���δ� �׵��� ��û�� ������� �ʴ´�.";
			text[6] = "���� �긯���� �Ǽ�ȸ�簡 ó���� �޵��� ����ϳ� �ð��� ������ ���� ������� ������ ����������";
			text[7] = "�Ǽ�ȸ���� �񸮴� ������ ���� �ǹ����� ����.";
			text[8] = "�̿� ȯ���� ���� �긯���� �ܱ����� ������.";

			mgc.setColor(Color.black);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 20));

			for (int i = 0; i < text.length; i++)
			{
				mgc.drawString(text[i], 40, 150 + 40 * i);
			}

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 55));
			mgc.drawString("Return Main Menu?", screenWidth / 2 - 330, 690);

			mgc.setFont(new Font("TimesRoman", Font.BOLD, 40));
			mgc.drawString("Press the Esc Key", screenWidth / 2 - 330, 760);
		}
	}

	private void tutorialScreen()
	{
		if (gameManager.getNowState() instanceof TutorialState)
		{
			mgc.setColor(Color.red);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
			mgc.drawString("Next?", 770, 700);
			mgc.drawString("Press SpaceBar Or Enter Key", 770, 750);

			mgc.drawString("Return Menu?", 550, 700);
			mgc.drawString("Press Esc Key", 550, 750);

			switch (InputManager.getInstance().getTutorialPage())
			{
			case 0:
				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 35));
				mgc.drawString("�⺻ ���� ����", 100, 100);

				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("ĳ���� �̵� : Ű���� ����Ű", 100, 200);
				mgc.drawString("��ų ��� �� ��ų ��ȭ : A, S, D, F, G", 100, 300);
				mgc.drawString("���� �Ͻ� ���� : Space Bar", 100, 400);
				mgc.drawString("���� �޴��� �ٽ� ���ư��� : Space Bar�� ���� �� esc", 100, 500);
				break;
			case 1:
				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 35));
				mgc.drawString("��ų ����", 80, 100);

				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));

				mgc.drawString("���� ��ġ : ���� ��ġ �� ������ ������ �ٽ� �� �� ����.", 150, 160);
				mgc.drawString("������ ������ ��� �����ϳ� �� AI�� ������� �հ� ������", 280, 195);
				mgc.drawLine(150, 200, 150, 530);

				mgc.drawString("ü�� ȸ�� : �÷��̾��� ��� ü���� ȸ���Ѵ�. ����� �� ���� ��� ����", 225, 250);
				mgc.drawLine(215, 270, 215, 530);

				mgc.drawString("�ִ� ü�� ���� : �÷��̾��� �ִ� ü���� ������Ų��. ����� �� ���� ��� ����", 265, 310);
				mgc.drawLine(270, 330, 270, 530);

				mgc.drawString("���� �ִ� ü�� ���� : ���� ������ �������� ����. ����� �� ���� ��� ����", 320, 380);
				mgc.drawLine(325, 400, 325, 530);

				mgc.drawString("���� ���� ���ð� ���� : ����� �� ���� ��� ����", 375, 450);
				mgc.drawLine(380, 470, 380, 530);

				mgc.drawImage(t_skill, 80, 550, null);
				break;
			case 2:
				mgc.drawImage(t_02, 80, 80, null);

				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("�������� ���������� ���� ȭ��ǥ���� ���´�.", 100, 600);
				break;
			case 3:
				mgc.drawImage(t_03, 80, 80, null);

				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("�������� �Ķ������� ���ϸ� �÷��̾�� ���� �� AI���� ���ظ� �ش�.", 100, 600);
				break;
			case 4:
				mgc.drawImage(t_05, 80, 80, null);

				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("�� AI�� ���� ���� �ȿ� ���� �÷��̾ ����ٴϸ� ", 100, 600);
				mgc.drawString("�÷��̾ ���� �ۿ� �ִٸ� ���������� ���ظ� ���� �ʴ´�. ", 100, 630);
				break;
			case 5:
				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 35));
				mgc.drawString("�� AI", 80, 100);

				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("�����ǰ� ���� �ð��� ������ �������� �Ѵ�.", 80, 200);
				mgc.drawString("�������� �ϸ� �̵��ӵ��� �����ϰ� �÷��̾ ���� �Ҽ� �ִ� ������ �о�����.", 80, 270);

				mgc.drawImage(t_06, 80, 400, null);
				break;
			case 6:
				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 35));
				mgc.drawString("���� ���� ����", 80, 100);

				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("���ظ� �ѹ� ������ ���� �ð� ���� �������� �ε����� ���ظ� ���� �ʴ´�.", 80, 200);
				mgc.drawString("�Ķ��� �������� �ε��� ��츸 ���ظ� �޴´�.", 80, 270);
				mgc.drawString("�� AI�� �������� �޸� ���� �ȿ� �ִ� �÷��̾ �����Ѵ�.", 80, 340);
				mgc.drawString("�� �Ʒ� �̹����� ���� ���� �ȿ� �ִ� ���¿��� �� AI�� �Ÿ��� �дٸ� ���� ���� �ʴ´�.", 80, 410);

				mgc.drawImage(brick_black, 200, 500, null);
				mgc.drawImage(playerImage, 175, 500, null);

				mgc.drawImage(excavator_001, 250, 500, null);
				break;
			case 7:
				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 35));
				mgc.drawString("��� ����", 80, 100);

				mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
				mgc.drawString("���丮 ��� : �÷��̾��� ���� �ܿ� ������ ���� �����Ѵ�.", 80, 200);
				mgc.drawString("�ٸ� �� AI���� �Ŵ��� AI�� ������ ���̺� �ȿ� óġ�ϸ� ������ ������.", 237, 250);
				mgc.drawString("������ ���̺갡 ��� ����Ǹ� �÷��̾�� ����ϰ� �ȴ�.", 237, 300);

				mgc.drawString("�׹����� ��� : ������ ���� �������� �ʴ´�.", 80, 400);
				break;
			default:
				mgc.setColor(Color.black);
				mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
				mgc.drawString("End", screenWidth / 2 - 40, screenHeight / 2 - 100);
				break;
			}
		}
	}

	private void warpGate()
	{

		float wx = dataManager.getWarpGate().x;
		float wy = dataManager.getWarpGate().y;

		AffineTransform t = new AffineTransform();
		t.translate(wx, wy); // x/y set here
		t.scale(1, 1);

		if (dataManager.getWarpGate().isOpen())
		{
			mgc.drawImage(open_door, t, null);
		}
		else
		{
			mgc.drawImage(closed_door, t, null);
		}
	}

	public void initSkillMessage()
	{
		skillMessageTextDuration = 0;
	}

	public void setSkillMessageText(String text)
	{
		switch (text)
		{
		case "cool":
			messageText = "Cooldown Time";
			break;
		case "point":
			messageText = "Not enough point";
			break;
		case "heal":
			messageText = "Full hp";
			break;
		case "heal_ok":
			messageText = "Hp heal";
			break;
		case "hp_ok":
			messageText = "Hp Max Up";
			break;
		case "wall_hp_ok":
			messageText = "Wall Max Hp Up";
			break;
		case "wall_cool_ok":
			messageText = "Wall Cooldown reduce";
			break;
		}

		skillMessageTextDuration = skillMessageTextMaxDuration;
	}

	public void setCostMessageText(int value)
	{
		costMessageText = "Cost " + String.valueOf(value) + " Up";
	}

	private String getCostMessageText()
	{
		return costMessageText;
	}

	private boolean initCostMessageText()
	{
		costMessageText = null;
		return true;
	}

	public boolean isAppearMessage()
	{
		if (skillMessageTextDuration > 0)
		{
			return true;
		}
		return false;
	}

	public int getSkillMessageTextDuration()
	{
		return skillMessageTextDuration;
	}

	public boolean addSkillMessageTextDuration(int value)
	{
		skillMessageTextDuration += value;
		return true;
	}
}