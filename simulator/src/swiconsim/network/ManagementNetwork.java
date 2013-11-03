package swiconsim.network;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import swiconsim.messages.Message;
import swiconsim.api.IDataNetwork;
import swiconsim.api.IManagementNetwork;
import swiconsim.controller.Controller;
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
	Map<Long, Switch> swMap;
	Map<Long, Controller> contMap;
	private static Logger logger = Logger.getLogger("sim:");

	Map<Long, Long> links;
	
	protected ManagementNetwork() {
		swMap = new HashMap<Long, Switch>();
		contMap = new HashMap<Long, Controller>();
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
	public void registerSwitch(long id, Switch sw) {
		this.swMap.put(id, sw);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IManagementNetwork#registerController(long,
	 * swiconsim.controller.Controller)
	 */
	@Override
	public void registerController(long id, Controller cont) {
		this.contMap.put(id, cont);
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
		if (contMap.containsKey(cid)) {
			contMap.get(cid).receiveNotificationFromSwitch(msg);
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
		if (swMap.containsKey(swid)) {
			swMap.get(swid).receiveNotificationFromController(msg);
		} else {
			logger.warning("Switch not found - " + swid);
		}
	}

	/**
	 * @param cid
	 * @return controller with given id
	 */
	public Controller getController(long cid) {
		return contMap.get(cid);
	}

	/**
	 * @param swid
	 * @return switch with given id
	 */
	public Switch getSwitch(long swid) {
		return swMap.get(swid);
	}

	@Override
	public void addEdge(long swid1, short portNum1, long swid2, short portNum2) {
		this.addEdge(PortUtil.calculatePortId(swid1, portNum1),
				PortUtil.calculatePortId(swid2, portNum2));
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
}