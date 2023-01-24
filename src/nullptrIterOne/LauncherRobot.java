package nullptrIterOne;

import battlecode.common.*;

enum PatrolMode {
	OPEN,
	GUARD
}

public class LauncherRobot extends MovingRobot{
	RobotController rc;
	PatrolMode myState;
	
	public LauncherRobot(RobotController rc) {
		super(rc);
		this.rc = rc;
		myState = PatrolMode.OPEN;
	}
	
	@Override
	void takeTurn() {
		while (true) {
			try {
				switch (myState) {
				case OPEN:
					int radius = rc.getType().actionRadiusSquared;
			        Team opponent = rc.getTeam().opponent();
			        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
			        if (enemies.length > 0) {
			        	MapLocation toAttack = enemies[0].location;
			            if (rc.canAttack(toAttack)) {
			                rc.setIndicatorString("Attacking");        
			                rc.attack(toAttack);
			            }
			        }
			        explore();
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
