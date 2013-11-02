package swiconsim.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import swiconsim.messages.Message;
import swiconsim.api.IController;
import swiconsim.api.IControllerSouthBound;
import swiconsim.api.ISwitchControlPlane;
import swiconsim.flow.Flow;
import swiconsim.messages.MessageType;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

/**
 * @author praveen
 *
 * Controller
 * 
 */
public class Controller implements ISwitchControlPlane, IController,
		IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	long id;
	List<Long> switches;

	public Controller(long id) {
		super();
		this.id = id;
		switches = new ArrayList<Long>();
		registerWithMgmtNet();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void addFlow(Flow f) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFlow(Flow f) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Port> getPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendPktInController(Packet pkt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerWithController(long cid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveNotificationFromSwitch(Message msg) {
		switch (msg.getType()) {
		case HELLO:
			long switchId = (Long) msg.getPayload();
			logger.info(this.id + "Hello from " + switchId);
			switches.add(switchId);
			break;
		default:
			break;
		}
	}

	@Override
	public void receiveNotificationFromController(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFlowToSwitch(long swid, Flow flow) {
		Message msg = new Message(swid, MessageType.OFPFC_ADD, flow, this.id);
		ManagementNetwork.getInstance().sendNotificationToSwitch(msg);
	}

	@Override
	public void deleteFlowFromSwitch(long swid, Flow flow) {
		Message msg = new Message(swid, MessageType.OFPFC_DELETE, flow, this.id);
		ManagementNetwork.getInstance().sendNotificationToSwitch(msg);
	}

	@Override
	public void registerWithMgmtNet() {
		ManagementNetwork.getInstance().registerController(id, this);
	}

	@Override
	public Topology getTopology() {
		Set<Switch> switches = new HashSet<Switch>();
		switches.addAll(DataNetwork.getInstance().getSwMap().values());
		Topology topology = new Topology(switches, new HashMap<Long, Long>(DataNetwork
				.getInstance().getLinks()));
		return topology;

	}
}
