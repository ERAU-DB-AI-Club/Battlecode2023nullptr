package nullptrIterOne;

import java.util.Random;

import battlecode.common.*;

public class MovingRobot extends Behavior{
	Random rng = new Random(6147);
    Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };
	
	public MovingRobot() {

	}
	
	@Override 
	void takeTurn() {
	}
	
	void explore(RobotController rc) throws GameActionException {	
	    Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
	}
}