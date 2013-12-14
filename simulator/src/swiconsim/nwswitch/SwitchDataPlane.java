package swiconsim.nwswitch;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import swiconsim.api.ISwitchDataPlane;
import swiconsim.flow.Action;
import swiconsim.flow.ActionType;
import swiconsim.host.Host;
import swiconsim.network.DataNetwork;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.packet.PacketIdentifier;

/**
 * @author praveen
 * 
 *         Data Plane of a switch
 * 
 */
public class SwitchDataPlane implements ISwitchDataPlane {
	long id;
	private TreeMap<Long, Port> ports;
	private FlowTable flowTable;
	private static Logger logger = Logger.getLogger("sim:");

	public SwitchDataPlane(long id, TreeMap<Long, Port> ports,
			FlowTable flowTable) {
		super();
		this.id = id;
		this.ports = ports;
		this.flowTable = flowTable;
	}

	public FlowTable getFlowTable() {
		return flowTable;
	}

	public void setFlowTable(FlowTable flowTable) {
		this.flowTable = flowTable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.ISwitchDataPlane#receivePkt(swiconsim.packet.Packet,
	 * short)
	 */
	@Override
	public boolean receivePkt(Packet pkt, long in_port) {
		logger.info("pkt rcvd: " + pkt.toString() + " on " + in_port);
		pkt.setIn_port(in_port);
		PacketIdentifier pktIden = new PacketIdentifier(pkt);
		//System.out.println("Packet Indentifier are: " + pktIden);
		List<Action> actions = flowTable.lookup(pktIden, pkt.getSize());
		//System.out.println("Actions are: " + actions);
		if (actions.isEmpty()) {
			return false;
		} else {
			applyActions(pkt, actions);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.ISwitchDataPlane#sendPkt(swiconsim.packet.Packet,
	 * short)
	 */
	@Override
	public void sendPkt(Packet pkt, long out_port) {
		Port port = this.ports.get(out_port);
		if(port==null){
			System.out.println("WARNING: port " + out_port + " not found on switch " + id + ". ports: " + ports.keySet());
			return;
		}
		Host host = port.getHost();
		if (host != null) {
			host.receivePkt(pkt);
		} else {
			DataNetwork.getInstance().handlePkt(pkt, port.getId());
		}
	}

	public void applyActions(Packet pkt, List<Action> actions) {
		for (Action action : actions) {
			if (action.getType() == ActionType.OUT_PORT) {
				long out_port = (long) action.getValue();
				sendPkt(pkt, out_port);
			}
		}
	}

}
