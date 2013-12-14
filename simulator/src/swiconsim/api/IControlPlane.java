package swiconsim.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import swiconsim.messages.Message;
import swiconsim.flow.Flow;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

/**
 * @author praveen
 *
 * Interface to be implemented by a control plane
 */
public interface IControlPlane {
	/**
	 * @return
	 */
	long getId();

	/**
	 * add
	 * 
	 * @param flow
	 */
	void addFlow(Flow flow, Packet packet);

	/**
	 * remove
	 * 
	 * @param flow
	 */
	void removeFlow(Flow flow);

	/**
	 * @return
	 */
	List<Port> getPorts();
	
	/**
	 * @param portNum
	 * @param port
	 */
	void addPort(long portNum, Port port);

	/**
	 * forward an in-pkt to controller
	 * 
	 * @param pkt
	 */
	void sendPktInController(Packet pkt);

	/**
	 * connect to a controller with given id
	 * 
	 * @param cid
	 *            controller id
	 */
	void registerWithController(long cid);

	/**
	 * receive a msg from controller
	 * @param msg
	 */
	void receiveNotificationFromController(Message msg);
	
	/**
	 * 
	 */
	void registerWithMgmtNodeAsNode();
}