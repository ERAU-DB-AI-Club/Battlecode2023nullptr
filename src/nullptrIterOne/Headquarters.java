package nullptrIterOne;

import battlecode.common.*;

public class Headquarters extends Behavior {
	RobotController rc;
	
	public Headquarters(RobotController rc) {
		this.rc = rc;
	}

	@Override
	void takeTurn() {
		int command = 0b011;
		command = command << 5;
		MapLocation mylocation = rc.getLocation();
		command = command | (mylocation.x + 2);
		command = command << 5; 
		command = command | (mylocation.y - 2);
		command = command << 3;
		while (true) {
			try {
				MapLocation spawn = rc.adjacentLocation(getRandomDirection());
				rc.writeSharedArray(rc.getID(), command);
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
