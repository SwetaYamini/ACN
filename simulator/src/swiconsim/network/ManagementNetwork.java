package swiconsim.network;

import java.util.HashMap;
import swiconsim.node.ManagementNode;

import java.util.Map;
import java.util.logging.Logger;

import swiconsim.link.Link;
import swiconsim.messages.Message;
import swiconsim.messages.MessageType;
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
	Map<Long, ManagementNode> mgmtContMap;
	private static Logger logger = Logger.getLogger("sim:");
	public int SwitchToControllerCount=0;
	public int ControllerToSwitchCount=0;
	public int ControllerToControllerCount=0;
	public HashMap<Packet, Integer> MessageCount = new HashMap<Packet, Integer>();

	Map<Long, Link> links;
	Map<Long, Long> virtualPortIdMap;
	
	public Map<Long, Long> getVirtualPortIdMap() {
		return virtualPortIdMap;
	}

	protected ManagementNetwork() {
		nodeMap = new HashMap<Long, Node>();
		mgmtContMap = new HashMap<Long, ManagementNode>();
		links = new HashMap<Long, Link>();
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
	public void registerController(long id, ManagementNode cont) {
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
			SwitchToControllerCount++;
			if(msg.getType()==MessageType.PKT_IN){
				//if(msg.packet.id > 600) System.out.println("Management Net: Got a packet with id > 600");
				if(MessageCount.containsKey(msg.packet)){
					MessageCount.put(msg.packet, MessageCount.get(msg.packet)+1);
				}else{
					MessageCount.put(msg.packet, 1);
				}
			}
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
			ControllerToSwitchCount++;
			if(msg.getType()==MessageType.OFPFC_ADD){
				//System.out.println("Type: "+msg.getType() + " " + msg.packet);
				if(MessageCount.containsKey(msg.packet)){
					//System.out.println("Incrementing message count");
					MessageCount.put(msg.packet, MessageCount.get(msg.packet)+1);
				}else{
					MessageCount.put(msg.packet, 1);
					//System.out.println("Adding packet to MessageCount: " + MessageCount.size());
				}
			}
			nodeMap.get(swid).receiveNotificationFromController(msg);
		} else {
			logger.warning("Switch not found - " + swid);
		}
	}
	
	public void sendNotificationToPeer(Message msg){
		logger.info(msg.getFrom() + "->" + msg.getTo() + " " + msg.getType());
		long cid = msg.getTo();
		if (nodeMap.containsKey(cid)) {
			logger.info("Sending to " + cid);
			ControllerToControllerCount++;
			if(msg.getType()==MessageType.PEER_ADD || msg.getType()==MessageType.PEER_UPDATE){
				//System.out.println("Type: "+msg.getType() + " " + msg.packet);
				if(MessageCount.containsKey(msg.packet)){
					//System.out.println("Incrementing message count");
					MessageCount.put(msg.packet, MessageCount.get(msg.packet)+1);
				}else{
					MessageCount.put(msg.packet, 1);
					//System.out.println("Adding packet to MessageCount: " + MessageCount.size());
				}
			}
			mgmtContMap.get(cid).receiveNotificationFromPeer(msg);
		} else {
			logger.warning("Switch not found - " + cid);
		}
	}

	/**
	 * @param cid
	 * @return controller with given id
	 */
	public ManagementNode getController(long cid) {
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
	public void addEdge(long nodeId1, short portNum1, long nodeId2, short portNum2, int capacity) {
		this.addEdge(PortUtil.calculatePortId(nodeId1, portNum1),
				PortUtil.calculatePortId(nodeId2, portNum2), capacity);
	}

	@Override
	public void addEdge(long portId1, long portId2, int capacity) {
		Link link1 = new Link(portId1, portId2, capacity, 0);
		Link link2 = new Link(portId2, portId1, capacity, 0);
		this.links.put(portId1, link1);
		this.links.put(portId2, link2);
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
	
	public Map<Long, ManagementNode> getMgmtContMap(){
		return this.mgmtContMap;
	}

	@Override
	public Map<Long, Link> getLinks() {
		return this.links;
	}
}