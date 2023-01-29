package nullptrIterOne;

import battlecode.common.*;

public class CarrierRobot extends MovingRobot {
	RobotController rc;
	MapLocation PatrolPoint;
	boolean hasAnchor;
	boolean isCaravan;
	
	public CarrierRobot(RobotController rc) {
		super(rc);
		this.rc = rc;
		try {
			if (rc.canTakeAnchor(HomeBase, Anchor.STANDARD) && rc.getRoundNum() % 3 == 0) {
				rc.takeAnchor(HomeBase, Anchor.STANDARD);
				hasAnchor = true;
				for (int i = 0; i < 64; i++) {
					int currNum = rc.readSharedArray(i);
					if ((currNum & 0b1110000000000001) == 0b0110000000000000) {
						PatrolPoint = new MapLocation(
								(currNum & 0b0001111110000000) >> 7,
								(currNum & 0b0000000001111110) >> 1);
						setTargetLoc(PatrolPoint);
						isCaravan = true;
						rc.writeSharedArray(i, currNum | 0b0000000000000001);
						i = 64;
					}
				}
			}
		}catch (GameActionException e) {
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();
        }
	}
	
	@Override
	void takeTurn() {
		while (true) {
			try {
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
				
		        if (hasAnchor) {
		        	if (rc.canPlaceAnchor()) {
						rc.placeAnchor();
						hasAnchor = false;
		        	}
		        }
		        
		        if (hasAnchor && PatrolPoint == null) {
		        	rc.setIndicatorString("Trying to find an Island");
		        	explore();
		        	int[] searchResults = rc.senseNearbyIslands();
		        	if (searchResults.length > 0 && rc.senseAnchorPlantedHealth(searchResults[0]) < 0) {
		        		PatrolPoint = rc.senseNearbyIslandLocations(searchResults[0])[0];
		        		setTargetLoc(PatrolPoint);
		        		rc.setIndicatorString("Moving to an Island.");
		        	}
		        } else if (PatrolPoint == null) {
					rc.setIndicatorString("Exploring");
					explore();
					WellInfo[] searchResults = rc.senseNearbyWells();
					if (searchResults.length > 0) {
						PatrolPoint = searchResults[0].getMapLocation();
						setTargetLoc(PatrolPoint);
						rc.setIndicatorString("Mining " + searchResults[0].getResourceType().toString());
					}
				} else {
				    if (weight > 30 && !hasAnchor) {
			        	rc.setIndicatorString("Going Back to Base.");
			        	setTargetLoc(HomeBase);
				    } 
				    Direction pointAtBase = rc.getLocation().directionTo(HomeBase);
				    MapLocation toBase = rc.adjacentLocation(pointAtBase);
				    if (weight == 0) {
				    	setTargetLoc(PatrolPoint); 
				    	//TODO: Directional, instead of just at the location.
					} else if (rc.canTransferResource(toBase, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM)))  {
						rc.transferResource(toBase, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));
						rc.setIndicatorString("Transferring Adamantium " + pointAtBase);
					} else if (rc.canTransferResource(toBase, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA)))  {
						rc.transferResource(toBase, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
						rc.setIndicatorString("Transferring Mana " + pointAtBase);
					} else if (rc.canTransferResource(toBase, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR)))  {
						rc.transferResource(toBase, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
						rc.setIndicatorString("Transferring Elixir " + pointAtBase);
					}
				    if (isCaravan) {
				    	rc.setIndicatorString("Setting up a Caravan at (" + PatrolPoint.x + ", " + PatrolPoint.y + ")");
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