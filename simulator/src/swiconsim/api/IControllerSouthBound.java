package swiconsim.api;

import swiconsim.messages.Message;
import swiconsim.flow.Flow;

/**
 * @author praveen
 *
 * Interface to be implemented by controller to talk to switches
 */
public interface IControllerSouthBound {
	/**
	 * @param msg
	 */
	void receiveNotificationFromSwitch(Message msg);
	
	/**
	 * @param swid
	 * @param flow
	 */
	void addFlowToSwitch(long swid, Flow flow);
	/**
	 * @param swid
	 * @param flow
	 */
	void deleteFlowFromSwitch(long swid, Flow flow);
	/**
	 * register controller on management network
	 */
	void registerWithMgmtNet();
}
