package nullptrIterOne;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public strictfp class RobotPlayer {
	Behavior agent;
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
    	try {
	        switch (rc.getType()) {
	            case HEADQUARTERS: break;
	            case CARRIER: agent = new CarrierRobot(); break;
	            case LAUNCHER: break;
	            case BOOSTER: break;
	            case DESTABILIZER: break;
	            case AMPLIFIER: break;
	        }
    	} catch (GameActionException e) {
            System.out.println("Illegal Action: " + rc.getType() + " Exception");
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Runtime Error: " + rc.getType() + " Exception");
            e.printStackTrace();

        }
    }
}