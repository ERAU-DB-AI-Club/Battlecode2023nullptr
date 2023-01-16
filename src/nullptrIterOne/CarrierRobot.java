package nullptrIterOne;

import battlecode.common.*;

public class CarrierRobot extends MovingRobot {
	RobotController rc;
	MapLocation HomeBase;
	MapLocation PatrolPoint;
	
	public CarrierRobot(RobotController rc) {
		this.rc = rc;
	}
	
	@Override
	void takeTurn() {
		while (true) {
			try {
				for (int i = 0; (rc.getRoundNum() % 5 == 0) && i < 64; i++) {
					if ((rc.readSharedArray(i) & 0b1110000000000100) == 0b0110000000000000) {
						int toPublish = rc.readSharedArray(i) | 0b0000000000000100;
						rc.writeSharedArray(i, toPublish);
						PatrolPoint = new MapLocation(((rc.readSharedArray(i) & 0b0001111100000000) >> 8), ((rc.readSharedArray(i) & 0b0000000011111000) >> 3));
						rc.setIndicatorString("Going to caravan point x: " + PatrolPoint.x + ", y: " + PatrolPoint.y);
						i = 64;
					}
				}
				if (PatrolPoint == null) {
					rc.setIndicatorString("Exploring");
					explore(rc);
					WellInfo[] searchResults = rc.senseNearbyWells();
					if (searchResults.length > 0) {
						PatrolPoint = searchResults[0].getMapLocation();
						rc.setIndicatorString("Mining " + searchResults[0].getResourceType().toString());
					}
				} else {
					System.out.println("Patrol Point x: " + PatrolPoint.x + ", y: " + PatrolPoint.y);
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