package swiconsim.network;

import java.util.HashMap;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import swiconsim.api.IDataNetwork;
import swiconsim.link.Link;
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
	public Map<Long, Link> links;
	private static Logger logger = Logger.getLogger("sim:");
	public Map<Packet, Integer> HopCount = new HashMap<Packet, Integer>();
	public Map<Packet, Double> UtilizationTracker = new HashMap<Packet, Double>();

	public Map<Long, Node> getNodeMap() {
		return nodeMap;
	}

	public Map<Long, Link> getLinks() {
		return links;
	}

	protected DataNetwork() {
		nodeMap = new HashMap<Long, Node>();
		links = new HashMap<Long, Link>();
	}

	public static DataNetwork getInstance() {
		if (instance == null) {
			instance = new DataNetwork();
		}
		return instance;
	}

	public boolean isLink(long portid1, long portid2){
		if(this.links.containsKey(portid1) && this.links.get(portid1).port2==portid2){
			return true;
		}
		return false;
		//return false;
	}
	
	public long getOtherPort(long portId){
		if(this.links.containsKey(portId)){
			return this.links.get(portId).port2;
		}
		return -1;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#registerSwitch(long,
	 * swiconsim.nwswitch.Switch)
	 */
	@Override
	public void registerNode(long id, Node node) {
		//System.out.println("Registering node " + id);
		this.nodeMap.put(id, node);
		//System.out.println(this.nodeMap.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#addEdge(long, short, long, short)
	 */
	@Override
	public void addEdge(long swid1, short portNum1, long swid2, short portNum2, int capacity) {
		this.addEdge(PortUtil.calculatePortId(swid1, portNum1),
				PortUtil.calculatePortId(swid2, portNum2), capacity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IDataNetwork#addEdge(long, long)
	 */
	@Override
	public void addEdge(long portId1, long portId2, int capacity) {
		Link link1 = new Link(portId1, portId2, capacity, 0);
		Link link2 = new Link(portId2, portId1, capacity, 0);
		this.links.put(portId1, link1);
		this.links.put(portId2, link2);
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
		//if(pkt.id > 600) System.out.println("Data Network: Got a packet with id > 600");
		if (links.containsKey(portId)) {
			Link link = links.get(portId);
			long destPortId = links.get(portId).port2;
			long switchId = PortUtil.getSwitchIdFromPortId(destPortId);
			long in_port = (destPortId);
			if (nodeMap.containsKey(switchId)) {
				Node node = nodeMap.get(switchId);
				if (node instanceof Switch) {
					Switch sw = (Switch) node;
					if(pkt.last==0){
						if(pkt.maxutil < link.utilization){
							pkt.maxutil = link.utilization;
						}
					}
					if(pkt.last==0){
						link.utilization += pkt.getSize();
					}else{
						link.utilization -= pkt.getSize();
					}
					long cid = nodeMap.get(PortUtil.getSwitchIdFromPortId(portId)).parent;
					ManagementNetwork.getInstance().getController(cid).updateUtilization(pkt);
					if(pkt.last==0){
						pkt.nhops++;
					}
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
	
	public void printUtilizations(){
		Iterator<Long> it = links.keySet().iterator();
		while(it.hasNext()){
			Link link = links.get(it.next());
			if(link.utilization!=0){
				System.out.print(link.port1 + "<->" + link.port2 + ":" + link.utilization + "\t");
			}
		}
		System.out.println();
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