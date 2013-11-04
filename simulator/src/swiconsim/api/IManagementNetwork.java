package swiconsim.api;

import swiconsim.messages.Message;
import swiconsim.controller.Controller;
import swiconsim.node.Node;

/**
 * @author praveen
 *
 * Interface to be implemented by management network
 */
public interface IManagementNetwork {
	/**
	 * register a switch on management network
	 * 
	 * @param id
	 * @param sw
	 */
	void registerNode(long id, Node node);

	/**
	 * 
	 * register a controller on management network
	 * @param id
	 * @param cont
	 */
	void registerController(long id, Controller cont);

	/**
	 * notify controller
	 * @param msg
	 */
	void sendNotificationToController(Message msg);

	/**
	 * notify a switch
	 * @param msg
	 */
	void sendNotificationToSwitch(Message msg);
}
