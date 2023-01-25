package nullptrIterOne;

import battlecode.common.*;

enum PatrolMode {
	OPEN,
	FOLLOWING,
	RUSH,
	GUARD
}

public class LauncherRobot extends MovingRobot{
	RobotController rc;
	PatrolMode myState;
	int squadLeadID;
	int fails = 0;
	
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
