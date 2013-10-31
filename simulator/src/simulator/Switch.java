package simulator;

import graph.Flow;

import java.util.Collection;
import java.util.HashMap;

public class Switch implements ISwitch {
	private int id;
	private HashMap<Integer, Port> ports;
	private FlowTable flowTable;
	
	public Switch(int Id) {
		id = Id;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public boolean addFlow(Flow f) {
		return flowTable.add(f);
	}
	
	@Override
	public boolean removeFlow(Flow f) {
		return flowTable.remove(f);
	}
	
	@Override
	public Collection<Port> getPorts() {
		return ports.values();
	}
	
	
}
