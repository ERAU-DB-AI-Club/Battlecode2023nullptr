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

        int i = 0;
        while(i < 32 && rc.readSharedArray(i * 2) != 0){
            i++;
        }
        arrayInsert = i * 2;
    } 

	@Override
	public void takeTurn() {
        while (true) {
			try {

                explore();
                
                WellInfo[] wells = rc.senseNearbyWells();
                for(int i = 0;i < wells.length; i++){
                    int out = 0;
                    
                    if(wells[i].getResourceType() == ResourceType.ADAMANTIUM){
                        out = (0b0010000000000001) | (wells[i].getMapLocation().x << 7) | (wells[i].getMapLocation().y << 1);
                    }
                    if(wells[i].getResourceType() == ResourceType.MANA){
                        out = (0b0010000000000000) | (wells[i].getMapLocation().x << 7) | (wells[i].getMapLocation().y << 1);
                    }
                    if(!isKnownWell(out) && rc.canWriteSharedArray(arrayInsert, out)){
                        rc.writeSharedArray(arrayInsert, out);
                        arrayInsert += 2;
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
    
}