package nullptrSubTwo;

import java.util.Map;

import javax.naming.ldap.HasControls;

import battlecode.common.Anchor;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.ResourceType;
import battlecode.common.RobotController;

public class ElixirCarrier extends CarrierRobot{
    
    //These will be gained from the bus
    MapLocation adamantiumWell;
    MapLocation manaWell;
    MapLocation elixerWell;
    boolean elixerWellExists;
    boolean hasCargo;



    public ElixirCarrier(RobotController rc) throws GameActionException{
        super(rc);
        //robot will look at bus to find adamantiumWell and manaWell
        hasCargo = false;
        if (HomeBase == null) {
            HomeBase = rc.adjacentLocation(Direction.SOUTHEAST);
        }
        for(int i = 0; i < 32; i++){
            
            // i is multiplied by 2 inorder to scale to 64 len array

            //below reads in adam/mana and elixer/ enemy points when availiable
            int in = rc.readSharedArray(i * 2);
            int x = (in & 0b0001111110000000) >> 7;
            int y = (in & 0b0000000001111110) >> 1;
            if((in & 0b1110000000000001) == 0b0010000000000001){
                adamantiumWell = new MapLocation(x,y);
            }
            if((in & 0b1110000000000001) == 0b0010000000000000){
                manaWell = new MapLocation(x,y);
            }
            in = rc.readSharedArray((i * 2) + 1);
            x = (in & 0b0001111110000000) >> 7;
            y = (in & 0b0000000001111110) >> 1;
            if((in & 0b1110000000000001) == 0b0100000000000001){

                elixerWell = new MapLocation(x,y);
                elixerWellExists = true;
            }
            elixerWellExists = false;
            

        }
        


    }

    @Override
    public void takeTurn(){
        while (true) {
			try {

                if(elixerWellExists){
                    collectElixerTurn();
                }else{
                    createElixerTurn();
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

    public void collectElixerTurn() throws GameActionException{

        if(hasCargo){

            if(rc.canTransferResource(HomeBase, ResourceType.ELIXIR, -1)){
                rc.transferResource(HomeBase, ResourceType.ELIXIR, -1);
                hasCargo = false;
            }else{
                moveTo(HomeBase);
            }
            
        }else{

            if(rc.canCollectResource(elixerWell, -1)){
                rc.collectResource(elixerWell, -1);
                hasCargo = true;
            }else{
                moveTo(elixerWell);
            }

        }
        

    }
    

    public void createElixerTurn() throws GameActionException{

        //for now just make mana into elixer well
        if(hasCargo){

            if(rc.canTransferResource(manaWell, ResourceType.ELIXIR, -1)){
                rc.transferResource(manaWell, ResourceType.ELIXIR, -1);
                hasCargo = false;
            }else{
                moveTo(manaWell);
            }
        }else{
            if(rc.canCollectResource(adamantiumWell, -1)){
                rc.collectResource(adamantiumWell, -1);
                hasCargo = true;
            }else{
                moveTo(adamantiumWell);
            }
        }        

    }


}
