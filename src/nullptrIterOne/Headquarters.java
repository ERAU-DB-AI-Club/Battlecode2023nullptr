package nullptrIterOne;

import battlecode.common.*;

public class Headquarters extends Behavior {
	RobotController rc;
	
	public Headquarters(RobotController rc) {
		this.rc = rc;
	}

	@Override
	void takeTurn() {
		while (true) {
			try {
				MapLocation spawn = rc.adjacentLocation(getRandomDirection());
				if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getRoundNum() > 10) {
					rc.buildAnchor(Anchor.STANDARD);
				}
				else if (rc.canBuildRobot(RobotType.BOOSTER, spawn) && rc.getRoundNum() % 15 == 0 
						&& (rc.getRoundNum() < 200 || rc.getNumAnchors(Anchor.STANDARD) > 0)) {
					rc.buildRobot(RobotType.BOOSTER, spawn);
				}
				else if (rc.canBuildRobot(RobotType.CARRIER, spawn) && rc.getRoundNum() % 2 == 0
						&& (rc.getRoundNum() < 200 || rc.getNumAnchors(Anchor.STANDARD) > 0)) { 
					rc.buildRobot(RobotType.CARRIER, spawn);
				} 
				else if (rc.canBuildRobot(RobotType.LAUNCHER, spawn) && 
						(rc.getRoundNum() < 200 || rc.getNumAnchors(Anchor.STANDARD) > 0)) {
					rc.buildRobot(RobotType.LAUNCHER, spawn);
				}
			} catch (GameActionException e) {
	            System.out.println(rc.getType() + " Exception");
	            e.printStackTrace();
	        } catch (Exception e) {
	            System.out.println(rc.getType() + " Exception");
	            e.printStackTrace();
	        } finally {
	        	Clock.yield();
	        }
		}
	}
}
