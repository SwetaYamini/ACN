package swiconsim.controller;

import java.util.Map;
import java.util.Set;

import swiconsim.nwswitch.Switch;

/**
 * @author praveen
 *
 * Topology - seen by a controller
 */
public class Topology {
	Set<Switch> switches;
	Map<Long, Long> edges;
	public Topology(Set<Switch> switches, Map<Long, Long> edges) {
		super();
		this.switches = switches;
		this.edges = edges;
	}
	@Override
	public String toString() {
		String ret  = "Topology [switches={";
		for(Switch sw : switches) {
			ret += sw.getId() + ", ";
		}
		ret += "}\nEdges={";
		for (Long portId1 : edges.keySet()) {
			ret += portId1 + "\t" + edges.get(portId1) + "\n";
		}
		ret += "}]";
		return ret;
	}
	
	
	
	
}
