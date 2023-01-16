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
						PatrolPoint = new MapLocation(((rc.readSharedArray(i) & 0b0001111100000000) >> 8), ((rc.readSharedArray(i) & 0b0000000011111000) >> 3));
						int toPublish = rc.readSharedArray(i) | 0b0000000000000100;
						rc.writeSharedArray(i, toPublish);
						break;
					}
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