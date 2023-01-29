package nullptrIterOne;

import battlecode.common.*;

enum PatrolMode {
	OPEN,
	FOLLOWING,
	RUSH,
	GUARD,
	CARAVAN
}

public class LauncherRobot extends MovingRobot{
	RobotController rc;
	PatrolMode myState;
	int squadLeadID;
	int fails = 0;
	MapLocation expectCarrier;
	int monitorID = 0;
	boolean MissionStart = false;
	
	public LauncherRobot(RobotController rc) {
		super(rc);
		this.rc = rc;
		myState = PatrolMode.OPEN;
	}
	
	@Override
	void takeTurn() {
		while (true) {
			try {
				int radius;
				Team opponent;
				RobotInfo[] enemies;
				switch (myState) {
				case OPEN:
					for (int i = 0; (rc.getRoundNum() % 5 == 0) && i < 64; i++) {
						int currNum = rc.readSharedArray(i);
						if ((currNum & 0b1110000000000001) == 0b0110000000000001) {
							rc.setIndicatorString("Reading Caravan Order...");
							int extMssg = rc.readSharedArray(i+1);
							if (Integer.bitCount(extMssg & 0b1111110000000000) < 6) {
								myState = PatrolMode.CARAVAN;
								i = 64;
							}
						}
					}
					radius = rc.getType().actionRadiusSquared;
			        opponent = rc.getTeam().opponent();
			        enemies = rc.senseNearbyRobots(radius, opponent);
			        if (enemies.length > 0) {
			        	MapLocation toAttack = enemies[0].location;
			            if (rc.canAttack(toAttack)) {
			                rc.setIndicatorString("Attacking");        
			                rc.attack(toAttack);
			            }
			        }
			        RobotInfo[] allies = rc.senseNearbyRobots(radius, opponent.opponent());
		        	for (int i = 0; i < allies.length; i++) {
		        		if (allies[i].ID < rc.getID() && allies[i].type == RobotType.LAUNCHER) {
		        			squadLeadID = allies[i].ID;
		        			myState = PatrolMode.FOLLOWING;
		        		}
		        	}
		        	if (rc.getRoundNum() % 50 == 12) {
		        		myState = PatrolMode.RUSH;
		        	}
			        explore();
					break;
				case FOLLOWING:
					radius = rc.getType().actionRadiusSquared;
			        opponent = rc.getTeam().opponent();
			        enemies = rc.senseNearbyRobots(radius, opponent);
			        if (enemies.length > 0) {
			        	MapLocation toAttack = enemies[0].location;
			            if (rc.canAttack(toAttack)) {
			                rc.setIndicatorString("Attacking");        
			                rc.attack(toAttack);
			            }
			        } 
			        if (rc.canSenseRobot(squadLeadID)) {
			        	Direction leadPoint = rc.getLocation().directionTo(rc.senseRobot(squadLeadID).location);
			        	if (rc.canMove(leadPoint)) {
			        		rc.move(leadPoint);
			        	} else if (rc.canMove(leadPoint.opposite())) {
			        		rc.move(leadPoint.opposite());
			        	}
			        } else {
			        	myState = PatrolMode.OPEN;
			        	explore();
			        }
					break;
				case RUSH:
					radius = rc.getType().actionRadiusSquared;
			        opponent = rc.getTeam().opponent();
			        enemies = rc.senseNearbyRobots(radius, opponent);
			        if (enemies.length > 0) {
			        	MapLocation toAttack = enemies[0].location;
			        	if (enemies[0].type == RobotType.HEADQUARTERS) {
			        		myState = PatrolMode.OPEN;
			        	}
			            if (rc.canAttack(toAttack)) {
			                rc.setIndicatorString("Attacking");        
			                rc.attack(toAttack);
			            }
			        }
			        Direction away = rc.getLocation().directionTo(HomeBase).opposite();
			        if (rc.canMove(away)) {
			        	rc.move(away);
			        	fails = 0; 
			        } else {
			        	fails++;
			        }
			        if (fails > 3) {
			        	myState = PatrolMode.OPEN;
			        }
					break;
				case GUARD:
					radius = rc.getType().actionRadiusSquared;
			        opponent = rc.getTeam().opponent();
			        enemies = rc.senseNearbyRobots(radius, opponent);
			        if (enemies.length > 0) {
			        	MapLocation toAttack = enemies[0].location;
			            if (rc.canAttack(toAttack)) {
			                rc.setIndicatorString("Attacking");        
			                rc.attack(toAttack);
			            }
			        }
					break;
				case CARAVAN:
					rc.setIndicatorString("Setting Up Caravan.");
					if(expectCarrier == null) {
						for (int i = 0; (rc.getRoundNum() % 5 == 0) && i < 64; i++) {
							int currNum = rc.readSharedArray(i);
							if ((currNum & 0b1110000000000001) == 0b0110000000000001) {
								int extMssg = rc.readSharedArray(i+1);
								if (Integer.bitCount(extMssg & 0b1111110000000000) > 1) {
									for(int ii = 0b0000010000000000; ii != 0b10000000000000000; ii = ii << 1) {
										if((ii & extMssg) == 0b0000000000000000) {
											rc.writeSharedArray(i + 1, ii | extMssg);
											expectCarrier = new MapLocation(
													(currNum & 0b0001111110000000) >> 7,
													(currNum & 0b0000000001111110) >> 1);
											setTargetLoc(expectCarrier);
										}
										ii =  0b10000000000000000;
									}
									i = 64;
								}	
							}
							if (i == 63) {
								myState = PatrolMode.OPEN;
							}
						}
					} else {
						radius = rc.getType().actionRadiusSquared;
				        opponent = rc.getTeam().opponent();
				        enemies = rc.senseNearbyRobots(radius, opponent);
				        if (enemies.length > 0) {
				        	MapLocation toAttack = enemies[0].location;
				            if (rc.canAttack(toAttack)) {
				                rc.setIndicatorString("Attacking");        
				                rc.attack(toAttack);
				            }
				        }
				        if (!MissionStart) {
							rc.setIndicatorString("Rallying at (" + expectCarrier.x + ", " + expectCarrier.y + ").");
							if (rc.getLocation().distanceSquaredTo(expectCarrier) < 2) {
								explore();
							} else {
								moveTo();
							}
							RobotInfo tryToSense = rc.senseRobotAtLocation(expectCarrier);
							if (tryToSense.type == RobotType.CARRIER) {
								if (tryToSense.ID == monitorID) {
									MissionStart = true;
								} else {
									monitorID = tryToSense.ID;
								}
							}
				        } else {
				        	rc.setIndicatorString("Following Caravan Leader!");
				        	if (rc.canSenseRobot(monitorID)) {
				        		setTargetLoc(rc.senseRobot(monitorID).location);
				        	} else {
				        		fails++;
				        		explore();
				        		if (fails > 8) {
				        			myState = PatrolMode.GUARD;
				        		}
				        	}
				        }
					}
					break;
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
