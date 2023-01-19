package nullptrIterOne;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public strictfp class RobotPlayer {
	static Behavior agent;
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
    	try {
	        switch (rc.getType()) {
	            case HEADQUARTERS: agent = new Headquarters(rc); break;
	            case CARRIER: agent = new CarrierRobot(rc); break;
	            case LAUNCHER: agent = new LauncherRobot(rc); break;
	            case BOOSTER: break;
	            case DESTABILIZER: break;
	            case AMPLIFIER: break;
	        }
	        agent.takeTurn();
    	} catch (Exception e) {
            System.out.println("Runtime Error: " + rc.getType() + " Exception");
            e.printStackTrace();

        }
    }
}