package pack_001;

final class LaserArrow
{
	private int indexX, indexY;
	private boolean exist = false;

	LaserArrow(int x, int y)
	{
		indexX = x;
		indexY = y;
	}

	public int getIndexX()
	{
		return indexX;
	}

	public int getIndexY()
	{
		return indexY;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof LaserArrow))
			return false;
		if (obj == this)
			return true;

		LaserArrow lar = (LaserArrow) obj;

		return (indexX == lar.indexX && indexY == lar.indexY);
	}

	public boolean isExist()
	{
		return exist;
	}

	public void setExist(boolean mod)
	{
		exist = mod;
	}
}