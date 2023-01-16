package nullptrIterOne;

import battlecode.common.*;

public class CarrierRobot extends MovingRobot {
	RobotController rc;
	MapLocation HomeBase;
	MapLocation PatrolPoint;
	
	public CarrierRobot(RobotController rc) {
		super(rc);
		this.rc = rc;
	}
	
	@Override
	void takeTurn() {
		while (true) {
			try {
				if (HomeBase == null) {
					HomeBase = rc.adjacentLocation(Direction.SOUTHEAST);		
				}
				for (int i = 0; (rc.getRoundNum() % 5 == 0) && rc.canWriteSharedArray(0, 0) && i < 64; i++) {
					if ((rc.readSharedArray(i) & 0b1110000000000100) == 0b0110000000000000) {
						int toPublish = rc.readSharedArray(i) | 0b0000000000000100;
						rc.writeSharedArray(i, toPublish);
						PatrolPoint = new MapLocation(((rc.readSharedArray(i) & 0b0001111100000000) >> 8), ((rc.readSharedArray(i) & 0b0000000011111000) >> 3));
						setTargetLoc(PatrolPoint);
						rc.setIndicatorString("Going to caravan point x: " + PatrolPoint.x + ", y: " + PatrolPoint.y);
						i = 64;
					}
				}
				MapLocation me = rc.getLocation();
		        for (int dx = -1; dx <= 1; dx++) {
		            for (int dy = -1; dy <= 1; dy++) {
		                MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
		                if (rc.canCollectResource(wellLocation, -1)) {
		                    if (rng.nextBoolean()) {
		                        rc.collectResource(wellLocation, -1);
		                        rc.setIndicatorString("Collecting, now have, AD:" + 
		                            rc.getResourceAmount(ResourceType.ADAMANTIUM) + 
		                            " MN: " + rc.getResourceAmount(ResourceType.MANA) + 
		                            " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
		                    }
		                }
		            }
		        }
		        int weight = (rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR));
				if (PatrolPoint == null) {
					rc.setIndicatorString("Exploring");
					explore();
					WellInfo[] searchResults = rc.senseNearbyWells();
					if (searchResults.length > 0) {
						PatrolPoint = searchResults[0].getMapLocation();
						setTargetLoc(PatrolPoint);
						rc.setIndicatorString("Mining " + searchResults[0].getResourceType().toString());
					}
				} else {
				    if (weight > 30) {
			        	rc.setIndicatorString("Going Back to Base.");
			        	setTargetLoc(HomeBase);
				    } 
				    if (weight == 0) {
				    	setTargetLoc(PatrolPoint);
				    	//TODO: Directional, instead of just at the location.
					} else if (rc.canTransferResource(HomeBase, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM)))  {
						rc.transferResource(HomeBase, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));
					} else if (rc.canTransferResource(HomeBase, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA)))  {
						rc.transferResource(HomeBase, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
					} else if (rc.canTransferResource(HomeBase, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR)))  {
						rc.transferResource(HomeBase, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
					}
					moveTo();
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