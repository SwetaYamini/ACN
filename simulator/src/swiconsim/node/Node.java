package swiconsim.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import swiconsim.api.IControlPlane;
import swiconsim.flow.Flow;
import swiconsim.messages.Message;
import swiconsim.messages.MessageType;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.FlowTable;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

public abstract class Node implements IControlPlane{
	protected long id;
	protected HashMap<Short, Port> ports;
	private FlowTable flowTable;
	protected long cid;
	private static Logger logger = Logger.getLogger("sim:");
	
	public Node(long id) {
		super();
		this.id = id;
		ports = new HashMap<Short, Port>();
		flowTable = new FlowTable();
		registerWithMgmtNodeAsNode();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	@Override
	public Set<Port> getPorts() {
		Set<Port> ports = new HashSet<Port>();
		ports.addAll(this.ports.values());
		return ports;
	}
	
	@Override
	public void addPort(short portNum, Port port){
		this.ports.put(portNum, port);
	}
	
	@Override
	public void sendPktInController(Packet pkt) {
		Message pktIn = new Message(cid, MessageType.PKT_IN, pkt, this.id);
		ManagementNetwork.getInstance().sendNotificationToController(pktIn);
	}
	

	@Override
	public void registerWithController(long cid) {
		this.cid = cid;
		Message hello = new Message(cid, MessageType.HELLO, new Long(this.id),
				this.id);
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
	
	@Override
	public void registerWithMgmtNodeAsNode(){
		ManagementNetwork.getInstance().registerNode(id, this);
	}
}
