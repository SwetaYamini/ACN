package swiconsim.network;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import swiconsim.messages.Message;
import swiconsim.api.IDataNetwork;
import swiconsim.api.IManagementNetwork;
import swiconsim.controller.Controller;
import swiconsim.node.Node;
import swiconsim.nwswitch.Switch;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Network connecting controllers and switches
 *         Management network should also act as data network for inter-controller network
 * 
 */
public class ManagementNetwork implements IManagementNetwork, IDataNetwork {
	private static ManagementNetwork instance = null;
	Map<Long, Node> nodeMap;
	Map<Long, Controller> mgmtContMap;
	private static Logger logger = Logger.getLogger("sim:");

	Map<Long, Long> links;
	Map<Long, Long> virtualPortIdMap;
	
	public Map<Long, Long> getVirtualPortIdMap() {
		return virtualPortIdMap;
	}

	protected ManagementNetwork() {
		nodeMap = new HashMap<Long, Node>();
		mgmtContMap = new HashMap<Long, Controller>();
		links = new HashMap<Long, Long>();
		virtualPortIdMap = new HashMap<Long, Long>();
	}

	public static ManagementNetwork getInstance() {

		if (instance == null) {
			instance = new ManagementNetwork();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IManagementNetwork#registerSwitch(long,
	 * swiconsim.nwswitch.Switch)
	 */
	@Override
	public void registerNode(long id, Node node) {
		logger.info("Registering node: " + id);
		this.nodeMap.put(id, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IManagementNetwork#registerController(long,
	 * swiconsim.controller.Controller)
	 */
	@Override
	public void registerController(long id, Controller cont) {
		logger.info("Registering controller : " + id);
		this.mgmtContMap.put(id, cont);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IManagementNetwork#sendNotificationToController(swiconsim
	 * .messages.Message)
	 */
	@Override
	public void sendNotificationToController(Message msg) {
		logger.info(msg.getFrom() + "->" + msg.getTo() + " " + msg.getType());
		long cid = msg.getTo();
		if (mgmtContMap.containsKey(cid)) {
			mgmtContMap.get(cid).receiveNotificationFromSwitch(msg);
		} else {
			logger.warning("Controller not found - " + cid);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IManagementNetwork#sendNotificationToSwitch(swiconsim.messages
	 * .Message)
	 */
	@Override
	public void sendNotificationToSwitch(Message msg) {
		logger.info(msg.getFrom() + "->" + msg.getTo() + " " + msg.getType());
		long swid = msg.getTo();
		if (nodeMap.containsKey(swid)) {
			logger.info("Sending to " + swid);
			nodeMap.get(swid).receiveNotificationFromController(msg);
		} else {
			logger.warning("Switch not found - " + swid);
		}
	}

	/**
	 * @param cid
	 * @return controller with given id
	 */
	public Controller getController(long cid) {
		return mgmtContMap.get(cid);
	}

	/**
	 * @param nodeId
	 * @return switch with given id
	 */
	public Node getNode(long nodeId) {
		return nodeMap.get(nodeId);
	}

	@Override
	public void addEdge(long nodeId1, short portNum1, long nodeId2, short portNum2) {
		this.addEdge(PortUtil.calculatePortId(nodeId1, portNum1),
				PortUtil.calculatePortId(nodeId2, portNum2));
	}

	@Override
	public void addEdge(long portId1, long portId2) {
		this.links.put(portId1, portId2);
		this.links.put(portId2, portId1);
	}

	@Override
	public void handlePkt(Packet pkt, long portId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pushPkt(Packet pkt, long portId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Long, Node> getNodeMap() {
		return this.nodeMap;
	}

	@Override
	public Map<Long, Long> getLinks() {
		return this.links;
	}
}