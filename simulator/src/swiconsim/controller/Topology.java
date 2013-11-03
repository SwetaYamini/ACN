package swiconsim.controller;

import java.util.Map;
import java.util.Set;

import swiconsim.host.Host;
import swiconsim.nwswitch.Switch;

/**
 * @author praveen
 * 
 *         Topology - seen by a controller
 */
public class Topology {
	Set<Switch> switches;
	Map<Long, Long> edges;
	Set<Host> hosts;

	public Topology(Set<Switch> switches, Map<Long, Long> edges, Set<Host> hosts) {
		super();
		this.switches = switches;
		this.edges = edges;
		this.hosts = hosts;
	}

	@Override
	public String toString() {
		String ret = "Topology [Switches={";

		for (Switch sw : switches) {
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
