package swiconsim.nwswitch;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import swiconsim.messages.Message;
import swiconsim.api.ISwitchControlPlane;
import swiconsim.flow.Flow;
import swiconsim.messages.MessageType;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

/**
 * @author praveen
 *
 * Control Plane of a switch
 *
 */
public class SwitchControlPlane implements ISwitchControlPlane {
	long id;
	private HashMap<Short, Port> ports;
	private FlowTable flowTable;
	long cid;
	private static Logger logger = Logger.getLogger("sim:");
	
	public SwitchControlPlane(long id, HashMap<Short, Port> ports,
			FlowTable flowTable) {
		super();
		this.id = id;
		this.ports = ports;
		this.flowTable = flowTable;
	}

	@Override
	public long getId() {
		return this.id;
	}
	
	@Override
	public void addFlow(Flow f) {
		flowTable.addFlowEntry(f);
	}
	
	@Override
	public void removeFlow(Flow f) {
		flowTable.removeFlowEntry(f);
	}
	
	@Override
	public Collection<Port> getPorts() {
		return ports.values();
	}

	@Override
	public void sendPktInController(Packet pkt) {
		Message pktIn = new Message(cid, MessageType.PKT_IN, pkt, this.id);
		ManagementNetwork.getInstance().sendNotificationToController(pktIn);
	}

	@Override
	public void registerWithController(long cid) {
		this.cid = cid;
		Message hello = new Message(cid, MessageType.HELLO, new Long(this.id), this.id);
		ManagementNetwork.getInstance().sendNotificationToController(hello);
	}

	@Override
	public void receiveNotificationFromController(Message msg) {
		switch (msg.getType()) {
		case OFPFC_ADD:
			Flow flow = (Flow) msg.getPayload();
			logger.info(this.id + "rcvd OFPFC_ADD " + flow.toString());
			addFlow(flow);
			break;
		case OFPFC_DELETE:
			Flow flowdel = (Flow) msg.getPayload();
			logger.info(this.id + "rcvd OFPFC_DELETE " + flowdel.toString());
			removeFlow(flowdel);
			break;
		default:
			break;
		}
	}

	

}
