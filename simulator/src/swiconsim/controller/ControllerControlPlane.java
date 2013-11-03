package swiconsim.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import swiconsim.api.IControlPlane;
import swiconsim.flow.Flow;
import swiconsim.messages.Message;
import swiconsim.messages.MessageType;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

public class ControllerControlPlane implements IControlPlane{
	long id;
	long cid;
	Controller controller;
	
	private static Logger logger = Logger.getLogger("sim:");
	
	public ControllerControlPlane(long id, Controller controller) {
		super();
		this.id = id;
		this.controller = controller;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void addFlow(Flow flow) {
		for(Long swid : this.controller.switches){
			this.controller.addFlowToSwitch(swid, flow);
		}
	}

	@Override
	public void removeFlow(Flow flow) {
		for(Long swid : this.controller.switches){
			this.controller.deleteFlowFromSwitch(swid, flow);
		}
	}

	@Override
	public Set<Port> getPorts() {
		Set<Port> ports = new HashSet<Port>();
		for(Long swid : this.controller.switches){
			Switch sw = ManagementNetwork.getInstance().getSwitch(swid);
			ports.addAll(sw.getPorts());
		}
		return ports;
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
