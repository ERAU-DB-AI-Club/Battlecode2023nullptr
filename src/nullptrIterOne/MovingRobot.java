package nullptrIterOne;

import battlecode.common.*;

public class MovingRobot extends Behavior{
	
	private RobotController rc;
	private int mapLocX;
	private int mapLocY;
	
	private MapLocation curLoc;
	
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

	public void setMapLocY(int mapLocY) {
		this.mapLocY = mapLocY;
	}

	public MovingRobot(RobotController rc) {
		this.rc = rc;
		
	}
	
	public void takeTurn() {
		curLoc = rc.getLocation();
		try {
			if(curLoc.x > mapLocX) {
				rc.move(Direction.WEST);
			}
			if(curLoc.x < mapLocX) {
				rc.move(Direction.EAST);
			}
			if(curLoc.y > mapLocY) {
				rc.move(Direction.SOUTH);
			}
			if(curLoc.y < mapLocY) {
				rc.move(Direction.NORTH);
			}
		}catch (Exception e) {
			System.out.println("uhh oh spagiety ooos");
		}
		
	}
}