package pack_001;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

class Enemy extends Colider implements Unit
{
	private String type_name = "enemy";
	private float x, y;
	private float dx, dy;
	private int width, height;
	private float speed = 1.5f;
	private int hp = 5, maxHp = 5;
	private int money = 80;
	private int swidth = 1100, sheight = 600;
	private boolean isMoveUp = false, isMoveDown = false, isMoveLeft = false, isMoveRight = false;
	private boolean randMove = true; // randMove == 랜덤 이동(플레이어 인식 못할 경우 랜덤 이동)
	private boolean damageable = false, damaged = false; // damageable == 플레이어 인식 시에만 데미지 입음, damaged == 피해 입음 상태 표시
	private boolean is_active = false; // 유닛 활성화 여부
	private Point2D.Float movePoint = null;
	private int rand_number = 0, direction = 0, damage_count = 0, active_count = 0;
	private int alive_time = 0, level = 0;
	private float vision = 200.0f;

	private DataManagement dm;

	Enemy(int hp)
	{
		dm = DataManagement.getInstance();

		setHp(hp);

		setPosition(566.0f, 350.0f);
		setSize(48, 48);

		dm.addEnemy(this);
		dm.getWarpGate().setOpen();
	}

	public void move(Point2D.Float target)
	{
		movePoint = target;

		if (target.x > this.x)
		{
			dx = speed;
		}
		else if (target.x < this.x)
		{
			dx = -speed;
		}
		else
		{
			dx = 0;
		}

		if (target.y > this.y)
		{
			dy = speed;
		}
		else if (target.y < this.y)
		{
			dy = -speed;
		}
		else
		{
			dy = 0;
		}

		if (dx != 0 && dy != 0)
		{
			dx = (float) (dx / Math.sqrt(2));
			dy = (float) (dy / Math.sqrt(2));
		}
	}

	private void randomMove()
	{
		rand_number += 1;

		switch (direction)
		{ // enemy의 이동 방향
		case 0: // left
			dx = -speed;
			dy = 0;
			break;
		case 1: // right
			dx = speed;
			dy = 0;
			break;
		case 2: // up
			dy = -speed;
			dx = 0;
			break;
		case 3: // down
			dy = speed;
			dx = 0;
			break;
		}

		if (rand_number > 100)
		{
			directionChange();
			rand_number = 0;
		}
	}

	private void directionChange()
	{
		direction = (int) (Math.random() * 3);
	}

	private void calDistance()
	{
		Point2D.Float target = DataManagement.getInstance().getPlayer().getPosition();
		float x1 = (float) Math.pow((this.x + (this.width / 2) - target.x), 2);
		float x2 = (float) Math.pow((this.y + (this.height / 2) - target.y), 2);

		if (Math.sqrt(x1 + x2) < vision)
		{
			randMove = false;
			damageable = true;
		}
		else
		{
			randMove = true;
			damageable = false;
		}

	}

	private void levelManage()
	{
		if (alive_time % 300 == 0)
		{
			vision += 10.0f;
			speed += 0.1f;
			level += 1;

			AlarmText at = new AlarmText(this.x, this.y);
			at.setText("Level Up");
			dm.addAlaram(at);
		}
	}

	@Override
	public void work()
	{
		alive_time += 1;
		levelManage();

		if (!is_active)
		{
			active_count += 1;
			if (active_count > 20)
			{
				is_active = true;
			}
			return;
		}

		calDistance();

		if (damaged)
		{
			damage_count += 1;
			if (damage_count > 50)
			{
				damage_count = 0;
				damaged = false;
			}
		}

		if (x - speed < movePoint.x && movePoint.x < x + speed)
		{
			dx = 0;
		}

		if (y - speed < movePoint.y && movePoint.y < y + speed)
		{
			dy = 0;
		}

		if (!randMove)
		{
			move(DataManagement.getInstance().getPlayer().getPosition());
		}
		else
		{
			randomMove();
		}

		if (isMoveRight)
		{
			x -= speed;
			isMoveRight = false;
		}
		if (isMoveLeft)
		{
			x += speed;
			isMoveLeft = false;
		}
		if (isMoveUp)
		{
			y += speed;
			isMoveUp = false;
		}
		if (isMoveDown)
		{
			y -= speed;
			isMoveDown = false;
		}

		if (x + dx > 50 && x + dx < swidth)
		{
			x += dx;
		}
		else if (x + dx < 50 || x + dx < swidth)
		{
			directionChange();
		}

		if (y + dy > 80 && y + dy < sheight)
		{
			y += dy;
		}
		else if (y + dy < 80 || y + dy > sheight)
		{
			directionChange();
		}

		if (x <= 50)
		{
			x += speed;
		}
		if (x >= swidth)
		{
			x -= speed;
		}
		if (y >= sheight)
		{
			y -= speed;
		}
		if (y <= 80)
		{
			y += speed;
		}
	}

	public void checkMoveable(Rectangle2D.Float target)
	{
		if ((x + cx) + cwidth >= target.x && x + cx < target.x && y + cy < target.y + target.height
				&& y + cy + cheight > target.y)
		{ // 물체를 기준으로 왼쪽에서 충돌
			isMoveRight = true;
		}
		if (x + cx <= target.x + target.width && (x + cx + cwidth) > target.x + target.width
				&& y + cy < target.y + target.height && y + cy + cheight > target.y)
		{ // 물체 오른쪽에 충돌
			isMoveLeft = true;
		}
		if (y + cy < target.y && y + cy + cwidth >= target.y && x + cx < target.x + target.width
				&& x + cx + cwidth > target.x)
		{ // 물체 위쪽에 충돌
			isMoveDown = true;
		}
		if (y + cy <= target.y + target.height && y + cy + cheight > target.y + target.height
				&& x + cx < target.x + target.width && x + cx + cwidth > target.x)
		{ // 물체 아래쪽에 충돌
			isMoveUp = true;
		}
	}

	@Override
	public void dead()
	{
		AlarmText _at = new AlarmText(this.x, this.y);
		_at.setText("P", money);
		dm.addAlaram(_at);
		dm.addScore(money);
		dm.addMoney(money);
		dm.removeEnemy(this);
	}

	public void damaged()
	{
		if (!damaged)
		{
			hp -= 1;
			damaged = true;
			if (hp <= 0)
			{
				dead();
			}
		}
	}

	public void addVision(float value)
	{
		vision += value;
	}

	public float getVision()
	{
		return vision;
	}

	public void addSpeed(float value)
	{
		speed += value;
	}

	public void setHp(int val)
	{
		this.maxHp = val;
		this.hp = val;
	}

	protected void setMoeny(int value)
	{
		this.money = value;
	}

	@Override
	public int getHp()
	{
		// TODO Auto-generated method stub
		return hp;
	}

	public int getMaxHp()
	{
		return maxHp;
	}

	@Override
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
		setBox(0, 0, width, height);
	}

	@Override
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		movePoint = new Point2D.Float(x, y);
	}

	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		movePoint = new Point2D.Float(x, y);
	}

	public void setTypeName(String name)
	{
		type_name = name;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	@Override
	public Float getBounds()
	{
		// TODO Auto-generated method stub
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	@Override
	public boolean collision(Rectangle2D.Float target)
	{
		if (!is_active)
		{
			return false;
		}

		return target.intersects(this.getBounds());
	}

	public boolean getRandMove()
	{
		return randMove;
	}

	public boolean getDamageable()
	{
		return damageable;
	}

	public boolean isActive()
	{
		return is_active;
	}

	public String getTypeName()
	{
		return type_name;
	}

	public int getMoney()
	{
		return money;
	}

	public int getLevel()
	{
		return level;
	}

	public int getDamageCount()
	{
		return damage_count;
	}
}