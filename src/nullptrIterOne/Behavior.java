package nullptrIterOne;

import battlecode.common.*;

public abstract class Behavior {
	abstract void takeTurn();
	
	Direction[] dirArray = {Direction.EAST, Direction.NORTH, Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTH, Direction.SOUTHEAST,
			Direction.SOUTHWEST, Direction.WEST};
	
	public Direction getRandomDirection() {
		return dirArray[(int) Math.floor(Math.random() * 8)];
	}
}