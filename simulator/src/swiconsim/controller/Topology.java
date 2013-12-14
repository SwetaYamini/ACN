package swiconsim.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import swiconsim.host.Host;
import swiconsim.link.Link;
import swiconsim.node.Node;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;

/**
 * @author praveen
 * 
 *         Topology - seen by a controller
 */
public class Topology {
	HashMap<Node, List<Port>> nodePorts;
	Map<Long, Link> edges;
	Set<Host> hosts;

	public Topology(HashMap<Node, List<Port>> nodePorts, Map<Long, Link> edges, Set<Host> hosts) {
		super();
		this.nodePorts = nodePorts;
		this.edges = edges;
		this.hosts = hosts;
	}

	@Override
	public String toString() {
		String ret = "Topology [Switches={";

		for (Node sw : nodePorts.keySet()) {
			ret += sw.getId() + " { ";
			for(Port port : nodePorts.get(sw)){
				ret += port.getId() + ", ";
			}
			ret += "}, ";
		}
		ret += "} , Edges={\n";
		for (Long portId1 : edges.keySet()) {
			ret += portId1 + " - " + edges.get(portId1) + "\n";
		}
		
		ret += "}\nHosts=";
		for(Host host : hosts){
			ret += host.getId() + "(" + host.getIp() + ")" + ", ";
		}
		
		ret += "]";

		return ret;
	}
}
