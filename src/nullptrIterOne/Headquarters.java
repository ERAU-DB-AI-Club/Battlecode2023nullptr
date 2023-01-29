package nullptrIterOne;

import java.util.LinkedList;
import java.util.Queue;

import battlecode.common.*;

public class Headquarters extends Behavior {
	RobotController rc;
	Queue<RobotType> bill;
	
	public Headquarters(RobotController rc) {
		this.rc = rc;
		bill = new LinkedList<RobotType>();
	}

	@Override
	void takeTurn() {
		while (true) {
			try {
				
				if (rc.getRoundNum() > 200 && rc.getNumAnchors(Anchor.STANDARD) == 0 && !(bill.contains(RobotType.HEADQUARTERS))) {
					bill.add(RobotType.HEADQUARTERS);
				}
				else if (rc.getRoundNum() % 100 == 0) {
					bill.add(RobotType.AMPLIFIER);
				}
				
				MapLocation spawn = rc.adjacentLocation(getRandomDirection());
				if (!(bill.isEmpty())) {
					if (bill.peek() == RobotType.HEADQUARTERS) {
						rc.setIndicatorString("Trying to build an Anchor");
						if (rc.canBuildAnchor(Anchor.ACCELERATING)) {
							rc.buildAnchor(Anchor.ACCELERATING);
							bill.poll();
						}
						else if (rc.canBuildAnchor(Anchor.STANDARD)) {
							rc.buildAnchor(Anchor.STANDARD);
							bill.poll();
						}
					} else {
						rc.setIndicatorString("Trying to build a " + bill.peek());
						if (rc.canBuildRobot(bill.peek(), spawn)) {
							rc.buildRobot(bill.poll(), spawn);
						}
					}
				}
				else if (rc.canBuildRobot(RobotType.CARRIER, spawn) && rc.getRoundNum() % 2 == 0) { 
					rc.buildRobot(RobotType.CARRIER, spawn);
				} 
				else if (rc.canBuildRobot(RobotType.LAUNCHER, spawn)) {
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
