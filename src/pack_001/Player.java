package pack_001;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

final class Player extends Colider implements Unit
{
	private float x, y;
	private float dx = 0, dy = 0;
	private int width, height;
	private int hp = 3, maxHp = 3;
	private int speed = 5;
	private int swidth = 1100, sheight = 600;
	private boolean isMoveUp = false, isMoveDown = false, isMoveLeft = false, isMoveRight = false;
	private boolean outTrigger = false, wall_able = false;
	private boolean damaged = false; // damaged == 피해 입음 상태 표시
	private int damage_count = 0;

	// up: y==-1, down: y==1, right: x==1, left: x==-1
	public void move(int x, int y)
	{
		if(x == 0 || y == 0)
		{
			dy = speed*y;
			dx = speed*x;
		}
		else
		{
			float _sqrtSpeed = (float) (speed / Math.sqrt(2));
			dy = _sqrtSpeed * y;
			dx = _sqrtSpeed * x;
		}
	}

	@Override
	public void work()
	{
		if (damaged)
		{
			damage_count += 1;
			if (damage_count > 50)
			{
				damage_count = 0;
				damaged = false;
			}
		}

		if (x + dx > 50 && x + dx < swidth)
		{
			x += dx;
		}
		if (y + dy > 80 && y + dy < sheight)
		{
			y += dy;
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

	@Override
	public void dead()
	{
		DataManagement dm = DataManagement.getInstance();
		dm.getAudio().stop();

		GameManager.getInstance().ChageState(new DeadState());
	}

	@Override
	public int getHp()
	{
		return hp;
	}

	public int getMaxHp()
	{
		return maxHp;
	}

	public boolean isWallAble()
	{
		return wall_able;
	}

	public void setWallAble(boolean target)
	{
		wall_able = target;
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
	}

	public void setPosition(Point2D.Float point)
	{
		this.x = point.x;
		this.y = point.y;
	}

	public void setPosition(Point point)
	{
		this.x = point.x;
		this.y = point.y;
	}

	public Point2D.Float getPosition()
	{
		return new Point2D.Float(x, y);
	}

	public Rectangle2D.Float getBounds()
	{
		return new Rectangle2D.Float(x + cx, y + cy, cwidth, cheight);
	}

	public boolean collision(Rectangle2D.Float target)
	{
		return target.intersects(this.getBounds());
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

	public boolean getOutTrigger()
	{
		return outTrigger;
	}

	public void setOutTrigger(boolean value)
	{
		outTrigger = value;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public void heal()
	{
		hp = maxHp;
	}

	public void maxHpUp()
	{
		maxHp += 1;
	}

	public int getDamageCount()
	{
		return damage_count;
	}
}