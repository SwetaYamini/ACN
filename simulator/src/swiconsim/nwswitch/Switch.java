package swiconsim.nwswitch;

import java.util.Collection;
import java.util.HashMap;

import swiconsim.messages.Message;
import swiconsim.api.ISwitchControlPlane;
import swiconsim.api.ISwitchDataPlane;
import swiconsim.flow.Flow;
import swiconsim.host.Host;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.port.Port;
import swiconsim.nwswitch.port.PortStatus;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Switch - control plane and data plane
 */
public class Switch implements ISwitchControlPlane, ISwitchDataPlane {

	SwitchControlPlane cp;
	SwitchDataPlane dp;
	long id;
	private HashMap<Short, Port> ports;
	private FlowTable flowTable;

	public Switch(long id) {
		this(id, 4);
	}

	public Switch(long id, int numPorts, long cid) {
		this(id, numPorts);
		registerWithController(cid);
	}

	public Switch(long id, int numPorts) {
		super();
		this.id = id;
		flowTable = new FlowTable();
		ports = new HashMap<Short, Port>();
		for (short i = 1; i <= numPorts; i++) {
			Port port = new Port(PortUtil.calculatePortId(id, i), PortStatus.UP, this);
			ports.put(i, port);
		}

		cp = new SwitchControlPlane(id, ports, flowTable);
		dp = new SwitchDataPlane(id, ports, flowTable);
		registerWithMgmtNet();
		registerWithDataNet();
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void addFlow(Flow flow) {
		cp.addFlow(flow);
	}

	@Override
	public void removeFlow(Flow flow) {
		cp.removeFlow(flow);
	}

	@Override
	public Collection<Port> getPorts() {
		return this.ports.values();
	}

	@Override
	public boolean receivePkt(Packet pkt, short in_port) {
		boolean isProcessed = dp.receivePkt(pkt, in_port);
		if (!isProcessed) {
			cp.sendPktInController(pkt);
		}
		return true;
	}

	@Override
	public void sendPkt(Packet pkt, short out_port) {
		dp.sendPkt(pkt, out_port);
	}

	@Override
	public void sendPktInController(Packet pkt) {
		cp.sendPktInController(pkt);
	}

	@Override
	public void registerWithController(long cid) {
		cp.registerWithController(cid);
	}

	public void registerWithMgmtNet() {
		ManagementNetwork.getInstance().registerSwitch(id, this);
	}

	public void registerWithDataNet() {
		DataNetwork.getInstance().registerSwitch(id, this);
	}

	@Override
	public void receiveNotificationFromController(Message msg) {
		cp.receiveNotificationFromController(msg);
	}

	@Override
	public String toString() {
		String ret = "Switch [id=" + id + ",  controller=" + cp.cid + ", ports={";
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
