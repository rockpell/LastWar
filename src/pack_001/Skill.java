package pack_001;

class Skill extends UI
{
	Skill(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void skillExcute()
	{
		if (!(GameManager.getInstance().getNowState() instanceof ProgressState))
		{
			return;
		}
	}

	public void skillClick(int x, int y)
	{ // 마우스 클릭시 스킬 실행
		if (pointCheck(x, y))
		{
			skillExcute();
		}
	}

	private boolean pointCheck(int x, int y)
	{ 
		if (this.x < x && x < this.x + this.width)
		{
			if (this.y < y && y < this.y + this.height)
			{
				return true;
			}
		}
		return false;
	}
}

final class WallSkill extends Skill
{
	WallSkill(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

	@Override
	public void skillExcute()
	{
		super.skillExcute();
		DataManager dm = DataManager.getInstance();

		if (dm.getCost(0) > dm.getMoney())
		{
			Screen.getInstance().setSkillMessageText("point");
			return;
		}

		if (dm.getCoolTimeLeft() == 0 && dm.getPlayer().isWallAble())
		{
			if (!dm.getPlayer().getOutTrigger())
			{
				dm.addWall(dm.getPlayer().getPosition().x, dm.getPlayer().getPosition().y);
				dm.subMoney(dm.getCost(0));
			}
			dm.getPlayer().setOutTrigger(true);
			dm.initCoolTime();
		}
		else if (dm.getCoolTimeLeft() != 0)
		{
			Screen.getInstance().setSkillMessageText("cool");
		}
	}
}

final class HealSkill extends Skill
{
	HealSkill(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void skillExcute()
	{
		super.skillExcute();
		DataManager dm = DataManager.getInstance();

		if (dm.getCost(1) > dm.getMoney())
		{
			Screen.getInstance().setSkillMessageText("point");
			return;
		}
		else if (dm.getPlayer().getHp() == dm.getPlayer().getMaxHp())
		{
			Screen.getInstance().setSkillMessageText("heal");
			return;
		}

		int cost1 = dm.getCost(1);

		dm.getPlayer().heal();
		dm.subMoney(cost1);
		dm.upCost(1);
		Screen.getInstance().setSkillMessageText("heal_ok");
		Screen.getInstance().setCostMessageText(dm.getCost(1) - cost1);
	}
}

final class HpSkill extends Skill
{
	HpSkill(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void skillExcute()
	{
		super.skillExcute();
		DataManager dm = DataManager.getInstance();

		if (dm.getCost(2) > dm.getMoney())
		{
			Screen.getInstance().setSkillMessageText("point");
			return;
		}

		int cost1 = dm.getCost(2);

		dm.getPlayer().maxHpUp();
		dm.subMoney(cost1);
		dm.upCost(2);
		Screen.getInstance().setSkillMessageText("hp_ok");
		Screen.getInstance().setCostMessageText(dm.getCost(2) - cost1);
	}
}

final class WallHpSkill extends Skill
{
	public WallHpSkill(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

	@Override
	public void skillExcute()
	{
		super.skillExcute();
		DataManager dm = DataManager.getInstance();

		if (dm.getCost(3) > dm.getMoney())
		{
			Screen.getInstance().setSkillMessageText("point");
			return;
		}

		int cost1 = dm.getCost(3);

		dm.subMoney(cost1);
		dm.upCost(3);
		dm.plusHp();
		Screen.getInstance().setSkillMessageText("wall_hp_ok");
		Screen.getInstance().setCostMessageText(dm.getCost(3) - cost1);
	}
}

final class WallCoolSkill extends Skill
{
	public WallCoolSkill(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

	@Override
	public void skillExcute()
	{
		super.skillExcute();
		DataManager dm = DataManager.getInstance();

		if (dm.getCost(4) > dm.getMoney())
		{
			Screen.getInstance().setSkillMessageText("point");
			return;
		}

		int cost1 = dm.getCost(4);

		dm.subMoney(cost1);
		dm.upCost(4);
		dm.minusWallCoolTime(1);
		Screen.getInstance().setSkillMessageText("wall_cool_ok");
		Screen.getInstance().setCostMessageText(dm.getCost(4) - cost1);
	}
}