package swiconsim.api;

import swiconsim.packet.Packet;

/**
 * @author praveen
 * Interface to be implemented by a switch's data plane 
 */
public interface ISwitchDataPlane {
	/**
	 * called when a pkt is received by a switch
	 * @param pkt
	 * @param in_port
	 * @return
	 */
	boolean receivePkt(Packet pkt, short in_port);
	/**
	 * @param pkt
	 * @param out_port
	 */
	
	/**
	 * send out a pkt on the specified out-port
	 * @param pkt
	 * @param out_port
	 */
	void sendPkt(Packet pkt, short out_port);
}
