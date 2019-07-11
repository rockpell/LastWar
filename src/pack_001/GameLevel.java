package pack_001;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

final class GameLevel
{
	private GameManager gameManager;
	private DataManagement dm;

	private Map<String, ArrayList<String>> sequenceData;

	private int nowSequence = 0;
	private int targetIndex = 0, all_index = 0;
	private int mode_type = 0;
	private boolean levelUp = true, refresh = false, patternChange = false;
	private String patternName = null;

	public GameLevel(int type)
	{
		gameManager = GameManager.getInstance();
		dm = DataManagement.getInstance();

		mode_type = type;

		if (type == 0)
		{
			sequenceData = dm.getScenario().getSequenceData();
		}
		else if (type == 1)
		{
			sequenceData = dm.getScenario().getSequenceData2();
		}

		patternName = sequenceData.get("1").get(0);

		// patternParser(0);
	}

	public void init()
	{
		nowSequence = 0;
		targetIndex = 1;
		levelUp = true;
		refresh = false;
		patternChange = false;
		patternName = sequenceData.get("1").get(0);
	}

	public void levelStart()
	{
		if (levelUp)
		{
			if (nowSequence != 0)
				patternChange = true;

			nowSequence += 1;
			levelUp = false;

			System.out.println("nowSequence : " + nowSequence);
			if (nowSequence > sequenceData.size())
			{
				if (mode_type == 0)
				{
					dm.getPlayer().dead();
					return;
				}
				else
				{
					nowSequence = 1;
				}
			}
		}

		if (refresh)
		{
			refresh = false;
			patternChange = true;
		}

		if (patternChange)
		{
			ArrayList<String> tempList = sequenceData.get("" + nowSequence);
			patternChange = false;
			targetIndex += 1;
			all_index += 1;

			if (tempList.size() <= targetIndex)
			{
				targetIndex = -1;
				levelUp = true;
				return;
			}

			patternName = tempList.get(targetIndex);
			System.out.println("patternName : " + patternName);
			gameManager.refreshLevelStartTime();
		}

		patternParse();
	}

	private void patternParse()
	{
		Map<String, ArrayList<Point>> patternMap = null;
		int time = gameManager.getPlayTime();
		int itime = gameManager.getLevelStartTime();
		String timeText = "" + (time - itime);

		patternMap = dm.getScenario().getPatternData().get(patternName).getMap();

		if (patternMap.containsKey(timeText))
		{
			ArrayList<Point> tempList = patternMap.get(timeText);

			if (tempList.size() == 0)
			{
				refresh = true;
			}

			for (int i = 0; i < tempList.size(); i++)
			{
				Point tempPoint = tempList.get(i);
				if (tempPoint.x == 99 && tempPoint.y == 99)
				{
					new Enemy(3);
				}
				else if (tempPoint.x == 99 && tempPoint.y == 100)
				{
					new Enemy(4);
				}
				else if (tempPoint.x == 99 && tempPoint.y == 101)
				{
					new Enemy(5);
				}
				else if (tempPoint.x == 100 && tempPoint.y == 99)
				{
					new BossEnemy(0);
				}
				else
				{
					new Laser(tempPoint.x, tempPoint.y);
				}

			}
		}
	}

	public int getMode()
	{
		return mode_type;
	}

	public int getAllIndex()
	{
		return all_index;
	}
}