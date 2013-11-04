package swiconsim.nwswitch;

import java.util.HashMap;
import java.util.TreeMap;

import swiconsim.api.IControlPlane;
import swiconsim.api.ISwitchDataPlane;
import swiconsim.flow.Flow;
import swiconsim.host.Host;
import swiconsim.network.DataNetwork;
import swiconsim.node.Node;
import swiconsim.nwswitch.port.Port;
import swiconsim.nwswitch.port.PortStatus;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Switch - control plane and data plane
 */
public class Switch extends Node implements IControlPlane, ISwitchDataPlane {

	SwitchDataPlane dp;
	
	private FlowTable flowTable;

	public Switch(long id) {
		this(id, 4);
	}

	public Switch(long id, int numPorts, long cid) {
		this(id, numPorts);
		registerWithController(cid);
	}

	public Switch(long id, int numPorts) {
		super(id);
		flowTable = new FlowTable();
		ports = new TreeMap<Short, Port>();
		for (short i = 1; i <= numPorts; i++) {
			Port port = new Port(PortUtil.calculatePortId(id, i), PortStatus.UP, this);
			ports.put(i, port);
		}
		
		dp = new SwitchDataPlane(id, ports, flowTable);
		registerWithDataNet();
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
	public boolean receivePkt(Packet pkt, short in_port) {
		boolean isProcessed = dp.receivePkt(pkt, in_port);
		if (!isProcessed) {
			sendPktInController(pkt);
		}
		return true;
	}

	@Override
	public void sendPkt(Packet pkt, short out_port) {
		dp.sendPkt(pkt, out_port);
	}

	public void registerWithDataNet() {
		DataNetwork.getInstance().registerNode(id, this);
	}

	
	@Override
	public String toString() {
		String ret = "Switch [id=" + id + ",  controller=" + cid + ", ports={";
		for (Port port : ports.values()) {
			ret += port.getId() + ", ";
		}
		
		ret += "}, flowTable=" + flowTable.toString() + "]";
		return ret;
	}

	public boolean addHost(long id, String ip, short portNum) {
		Host host = new Host(id, ip);
		return addHost(host, portNum);
	}

	public boolean addHost(Host host, short portNum) {
		Port p = this.ports.get(portNum);
		if (p.getHost() == null) {
			p.setHost(host);
			return true;
		} else {
			return false;
		}
	}
}
