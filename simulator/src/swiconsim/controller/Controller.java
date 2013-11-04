package swiconsim.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import swiconsim.messages.Message;
import swiconsim.api.IController;
import swiconsim.api.IControllerSouthBound;
import swiconsim.api.IControlPlane;
import swiconsim.flow.Flow;
import swiconsim.host.Host;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.node.Node;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Controller
 * 
 */
public class Controller extends Node implements IControlPlane, IController,
		IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	List<Long> nodes;
	ControllerSouthBound csb;

	public Controller(long id, long cid) {
		this(id);
		registerWithController(cid);
	}

	public Controller(long id) {
		super(id);
		this.id = id;
		nodes = new ArrayList<Long>();
		csb = new ControllerSouthBound(id, nodes, this);
		registerWithMgmtNetAsController();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void addFlow(Flow flow) {
		for (Long swid : nodes) {
			addFlowToSwitch(swid, flow);
		}
	}

	@Override
	public void removeFlow(Flow flow) {
		for (Long swid : nodes) {
			deleteFlowFromSwitch(swid, flow);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IControlPlane#sendPktInController(swiconsim.packet.Packet)
	 */
	@Override
	public void sendPktInController(Packet pkt) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IControlPlane#receiveNotificationFromController(swiconsim
	 * .messages.Message)
	 */
	@Override
	public void receiveNotificationFromController(Message msg) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IControllerSouthBound#receiveNotificationFromSwitch(swiconsim
	 * .messages.Message)
	 */
	@Override
	public void receiveNotificationFromSwitch(Message msg) {
		this.csb.receiveNotificationFromSwitch(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControllerSouthBound#addFlowToSwitch(long,
	 * swiconsim.flow.Flow)
	 */
	@Override
	public void addFlowToSwitch(long swid, Flow flow) {
		this.csb.addFlowToSwitch(swid, flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControllerSouthBound#deleteFlowFromSwitch(long,
	 * swiconsim.flow.Flow)
	 */
	@Override
	public void deleteFlowFromSwitch(long swid, Flow flow) {
		this.csb.deleteFlowFromSwitch(swid, flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IController#getTopology()
	 */
	@Override
	public Topology getTopology() {
		logger.info("Getting topology");
		Set<Node> nodes = new HashSet<Node>();
		Set<Host> hosts = new HashSet<Host>();
		Map<Long, Long> links = new HashMap<Long, Long>();
		Map<Long, Long> switchLinks = DataNetwork.getInstance().getLinks();
		Map<Long, Long> contLinks = ManagementNetwork.getInstance().getLinks();
		Map<Long, Node> allNodes = ManagementNetwork.getInstance().getNodeMap();
		for (long nodeId : this.nodes) {
			logger.info("Node : " + nodeId);
			Node node = allNodes.get(nodeId);
			nodes.add(node);
			for (Port port : node.getPorts()) {
				if (switchLinks.containsKey(port.getId())) {
					links.put(port.getId(), switchLinks.get(port.getId()));
				}
				else if (contLinks.containsKey(port.getId())) {
					links.put(port.getId(), contLinks.get(port.getId()));
				}
				if (port.getHost() != null) {
					hosts.add(port.getHost());
				}
			}
		}
		Topology topology = new Topology(nodes, links, hosts);
		return topology;
	}

	@Override
	public void registerWithMgmtNetAsController() {
		ManagementNetwork.getInstance().registerController(id, this);
	}

	@Override
	public Set<Port> getPorts() {
		Set<Port> ports = new HashSet<Port>();
		for (Long nodeId : this.nodes) {
			Set<Port> swPorts = ManagementNetwork.getInstance()
					.getNode(nodeId).getPorts();
			for (Port swPort : swPorts) {
				short portNum = (short) ports.size();
				Port port = new Port(swPort);
				port.setId(PortUtil.calculatePortId(id, portNum));
				this.addPort(portNum, port);
			}
		}	
		return ports;
	}

}
