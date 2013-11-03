package swiconsim.controller;

import java.util.ArrayList;
import java.util.Collection;
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
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

/**
 * @author praveen
 * 
 *         Controller
 * 
 */
public class Controller implements IControlPlane, IController,
		IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	long id;
	List<Long> switches;
	ControllerControlPlane ccp;
	ControllerSouthBound csb;

	public Controller(long id) {
		super();
		this.id = id;
		switches = new ArrayList<Long>();
		// ccp = new ControllerControlPlane(id);
		csb = new ControllerSouthBound(id, switches, this);
		registerWithMgmtNet();
	}

	@Override
	public long getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControlPlane#addFlow(swiconsim.flow.Flow)
	 */
	@Override
	public void addFlow(Flow flow) {
		this.ccp.addFlow(flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControlPlane#removeFlow(swiconsim.flow.Flow)
	 */
	@Override
	public void removeFlow(Flow flow) {
		this.ccp.removeFlow(flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControlPlane#getPorts()
	 */
	@Override
	public Collection<Port> getPorts() {
		// TODO Auto-generated method stub
		return null;
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
	 * @see swiconsim.api.IControlPlane#registerWithController(long)
	 */
	@Override
	public void registerWithController(long cid) {
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
	 * @see swiconsim.api.IControllerSouthBound#registerWithMgmtNet()
	 */
	@Override
	public void registerWithMgmtNet() {
		this.csb.registerWithMgmtNet();
	}

	/* (non-Javadoc)
	 * @see swiconsim.api.IControllerSouthBound#addFlowToSwitch(long, swiconsim.flow.Flow)
	 */
	@Override
	public void addFlowToSwitch(long swid, Flow flow) {
		this.csb.addFlowToSwitch(swid, flow);
	}

	/* (non-Javadoc)
	 * @see swiconsim.api.IControllerSouthBound#deleteFlowFromSwitch(long, swiconsim.flow.Flow)
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
		Set<Switch> switches = new HashSet<Switch>();
		Set<Host> hosts = new HashSet<Host>();
		Map<Long, Long> links = new HashMap<Long, Long>();
		Map<Long, Long> allLinks = DataNetwork.getInstance().getLinks();
		Map<Long, Switch> allSwitches = DataNetwork.getInstance().getSwMap();
		for (long swid : this.switches) {
			Switch sw = allSwitches.get(swid);
			switches.add(sw);
			for (Port port : sw.getPorts()) {
				if (allLinks.containsKey(port.getId())) {
					links.put(port.getId(), allLinks.get(port.getId()));
				}
				if (port.getHost() != null) {
					hosts.add(port.getHost());
				}
			}
		}
		Topology topology = new Topology(switches, links, hosts);
		return topology;
	}

	
}
