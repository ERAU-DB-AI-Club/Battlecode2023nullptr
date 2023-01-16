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
	private RobotController rc;
	private int mapLocX;
	private int mapLocY;
	private MapLocation curLoc;
	
	public MovingRobot(RobotController rc) {
		this.rc = rc;
	}
	
	@Override 
	void takeTurn() {
	}
	
	void explore() throws GameActionException {	
	    Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
	}
	
	
	public int getMapLocX() {
		return mapLocX;
	}

	public void setMapLocX(int mapLocX) {
		this.mapLocX = mapLocX;
	}

	public int getMapLocY() {
		return mapLocY;
	}
	
	public void setTargetLoc(int x, int y) {
		mapLocX = x;
		mapLocY = y;
	}
	
	public void setTargetLoc(MapLocation target) {
		mapLocX = target.x;
		mapLocY = target.y;
	}

	public void setMapLocY(int mapLocY) {
		this.mapLocY = mapLocY;
	}
	
	public void moveTo() throws GameActionException {
		curLoc = rc.getLocation();
		if(curLoc.x > mapLocX && rc.canMove(Direction.WEST)) {
			rc.move(Direction.WEST);
		}
		else if(curLoc.x < mapLocX && rc.canMove(Direction.EAST)) {
			rc.move(Direction.EAST);
		}
		if(curLoc.y > mapLocY && rc.canMove(Direction.SOUTH)) {
			rc.move(Direction.SOUTH);
		}
		else if(curLoc.y < mapLocY && rc.canMove(Direction.NORTH)) {
			rc.move(Direction.NORTH);
		}
		if (rc.getRoundNum() % 3 == 0) {
			explore();
		}
	}
	
	public void moveTo(MapLocation target) throws GameActionException {
		setTargetLoc(target);
		moveTo();
	}
}