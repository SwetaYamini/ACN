package swiconsim.api;

import java.util.Collection;

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
	void addFlow(Flow flow);

	/**
	 * remove
	 * 
	 * @param flow
	 */
	void removeFlow(Flow flow);

	/**
	 * @return
	 */
	Collection<Port> getPorts();

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
}