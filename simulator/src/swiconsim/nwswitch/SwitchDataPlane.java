package swiconsim.nwswitch;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import swiconsim.api.ISwitchDataPlane;
import swiconsim.flow.Action;
import swiconsim.flow.ActionType;
import swiconsim.network.DataNetwork;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.packet.PacketIdentifier;

/**
 * @author praveen
 *
 * Data Plane of a switch
 *
 */
public class SwitchDataPlane implements ISwitchDataPlane {
	long id;
	private HashMap<Short, Port> ports;
	private FlowTable flowTable;
	private static Logger logger = Logger.getLogger("sim:");

	public SwitchDataPlane(long id, HashMap<Short, Port> ports,
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

	/* (non-Javadoc)
	 * @see swiconsim.api.ISwitchDataPlane#receivePkt(swiconsim.packet.Packet, short)
	 */
	@Override
	public boolean receivePkt(Packet pkt, short in_port) {
		logger.info("pkt rcvd: " + pkt.toString() + " on " + in_port);
		pkt.setIn_port(in_port);
		PacketIdentifier pktIden = new PacketIdentifier(pkt);
		List<Action> actions = flowTable.lookup(pktIden, pkt.getSize());
		if(actions.isEmpty()){
			return false;
		}
		else{
			applyActions(pkt, actions);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see swiconsim.api.ISwitchDataPlane#sendPkt(swiconsim.packet.Packet, short)
	 */
	@Override
	public void sendPkt(Packet pkt, short out_port) {
		DataNetwork.getInstance().handlePkt(pkt, ports.get(out_port).getId());
	}
	
	public void applyActions(Packet pkt, List<Action> actions){
		for(Action action : actions){
			if(action.getType() == ActionType.OUT_PORT){
				short out_port = (short)action.getValue();
				sendPkt(pkt, out_port);
			}
		}
	}
	
}
