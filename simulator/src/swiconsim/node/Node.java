package swiconsim.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import swiconsim.api.IControlPlane;
import swiconsim.flow.Flow;
import swiconsim.messages.Message;
import swiconsim.messages.MessageType;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.FlowTable;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.util.IPUtil;

public abstract class Node implements IControlPlane {
	protected long id;
	public TreeMap<Long, Port> ports;
	private FlowTable flowTable;
	protected long cid;
	private static Logger logger = Logger.getLogger("sim:");
	public long parent=0;

	public Node(long id) {
		super();
		this.id = id;
		ports = new TreeMap<Long, Port>();
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
	public List<Port> getPorts() {
		List<Port> ports = new ArrayList<Port>();
		Iterator<Port> it = this.ports.values().iterator();
		while(it.hasNext()){
			ports.add(it.next());
			/*if (!ManagementNetwork.getInstance().getVirtualPortIdMap()
					.containsKey(this.ports.get(i).getId())) {
				ManagementNetwork
						.getInstance()
						.getVirtualPortIdMap()
						.put(this.ports.get(i).getId(),
								this.ports.get(i).getId());
			}*/
		}
		return ports;
	}
	
	public List<Port> getAllPorts() {
		List<Port> ports = new ArrayList<Port>();
		Iterator<Port> it = this.ports.values().iterator();
		while(it.hasNext()){
			ports.add(it.next());
			/*if (!ManagementNetwork.getInstance().getVirtualPortIdMap()
					.containsKey(this.ports.get(i).getId())) {
				ManagementNetwork
						.getInstance()
						.getVirtualPortIdMap()
						.put(this.ports.get(i).getId(),
								this.ports.get(i).getId());
			}*/
		}
		return ports;
	}

	@Override
	public void addPort(long portNum, Port port) {
		this.ports.put(portNum, port);
	}

	@Override
	public void sendPktInController(Packet pkt) {
		Message pktIn = new Message(cid, MessageType.PKT_IN, pkt, this.id, pkt);
		ManagementNetwork.getInstance().sendNotificationToController(pktIn);
	}

	@Override
	public void registerWithController(long cid) {
		this.cid = cid;
		Message hello = new Message(cid, MessageType.HELLO, new Long(this.id),
				this.id, null);
		ManagementNetwork.getInstance().sendNotificationToController(hello);
	}

	@Override
	public void receiveNotificationFromController(Message msg) {
		logger.info(" rcvd ");
		switch (msg.getType()) {
		case OFPFC_ADD:
			Flow flow = (Flow) msg.getPayload();
			logger.info(this.id + ": rcvd OFPFC_ADD " + flow.toString());
			addFlow(flow, msg.packet);
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
	public void registerWithMgmtNodeAsNode() {
		ManagementNetwork.getInstance().registerNode(id, this);
	}
	
	public void receiveNotificationFromPeer(Message msg){
		
	}
	
	public long checkHost(Packet pkt){
		return 0;
	}
}
