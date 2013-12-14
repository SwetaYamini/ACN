package swiconsim.nwswitch;

import java.util.HashMap;
import java.util.Iterator;
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
import swiconsim.util.IPUtil;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Switch - control plane and data plane
 */
public class Switch extends Node implements IControlPlane, ISwitchDataPlane, Comparable {

	SwitchDataPlane dp;
	private FlowTable flowTable;

	public Switch(long id) {
		this(id, 4);
	}

	public Switch(long id, int numPorts, long cid) {
		this(id, numPorts);
		this.parent=cid;
		//DataNetwork.getInstance().registerNode(id, this);
		registerWithController(cid);
	}

	public Switch(long id, int numPorts) {
		super(id);
		flowTable = new FlowTable();
		ports = new TreeMap<Long, Port>();
		for (short i = 1; i <= numPorts; i++) {
			long portid = PortUtil.calculatePortId(id, i);
			//System.out.println("Switch " + id + ": Adding port " + portid);
			Port port = new Port(portid, PortStatus.UP, this);
			ports.put(portid, port);
		}
		
		dp = new SwitchDataPlane(id, ports, flowTable);
		registerWithDataNet();
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void addFlow(Flow f, Packet pkt) {
		//System.out.println(id + ": Adding flow " + f.toString());
		flowTable.addFlowEntry(f);
		//System.out.println(flowTable.toString());

	}

	@Override
	public void removeFlow(Flow f) {
		flowTable.removeFlowEntry(f);
	}

	@Override
	public boolean receivePkt(Packet pkt, long in_port) {
		//if(pkt.id > 600) System.out.print("Processing packet "+pkt.id);
		boolean isProcessed = dp.receivePkt(pkt, in_port);
		if (!isProcessed) {
			//System.out.print(" Sending to controller");
			sendPktInController(pkt);
		}
		//System.out.println();
		return true;
	}

	@Override
	public void sendPkt(Packet pkt, long out_port) {
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

	public boolean addHost(long id, String ip, long portNum) {
		Host host = new Host(id, ip);
		return addHost(host, portNum);
	}

	public boolean addHost(Host host, long portNum) {
		Port p = this.ports.get(portNum);
		if (p.getHost() == null) {
			p.setHost(host);
			return true;
		} else {
			return false;
		}
	}
	

	@Override
	public int compareTo(Object other) {
		Switch sw = (Switch) other;
		if(sw.getId()==this.id) return 0;
		if(sw.getId() < this.id) return 1;
		return -1;
	}
	
	public long checkHost(Packet pkt){
		//System.out.println("I'm a switch. My id is " + id);
		Iterator<Long> it = this.ports.keySet().iterator();
		//System.out.println("Size: " + this.ports.size());
		while(it.hasNext()){
			Port port = this.ports.get(it.next());
			if(port.getHost()!=null){
				//System.out.println("Checking " + port.getHost().getIp() + " and " + IPUtil.toString(pkt.getNw_dst()));
				if(port.getHost().getIp().equals(IPUtil.toString(pkt.getNw_dst()))){			
					return port.getId();
				}
			}
		}
		return 0;
	}
}
