package timestamp;

import java.util.HashMap;

public class Link extends Element {
	int id;
	int port1;
	int port2;
	int capacity;
	int controller;
	HashMap<Integer, Flow> flows;
	
	public Link(int id, int controller, int port1, int port2, int capacity){
		this.id = id;
		this.controller = controller;
		this.port1 = port1;
		this.port2 = port2;
		this.capacity = capacity;
		flows = new HashMap<Integer, Flow>();
	}
	
	public String toString(){
		String ret = "Link["+id + "]["+controller+"]["+ port1 + "-" + port2 + "]";
		return ret;
	}
	
	public int getOtherPort(int port){
		if(port==port1) return port2;
		else return port1;
	}
}
