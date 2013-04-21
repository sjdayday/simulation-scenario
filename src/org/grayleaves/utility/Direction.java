package org.grayleaves.utility;

public enum Direction
{
	ASCENDING(1), DESCENDING(-1);  
	
	private int direction;

	private Direction(int direction)
	{
		this.direction = direction;
	}
	public int getDirection()
	{
		return direction; 
	}
}
