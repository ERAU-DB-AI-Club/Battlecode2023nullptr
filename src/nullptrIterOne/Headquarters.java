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
				for (int i = 0; (rc.getRoundNum() % 6 == 0) && i < 64; i++) {
					int currNum = rc.readSharedArray(i);
					if ((currNum & 0b1110000000000001) == 0b0110000000000001) {
						int extMssg = rc.readSharedArray(i+1);
						if((extMssg & 0b0000000000000100) == 0) {
							MapInfo[] plotSearch = rc.senseNearbyMapInfos();
							int xinfo = 0;
							int yinfo = 0;
							for (int ii = plotSearch.length - 1; ii > 0; ii--) {
								if (plotSearch[ii].isPassable() && plotSearch[ii].getCurrentDirection() == Direction.CENTER && plotSearch[ii].hasCloud() == false) {
									xinfo = plotSearch[ii].getMapLocation().x;
									yinfo = plotSearch[ii].getMapLocation().y;
								}
							}
							rc.writeSharedArray(9, 0b0110000000000000 | (xinfo << 7) | (yinfo << 1));
							rc.writeSharedArray(10, 0b0000000000000100 | (extMssg & 0b0000000011111000));
							System.out.println("WriteE");
						}
					}
				}
				for (int i = 0; (rc.getRoundNum() % 5 == 0) && i < 64; i++) {
					int currNum = rc.readSharedArray(i);
					if ((currNum & 0b1110000000000000) == 0b0110000000000000) {
						int extMssg = rc.readSharedArray(i+1);
						if ((extMssg & 0b0000000100000000) == 0b0000000000000000) {
							if ((currNum & 0b0000000000000001) == 0b0000000000000000) {
								bill.add(RobotType.CARRIER);
							}
							if (Integer.bitCount(extMssg & 0b0000001000000000) > 0) {
								bill.add(RobotType.CARRIER);
							}
							if (Integer.bitCount(extMssg & 0b0000110000000000) > 0) {
								for (int ii = 0; ii < Integer.bitCount(extMssg & 0b0000110000000000); ii++) {
									if (rc.getResourceAmount(ResourceType.ELIXIR) > 1){
										bill.add(RobotType.DESTABILIZER);
									} else {
										bill.add(RobotType.LAUNCHER);
									}
								}
							}
							if (Integer.bitCount(extMssg & 0b1111000000000000) > 0) {
								for (int ii = 0; ii < Integer.bitCount(extMssg & 0b1111000000000000); ii++) {
									bill.add(RobotType.LAUNCHER);
								}
							}
							rc.writeSharedArray(i+1, (extMssg | 0b0000000100000000));
							System.out.println("WriteD");
						}
					}
				}
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
