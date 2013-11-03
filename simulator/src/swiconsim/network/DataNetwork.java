package swiconsim.network;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import swiconsim.api.IDataNetwork;
import swiconsim.node.Node;
import swiconsim.nwswitch.Switch;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Data network - hosts and switches
 */
public class DataNetwork implements IDataNetwork {
	private static DataNetwork instance = null;
	Map<Long, Node> nodeMap;
	Map<Long, Long> links;
	private static Logger logger = Logger.getLogger("sim:");

	public Map<Long, Node> getSwMap() {
		return nodeMap;
	}

	public Map<Long, Long> getLinks() {
		return links;
	}

	protected DataNetwork() {
		nodeMap = new HashMap<Long, Node>();
		links = new HashMap<Long, Long>();
	}

	public static DataNetwork getInstance() {
		if (instance == null) {
			instance = new DataNetwork();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#registerSwitch(long,
	 * swiconsim.nwswitch.Switch)
	 */
	@Override
	public void registerSwitch(long id, Switch sw) {
		this.nodeMap.put(id, sw);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#addEdge(long, short, long, short)
	 */
	@Override
	public void addEdge(long swid1, short portNum1, long swid2, short portNum2) {
		this.addEdge(PortUtil.calculatePortId(swid1, portNum1),
				PortUtil.calculatePortId(swid2, portNum2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#addEdge(long, long)
	 */
	@Override
	public void addEdge(long portId1, long portId2) {
		this.links.put(portId1, portId2);
		this.links.put(portId2, portId1);
	}

	public String toString() {
		String ret = "Data Network:\nSwitches: ";
		for (long id : nodeMap.keySet()) {
			ret += "S-" + id + ", ";
		}
		ret += "\nLinks:\n";
		for (Long portId1 : links.keySet()) {
			ret += portId1 + "\t" + links.get(portId1) + "\n";
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#handlePkt(swiconsim.packet.Packet, long)
	 */
	@Override
	public void handlePkt(Packet pkt, long portId) {
		if (links.containsKey(portId)) {
			long destPortId = links.get(portId);
			long switchId = PortUtil.getSwitchIdFromPortId(destPortId);
			short in_port = PortUtil.getPortNumFromPortId(destPortId);
			if (nodeMap.containsKey(switchId)) {
				Node node = nodeMap.get(switchId);
				if (node instanceof Switch) {
					Switch sw = (Switch) node;
					sw.receivePkt(pkt, in_port);
					logger.info("Pkt" + pkt.toString() + " : " + portId
							+ " -> " + destPortId);
				}
			} else {
				logger.warning("Switch doesn't exist : pkt dropped");
			}
		} else {
			logger.warning("No Link : pkt dropped");
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#pushPkt(swiconsim.packet.Packet, long)
	 */
	@Override
	public void pushPkt(Packet pkt, long portId) {
		long switchId = PortUtil.getSwitchIdFromPortId(portId);
		short in_port = PortUtil.getPortNumFromPortId(portId);
		if (nodeMap.containsKey(switchId)) {
			Node node = nodeMap.get(switchId);
			if (node instanceof Switch) {
				Switch sw = (Switch) node;
				sw.receivePkt(pkt, in_port);
				logger.info("Pkt" + pkt.toString() + " : " + portId + " -> "
						+ portId);
			}
		} else {
			logger.warning("No Link : pkt dropped");
		}
	}
}