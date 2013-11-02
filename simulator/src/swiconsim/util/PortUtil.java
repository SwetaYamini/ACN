package swiconsim.util;

/**
 * @author praveen
 *
 * Used to map between a global portid and the (switchid & portNum)
 * Assuming numPorts per switch < 100000
 */
public class PortUtil {
	static int maxNumPorts = 100000;
	
	public static long calculatePortId(long swid, short portNum){
		return swid * maxNumPorts + portNum;
	}
	
	public static long getSwitchIdFromPortId(long portId){
		return portId/maxNumPorts;
	}
	
	public static short getPortNumFromPortId(long portId){
		return (short) (portId%maxNumPorts);
	}
}
