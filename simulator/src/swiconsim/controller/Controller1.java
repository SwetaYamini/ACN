package swiconsim.controller;

import java.util.ArrayList;
import swiconsim.flow.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import swiconsim.link.Link;
import swiconsim.messages.Message;
import swiconsim.api.IController;
import swiconsim.api.IControllerSouthBound;
import swiconsim.api.IControlPlane;
import swiconsim.flow.Flow;
import swiconsim.host.Host;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.node.Node;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.util.IPUtil;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         Controller
 * 
 */
public class Controller1 extends Controller implements IControlPlane, IController,
		IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	public List<Long> nodes;
	public List<Long> ExternalPorts;
	public HashMap<PortPair, ArrayList<Long>> paths = new HashMap<PortPair, ArrayList<Long>>();
	public ControllerSouthBound csb;
	public long parent=0;

	public Controller1(long id, long cid) {
		this(id);
		DataNetwork.getInstance().registerNode(id, this);
		registerWithController(cid);
		parent=cid;
	}

	public Controller1(long id) {
		super(id);
		this.id = id;
		this.ports = new TreeMap<Long, Port>();
		nodes = new ArrayList<Long>();
		ExternalPorts = new ArrayList<Long>();
		csb = new ControllerSouthBound(id, nodes, this);
		registerWithMgmtNetAsController();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void addFlow(Flow flow, Packet packet) {
		logger.info("**** " + flow.toString());
		long in_port = flow.getMatch().in_port;
		if(in_port==0){
			System.out.println("WARNING: No in_port specified for flow " + flow.toString() + " at switch " +id);
			return;
		}
		if(!ExternalPorts.contains(in_port)){
			System.out.println("WARNING: " + in_port + " does not belong to controllers' " + id + " external ports");
			return;
		}
		List<Action> actions = flow.getActions();
		for(int i=0; i< actions.size(); i++){
			if(actions.get(i).getType()==ActionType.DROP){
				addFlowToSwitch(reverseLookup(in_port), flow, packet);
			}else{
				long out_port = actions.get(i).getValue();
				if(!ExternalPorts.contains(out_port)){
					System.out.println("WARNING: " + out_port + " does not belong to controllers' " + id + " external ports");
					return;
				}
				ArrayList<Long> path = BFS(in_port, out_port);
				//System.out.println(path);
				if(path.size()==0){
					System.out.println("No path exist for flow " + flow.toString());
				}
				if(path.size()%2!=0){
					System.out.println("WARNING: Wrong path calculated for flow " + flow.toString());
				}
				for(int j=0;j<path.size();j=j+2){
					long sw = reverseLookup(path.get(j));
					if(sw!=reverseLookup(path.get(j+1))){
						System.out.println("WARNING: Wrong path calculated for flow " + flow.toString());
					}
					Match subMatch = new Match(flow.getMatch());
					subMatch.setIn_port(path.get(j));
					List<Action> subActions = new ArrayList<Action>();
					subActions.add(new Action(ActionType.OUT_PORT, path.get(j+1)));					
					Flow subFlow = new Flow(subMatch, subActions, flow.getPriority());
					//System.out.println("Setting up flow " + subFlow + " on switch " + sw);
					addFlowToSwitch(sw, subFlow, packet);
			
					
				}
			}
		}
		
		/*for (Long swid : nodes) {
			addFlowToSwitch(swid, flow);
		}*/
	}
	
	ArrayList<Long> BFS(long in_port, long out_port){
		long start = reverseLookup(in_port);
		long end = reverseLookup(out_port);
		if(start==0 || end==0) return new ArrayList<Long>();
		//System.out.println("BFS on " + in_port + "->" + out_port);
		ArrayBlockingQueue<Long> queue = new ArrayBlockingQueue<Long>(nodes.size());
		ArrayList<Long> closed = new ArrayList<Long>(); 
		HashMap<Long, Long> parent = new HashMap<Long, Long>();
		ArrayList<Long> path = new ArrayList<Long>();
		queue.add(in_port);
		boolean found=false;
		double globalBest=100000;
		
		while(!queue.isEmpty()){
			Long port = queue.peek();
			queue.poll();
			
			closed.add(reverseLookup(port));
			//System.out.println("BFS: switch " + port);
			//path.add(sw);
			
			
			List<Port> swPorts = ManagementNetwork.getInstance().getNode(reverseLookup(port)).getPorts();
			for (Port swPort : swPorts) {
				long port2 = swPort.getId();
				//System.out.println("Analyzing port " + port2);
				if(port2==port) continue;
				if(out_port==port2){
				//	System.out.println("Found port " + port2);
					found=true;
					parent.put(port2, port);
					Long curr = out_port;
					ArrayList<Long> currPath = new ArrayList<Long>();
					double max_util=0;
					while(curr != in_port){
						currPath.add(0, curr);
						Long next = parent.get(curr);
						if(DataNetwork.getInstance().links.containsKey(next) && DataNetwork.getInstance().links.get(next).port2==curr && max_util < DataNetwork.getInstance().links.get(next).utilization){
							max_util = DataNetwork.getInstance().links.get(next).utilization;
						}
						curr = next;
					}
					currPath.add(0,curr);
					//System.out.println(max_util +" and " + currPath.size());
					if(globalBest > max_util || (globalBest==max_util && currPath.size() < path.size())){
						path.clear();
						path.addAll(currPath);
						globalBest = max_util;
					}
					parent.remove(port2);
					continue;
				}
				if(!DataNetwork.getInstance().links.containsKey(port2)) continue; 
				long port3 = DataNetwork.getInstance().links.get(swPort.getId()).port2;
				long sw2 = reverseLookup(port3);
				if(!nodes.contains(sw2)) continue;
				if(closed.contains(sw2)) continue;
				parent.put(port2, port);
				parent.put(port3, port2);
				try {
				//	System.out.println("Adding port " + port3);
					queue.put(port3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//System.out.println("State: "+port+" "+queue+" "+closed);
			
		}
		//System.out.println("Parent key set: " + parent.keySet());
		Long curr = out_port;
		if(!found) return path;
		
		/*while(curr != in_port && found){
			path.add(0, curr);
			Long next = parent.get(curr);
			curr = next;
		}
		path.add(0,curr);*/
		//System.out.println("Global best is: " + globalBest);
		return path;
		
	}
	
	public long reverseLookup(long port){
		long sw=0;
		int found=0;
		for(int i=0; i<nodes.size(); i++){
			List<Port> swPorts = ManagementNetwork.getInstance().getNode(nodes.get(i)).getPorts();
		
			for(int j=0; j<swPorts.size(); j++){
				if(swPorts.get(j).getId()==port){
					found=1;
					break;
				}
			}
			if(found==1){
				sw=nodes.get(i);
				break;
			}
		}
		return sw;		
	}
	
	

	@Override
	public void removeFlow(Flow flow) {
		for (Long swid : nodes) {
			deleteFlowFromSwitch(swid, flow);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IControlPlane#sendPktInController(swiconsim.packet.Packet)
	 */
	//@Override
	/*public void sendPktInController(Packet pkt) {
		// TODO Auto-generated method stub
		sendPktInController(pkt);

	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IControllerSouthBound#receiveNotificationFromSwitch(swiconsim
	 * .messages.Message)
	 */
	@Override
	public void receiveNotificationFromSwitch(Message msg) {
		this.csb.receiveNotificationFromSwitch(msg);
	}
	
	public long checkHost(Packet pkt){
		Iterator<Long> it = this.ports.keySet().iterator();
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

	public void handlePacket(Packet pkt){
		//System.out.print("Controller " + id + " handling packet. ");
		long out_port = checkHost(pkt);
		if(out_port!=0){
			//System.out.println("Out port macthed. Installing rule");
			Match match = new Match(pkt.getIn_port(), pkt.getNw_src(), pkt.getNw_dst());
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add( new Action(ActionType.OUT_PORT, out_port));
			Flow flow = new Flow(match, actions, (short)10);
			addFlow(flow, pkt);
			//System.out.println();
			//ports.get(pkt.getIn_port()).getSw().sendPkt(pkt, pkt.getIn_port());
		}else{
			if(parent==0){
			//	System.out.println("Root controller doesn't see host. Dropping packet.");
			}else{
			//	System.out.println("Sending to parent controller");
				sendPktInController(pkt);
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControllerSouthBound#addFlowToSwitch(long,
	 * swiconsim.flow.Flow)
	 */
	@Override
	public void addFlowToSwitch(long swid, Flow flow, Packet packet) {
		this.csb.addFlowToSwitch(swid, flow, packet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControllerSouthBound#deleteFlowFromSwitch(long,
	 * swiconsim.flow.Flow)
	 */
	@Override
	public void deleteFlowFromSwitch(long swid, Flow flow) {
		this.csb.deleteFlowFromSwitch(swid, flow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IController#getTopology()
	 */
	@Override
	public Topology getTopology() {
		logger.info("Getting topology");
		HashMap<Node, List<Port>> nodePorts = new HashMap<Node, List<Port>>();
		Set<Host> hosts = new HashSet<Host>();
		Map<Long, Link> links = new HashMap<Long, Link>();
		Map<Long, Link> switchLinks = DataNetwork.getInstance().getLinks();
		Map<Long, Link> contLinks = ManagementNetwork.getInstance().getLinks();
		Map<Long, Node> allNodes = ManagementNetwork.getInstance().getNodeMap();
		for (long nodeId : this.nodes) {
			logger.info("Node : " + nodeId);
			Node node = allNodes.get(nodeId);
			nodePorts.put(node, node.getPorts());
			for (Port port : node.getPorts()) {
				if (switchLinks.containsKey(port.getId())) {
					links.put(port.getId(), new Link(switchLinks.get(port.getId())));
				}
				else if (contLinks.containsKey(port.getId())) {
					links.put(port.getId(), new Link(contLinks.get(port.getId())));
				}
				if (port.getHost() != null) {
					hosts.add(port.getHost());
				}
				for (long nodeId2 : this.nodes) {
					Node node2 = allNodes.get(nodeId2);
					for (Port port2 : node2.getPorts()) {
						long realPortId1 = ManagementNetwork.getInstance().getVirtualPortIdMap().get(port.getId());
						long realPortId2 = ManagementNetwork.getInstance().getVirtualPortIdMap().get(port2.getId());
						// logger.info(">> " + realPortId1 + "-" + realPortId2);
						if(switchLinks.containsKey(realPortId1)){
							if(switchLinks.get(realPortId1).port2 == realPortId2){
								links.put(port.getId(), new Link(port.getId(), port2.getId(), 100, 0));
							}
						}
					}
				}
			}
		}
		Topology topology = new Topology(nodePorts, links, hosts);
		return topology;
	}

	@Override
	public void registerWithMgmtNetAsController() {
		ManagementNetwork.getInstance().registerController(id, this);
	}

	@Override
	public List<Port> getPorts() {
		List<Port> ports = new ArrayList<Port>();
		//Iterator<Port> it = this.ExternalPorts.values().iterator();
		for(int i=0; i<this.ExternalPorts.size();i++){
			ports.add(this.ports.get(ExternalPorts.get(i)));
		}
		return ports;
	}
	
	public void populatePorts(){
		for(int i=0;i < nodes.size(); i++){
			List<Port> swPorts = ManagementNetwork.getInstance()
					.getNode(nodes.get(i)).getPorts();
			for (Port swPort : swPorts) {								
				this.addPort(swPort.getId(), swPort);				
			}
		}
	}
	
	public void populateExternalPorts(){
		Iterator<Long> it = this.ports.keySet().iterator();
		while(it.hasNext()){
			long portid = it.next();
			long otherPortId = DataNetwork.getInstance().getOtherPort(portid);
			if(this.ports.containsKey(otherPortId)){
				if(this.ExternalPorts.contains(otherPortId)) this.ExternalPorts.remove(otherPortId);
			}else{
				this.ExternalPorts.add(portid);
			}
		}
	}
	
	public static void main(String[] args){
		
		

		
	}

}
