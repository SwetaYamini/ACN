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
import swiconsim.messages.MessageType;
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
import swiconsim.util.SwitchFlowPair;
import swiconsim.node.ManagementNode;

/**
 * @author praveen
 * 
 *         Controller
 * 
 */
public class Controller2 extends ManagementNode implements IControlPlane, IController,
IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	public List<Long> nodes;
	public HashMap<PortPair, ArrayList<Long>> paths = new HashMap<PortPair, ArrayList<Long>>();
	public ControllerSouthBound csb;


	public Controller2(long id) {
		super(id);
		//DataNetwork.getInstance().registerNode(id, this);
		this.id = id;
		this.ports = new TreeMap<Long, Port>();
		nodes = new ArrayList<Long>();
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
		List<Action> actions = flow.getActions();
		for(int i=0; i< actions.size(); i++){
			if(actions.get(i).getType()==ActionType.DROP){
				addFlowToSwitch(reverseLookup(in_port), flow, packet);
			}else{
				long out_port = actions.get(i).getValue();
				ArrayList<Long> path = BFS(in_port, out_port);
				//System.out.println(path);
				if(path.size()==0){
					System.out.println("No path exist for flow " + flow.toString());
					return;
				}
				if(path.size()%2!=0){
					System.out.println("WARNING: Wrong path calculated for flow " + flow.toString());
					return;
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
					if(nodes.contains(sw)){
						addFlowToSwitch(sw, subFlow, packet);
					}else{
						//TODO: send packet to controller owning sw
						long cid = getController(sw);
						Message msg = new Message(cid, MessageType.PEER_ADD, new SwitchFlowPair(sw, subFlow), id, packet);
						sendNotificationToPeer(msg);
					}


				}
			}
		}

		/*for (Long swid : nodes) {
			addFlowToSwitch(swid, flow);
		}*/
	}
	
	public void updateUtilization(Packet pkt) {
		Iterator<Long> it = ManagementNetwork.getInstance().getMgmtContMap().keySet().iterator();
		while(it.hasNext()){
			long cid = it.next();
			if(cid==id) continue;
			Message msg = new Message(cid, MessageType.PEER_UPDATE, null, id, pkt);
			sendNotificationToPeer(msg);
		}
	}

	public void sendNotificationToPeer(Message msg){
		ManagementNetwork.getInstance().sendNotificationToPeer(msg);
	}

	public void receiveNotificationFromPeer(Message msg){
		switch (msg.getType()) {
		case PEER_ADD:
			SwitchFlowPair swFlow = (SwitchFlowPair) msg.getPayload();
			long switchId = swFlow.sw;
			Flow flow = swFlow.flow;
			logger.info(this.id + "PEER_ADD from " + msg.getFrom());
			addFlowToSwitch(switchId, flow, msg.packet);										
			break;
		case PEER_UPDATE:
			//This message can be safely ignore because the update had been currently updated in DataNetwork which is what this is going to use.
			break;
		default:
			break;
		}
	}

	public long getController(long sw){
		if(!DataNetwork.getInstance().getNodeMap().containsKey(sw)){
			return 0;
		}
		return DataNetwork.getInstance().getNodeMap().get(sw).parent;		
	}

	ArrayList<Long> BFS(long in_port, long out_port){
		long start = reverseLookup(in_port);
		long end = reverseLookup(out_port);
		if(start==0 || end==0) return new ArrayList<Long>();
		//System.out.println("BFS on " + in_port + "->" + out_port);
		ArrayBlockingQueue<Long> queue = new ArrayBlockingQueue<Long>(DataNetwork.getInstance().getNodeMap().size());
		ArrayList<Long> closed = new ArrayList<Long>(); 
		ArrayList<Long> open = new ArrayList<Long>();
		HashMap<Long,Long> parent = new HashMap<Long, Long>();
		ArrayList<Long> path = new ArrayList<Long>();
		queue.add(in_port);
		boolean found=false;
		double globalBest=100000;

		while(!queue.isEmpty()){
			Long port = queue.peek();
			queue.poll();

			closed.add(reverseLookup(port));
			open.remove(reverseLookup(port));
			//System.out.println("BFS: switch " + port);
			//path.add(sw);


			List<Port> swPorts = DataNetwork.getInstance().getNodeMap().get(reverseLookup(port)).getPorts();
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
				//if(!nodes.contains(sw2)) continue;
				if(closed.contains(sw2) || open.contains(sw2)) continue;
				
				parent.put(port2, port);
				parent.put(port3, port2);
				try {
					//	System.out.println("Adding port " + port3);
					queue.put(port3);
					open.add(sw2);
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
		Iterator<Long> it = DataNetwork.getInstance().getNodeMap().keySet().iterator();
		while(it.hasNext()){
			sw = it.next();
			List<Port> swPorts = DataNetwork.getInstance().getNodeMap().get(sw).getPorts();

			for(int j=0; j<swPorts.size(); j++){
				if(swPorts.get(j).getId()==port){
					found=1;
					break;
				}
			}
			if(found==1){
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

		Iterator<Long> it = DataNetwork.getInstance().getNodeMap().keySet().iterator();
		while(it.hasNext()){
			long sw = it.next();
			List<Port> swPorts = DataNetwork.getInstance().getNodeMap().get(sw).getPorts();		
			for(int j=0; j<swPorts.size(); j++){
				if(swPorts.get(j).getHost()!=null){
					//System.out.println("Checking " + port.getHost().getIp() + " and " + IPUtil.toString(pkt.getNw_dst()));
					if(swPorts.get(j).getHost().getIp().equals(IPUtil.toString(pkt.getNw_dst()))){			
						return swPorts.get(j).getId();
					}
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
			//	System.out.println("Root controller doesn't see host. Dropping packet.");
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
		return ports;
	}

	public static void main(String[] args){
		//samplerun1();
		samplerun3();
	}
	
	static void samplerun1(){
		int nports=4;
		Controller2 c1 = new Controller2(1001);
		Controller2 c2 = new Controller2(1002);


		Switch sw1 = new Switch(1,nports,1001);
		Switch sw2 = new Switch(2,nports,1002);

		DataNetwork.getInstance().addEdge(100001, 200001, 100);

		Host h1 = new Host(10001, "1.1.1.1");
		Host h2 = new Host(10002, "1.1.1.2");

		sw1.addHost(h1, 100002);
		sw2.addHost(h2, 200002);

		Packet pkt12 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.2"), 10, 1);
		h1.sendPkt(pkt12);
		h1.startFlow(pkt12);
		DataNetwork.getInstance().printUtilizations();
		//pkt12 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
		//IPUtil.stringToIP("1.1.1.2"), 10, 1);
		pkt12.last=1;
		h1.endFlow(pkt12);
		DataNetwork.getInstance().printUtilizations();

		Iterator<Packet> it = DataNetwork.getInstance().HopCount.keySet().iterator();
		while(it.hasNext()){
			Packet pkt = it.next();
			System.out.println(pkt.id + ": " + pkt.nhops);
		}

		System.out.println("ControllerToSwitchCount: " + ManagementNetwork.getInstance().ControllerToSwitchCount);
		System.out.println("SwitchToControllerCount: " + ManagementNetwork.getInstance().SwitchToControllerCount);
		System.out.println("ControllerToControllerCount: " + ManagementNetwork.getInstance().ControllerToControllerCount);
		//System.out.println("MessageCount size: " + ManagementNetwork.getInstance().MessageCount.size());

		Iterator<Packet> it2 = ManagementNetwork.getInstance().MessageCount.keySet().iterator();
		while(it2.hasNext()){
			Packet pkt = it2.next();
			System.out.println(pkt.id + ": " + ManagementNetwork.getInstance().MessageCount.get(pkt));
		}		

	}
	
	static void samplerun3(){
		int nports=10;
		
		Controller2 c1 = new Controller2(1001);
		Switch sw11 = new Switch(11, nports, c1.getId());
		Switch sw12 = new Switch(12, nports, c1.getId());
		Switch sw13 = new Switch(13, nports, c1.getId());
		Switch sw14 = new Switch(14, nports, c1.getId());
		DataNetwork.getInstance().addEdge(1100001, 1200001, 100);
		DataNetwork.getInstance().addEdge(1200002, 1300001, 100);
		DataNetwork.getInstance().addEdge(1300002, 1400001, 100);
		DataNetwork.getInstance().addEdge(1400002, 1100002, 100);
		
		Controller2 c2 = new Controller2(1002);
		Switch sw21 = new Switch(21, nports, c2.getId());
		Switch sw22 = new Switch(22, nports, c2.getId());
		Switch sw23 = new Switch(23, nports, c2.getId());
		Switch sw24 = new Switch(24, nports, c2.getId());
		DataNetwork.getInstance().addEdge(2100001, 2200001, 100);
		DataNetwork.getInstance().addEdge(2200002, 2300001, 100);
		DataNetwork.getInstance().addEdge(2300002, 2400001, 100);
		DataNetwork.getInstance().addEdge(2400002, 2100002, 100);
		
		Controller2 c3 = new Controller2(1003);
		Switch sw31 = new Switch(31, nports, c3.getId());
		Switch sw32 = new Switch(32, nports, c3.getId());
		Switch sw33 = new Switch(33, nports, c3.getId());
		Switch sw34 = new Switch(34, nports, c3.getId());
		DataNetwork.getInstance().addEdge(3100001, 3200001, 100);
		DataNetwork.getInstance().addEdge(3200002, 3300001, 100);
		DataNetwork.getInstance().addEdge(3300002, 3400001, 100);
		DataNetwork.getInstance().addEdge(3400002, 3100002, 100);

		DataNetwork.getInstance().addEdge(1100003, 2100003, 100);
		DataNetwork.getInstance().addEdge(2100004, 3100003, 100);
		DataNetwork.getInstance().addEdge(3100004, 1100004, 100);
		
			
		
		
		//System.out.println("Controller ports: " + c3.ports.keySet());
		//System.out.println("Controller external ports: " + c3.ExternalPorts);
		
	
		//System.out.println(c.BFS(100002,300004));
		Match match = new Match(100002, IPUtil.stringToIP("1.1.1.1"), IPUtil.stringToIP("1.1.1.2"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 600004));
		Flow flow = new Flow(match, actions, (short) 10);
		/*System.out.println(sw1.toString());
		System.out.println(sw2.toString());
		System.out.println(sw3.toString());*/
		//c3.addFlow(flow);
		/*System.out.println(sw1.toString());
		System.out.println(sw2.toString());
		System.out.println(sw3.toString());
		System.out.println(sw4.toString());
		System.out.println(sw5.toString());
		System.out.println(sw6.toString());*/
		

		Host h1 = new Host(10001, "1.1.1.1");
		Host h2 = new Host(10002, "1.1.1.2");
		Host h3 = new Host(10003, "1.1.1.3");

		sw13.addHost(h1, 1300003);
		sw23.addHost(h2, 2300003);
		sw33.addHost(h3, 3300003);
		
		//System.out.println(c3.parent);
		
		Packet pkt12 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.2"), 10, 1);
		h1.sendPkt(pkt12);
		h1.startFlow(pkt12);
		//DataNetwork.getInstance().printUtilizations();
		//pkt12 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
		//		IPUtil.stringToIP("1.1.1.2"), 10, 1);
		pkt12.last=1;
		h1.endFlow(pkt12);
		//DataNetwork.getInstance().printUtilizations();
		
		Packet pkt13 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.3"), 10, 2);
		h1.sendPkt(pkt13);
		h1.startFlow(pkt13);
		//pkt13 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),		IPUtil.stringToIP("1.1.1.3"), 10, 2);
		pkt13.last=1;
		h1.endFlow(pkt13);
		
		Packet pkt23 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.2"),
				IPUtil.stringToIP("1.1.1.3"), 10, 3);
		h2.sendPkt(pkt23);
		h2.startFlow(pkt23);
		//pkt23 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.2"),	IPUtil.stringToIP("1.1.1.3"), 10, 3);
		pkt23.last=1;
		h2.endFlow(pkt23);
		
		
		
		Iterator<Packet> it = DataNetwork.getInstance().HopCount.keySet().iterator();
		while(it.hasNext()){
			Packet pkt = it.next();
			System.out.println(pkt.id + ": " + pkt.nhops);
		}
		
		System.out.println("ControllerToSwitchCount: " + ManagementNetwork.getInstance().ControllerToSwitchCount);
		System.out.println("SwitchToControllerCount: " + ManagementNetwork.getInstance().SwitchToControllerCount);
		System.out.println("ControllerToControllerCount: " + ManagementNetwork.getInstance().ControllerToControllerCount);
		//System.out.println("MessageCount size: " + ManagementNetwork.getInstance().MessageCount.size());

		Iterator<Packet> it2 = ManagementNetwork.getInstance().MessageCount.keySet().iterator();
		while(it2.hasNext()){
			Packet pkt = it2.next();
			System.out.println(pkt.id + ": " + ManagementNetwork.getInstance().MessageCount.get(pkt));
		}
	}

}
