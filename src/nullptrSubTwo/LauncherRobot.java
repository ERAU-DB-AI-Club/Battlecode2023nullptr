package nullptrSubTwo;

import battlecode.common.*;

public class LauncherRobot extends MovingRobot{
	RobotController rc;
	
	public LauncherRobot(RobotController rc) {
		super(rc);
		this.rc = rc;	
	}
	
	@Override
	void takeTurn() {
		while (true) {
			try {
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
