package swiconsim.api;

import swiconsim.nwswitch.Switch;
import swiconsim.packet.Packet;

/**
 * @author praveen
 * 
 * Interface to be implemented by the data network
 * 
 */

public interface IDataNetwork {
	/**
	 * register a switch on data network
	 * 
	 * @param id
	 * @param sw
	 */
	void registerSwitch(long id, Switch sw);

	/**
	 * 
	 * add an edge by switchid+portid
	 * 
	 * @param swid1
	 * @param portNum1
	 * @param swid2
	 * @param portNum2
	 */
	void addEdge(long swid1, short portNum1, long swid2, short portNum2);

	/**
	 * add an edge by portid
	 * 
	 * @param portId1
	 * @param portId2
	 */
	void addEdge(long portId1, long portId2);

	/**
	 * pkt coming into data network through portid
	 * 
	 * @param pkt
	 * @param portId
	 */
	void handlePkt(Packet pkt, long portId);

	/**
	 * send a pkt on portid
	 * 
	 * @param pkt
	 * @param portId
	 */
	void pushPkt(Packet pkt, long portId);

}
