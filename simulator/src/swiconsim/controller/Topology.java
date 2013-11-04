package swiconsim.controller;

import java.util.Map;
import java.util.Set;

import swiconsim.host.Host;
import swiconsim.node.Node;
import swiconsim.nwswitch.Switch;

/**
 * @author praveen
 * 
 *         Topology - seen by a controller
 */
public class Topology {
	Set<Node> nodes;
	Map<Long, Long> edges;
	Set<Host> hosts;

	public Topology(Set<Node> nodes, Map<Long, Long> edges, Set<Host> hosts) {
		super();
		this.nodes = nodes;
		this.edges = edges;
		this.hosts = hosts;
	}

	@Override
	public String toString() {
		String ret = "Topology [Switches={";

		for (Node sw : nodes) {
			ret += sw.getId() + ", ";
		}
		ret += "} , Edges=\n";
		for (Long portId1 : edges.keySet()) {
			ret += portId1 + " - " + edges.get(portId1) + "\n";
		}
		
		ret += "Hosts=";
		for(Host host : hosts){
			ret += host.getId() + "(" + host.getIp() + ")" + ", ";
		}
		
		ret += "]";

		return ret;
	}
}
