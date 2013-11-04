package timestamp;

import java.util.HashMap;
import java.util.Iterator;

public class Switch  {
	int id;
	HashMap<Integer, Port> ports ;
	int controller;
	
	public Switch(int id, int nports, int controller){
		this.id=id;
		this.ports = new HashMap<Integer, Port>();
		for(int i=0; i<nports; i++){
			ports.put(i, new Port(id*Configuration.MAXPORTS+i, i, id));
			Network.ports.put(id*Configuration.MAXPORTS+i, ports.get(i));
		}
		this.controller = controller;
	}
	
	public int addLink(int link_id) throws PortException{
		for(int i=0; i<ports.size(); i++){
			Port port = ports.get(i);
			if(port.link==-1){
				port.link=link_id;
				return port.id;
			}
		}
		throw new PortException(id);
	}
	
	public String toString(){
		String ret = "Switch[" +id+"]["+controller+"]";
		/*ret += "[";
		for(int i=0; i<ports.size(); i++){
			if(i!=0) ret += ",";
			ret += ports.get(i).id;
		}
		ret +="]";*/
		return ret;
	}
	
}
