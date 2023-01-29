package nullptrIterOne; 
import battlecode.common.*;

public class AmplifierRobot extends MovingRobot{

    private MapLocation HomeBase;
    private RobotController rc;
    private int arrayInsert;

    public AmplifierRobot(RobotController rc) throws GameActionException{
        super(rc);
        this.rc = rc;
        if (HomeBase == null) {
            HomeBase = rc.adjacentLocation(Direction.SOUTHEAST);
        }
    } 

	@Override
	public void takeTurn() {
        while (true) {
			try {

                explore();
                
                WellInfo[] wells = rc.senseNearbyWells();
                for(int i = 0; i < wells.length; i++){
                    int out = 0;
                    
                    if(wells[i].getResourceType() == ResourceType.ADAMANTIUM){
                        out = (0b0010000000000001) | (wells[i].getMapLocation().x << 7) | (wells[i].getMapLocation().y << 1);
                    }
                    if(wells[i].getResourceType() == ResourceType.MANA){
                        out = (0b0010000000000000) | (wells[i].getMapLocation().x << 7) | (wells[i].getMapLocation().y << 1);
                    }
                    if(!isKnownWell(out) && rc.canWriteSharedArray(arrayInsert, out)){
                        rc.writeSharedArray(arrayInsert, out);
                        updateInsert();
                        System.out.println("WriteA");
                    }
                }
                
                int sightRad = rc.getType().actionRadiusSquared;
                int[] scan = rc.senseNearbyIslands();
                for(int i = 0; i < scan.length; i++) {
                	if (rc.senseTeamOccupyingIsland(scan[i]) != rc.getTeam()) {
                		if(!isKnownIsland(scan[i])) {
                			rc.writeSharedArray(arrayInsert, 0b01100000000001);
                			rc.writeSharedArray(arrayInsert + 1, 
                					0b1111111100000000 | (scan[i] << 3));
                			updateInsert();
                			System.out.println("WriteB");
                		}
                	}
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

    public boolean isKnownWell(int w) throws GameActionException{

        for(int i = 0; i < 32; i++){
            if(w == rc.readSharedArray(i * 2)){
                return true;
            }
        }
        return false;
    }
    
    public void updateInsert() throws GameActionException {
    	int i = 0;
        while(i < 32 && rc.readSharedArray(i * 2) != 0){
            i++;
        }
        for (int ii = 0; ii < 64; ii++) {
			int currNum = rc.readSharedArray(i);
			System.out.println(currNum);
		}
        arrayInsert = (i * 2 > 63) ? 0 : i * 2;
    }
    
    public boolean isKnownIsland(int l) throws GameActionException {
    	for(int i = 0; i < 64; i++) {
    		if((rc.readSharedArray(i) & 0b1110000000000000) == 0b0110000000000000) {
	    		if((l << 3) == (rc.readSharedArray(i+1) & 0b0000000011111000)) {
	    			return true;
	    		}
    		}
    	}
    	return false;
    }
    
}