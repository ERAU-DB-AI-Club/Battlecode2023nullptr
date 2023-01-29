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
	private MapLocation mapLoc;
	private MapLocation curLoc;
	protected MapLocation HomeBase;
	
	public MovingRobot(RobotController rc) {
		this.rc = rc;
		try {
			RobotInfo[] searchResults = rc.senseNearbyRobots(2, rc.getTeam());
			for (int i = 0; HomeBase == null; i++) {
				if (searchResults[i].type == RobotType.HEADQUARTERS) {
					HomeBase = searchResults[i].location;
				}
			}
		} catch (GameActionException e) {
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();
        } 		
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
		return mapLoc.x;
	}

	public void setMapLocX(int mapLocX) {
		mapLoc = new MapLocation(mapLocX, mapLoc.y);
	}

	public int getMapLocY() {
		return mapLoc.y;
	}
	
	public void setTargetLoc(int x, int y) {
		mapLoc = new MapLocation(x,y);
	}
	
	public void setTargetLoc(MapLocation target) {
		mapLoc = target;
	}

	public void setMapLocY(int mapLocY) {
		mapLoc = new MapLocation(mapLoc.x, mapLocY);
	}
	
	public void moveTo() throws GameActionException {
		Direction targ = rc.getLocation().directionTo(mapLoc);
		if	(rc.canMove(targ)) {
			Direction currdir = rc.senseMapInfo(rc.adjacentLocation(targ)).getCurrentDirection();
			if (currdir == Direction.CENTER || currdir == targ) {
				rc.move(targ);
			}
			else {
				explore();
			}
		} 
		if (rc.getRoundNum() % 3 == 0 && targ != Direction.CENTER) {
			explore();
		}
	}
	
	public void moveTo(MapLocation target) throws GameActionException {
		setTargetLoc(target);
		moveTo();
	}
}