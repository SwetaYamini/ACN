package swiconsim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import swiconsim.controller.Controller;
import swiconsim.flow.Action;
import swiconsim.flow.ActionType;
import swiconsim.flow.Flow;
import swiconsim.flow.Match;
import swiconsim.flow.MatchField;
import swiconsim.host.Host;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.Switch;
import swiconsim.packet.Packet;
import swiconsim.util.IPUtil;
import swiconsim.util.PortUtil;

public class SwiConSim {

	public static void main(String[] args) {
		samplerun3();
	}
	
	static void samplerun4(){
		
	}
	
	static void samplerun3(){
		int nports=10;
		Controller c = new Controller(1000);
		
		Controller c1 = new Controller(1001,c.getId());
		Switch sw11 = new Switch(11, nports, c1.getId());
		Switch sw12 = new Switch(12, nports, c1.getId());
		Switch sw13 = new Switch(13, nports, c1.getId());
		Switch sw14 = new Switch(14, nports, c1.getId());
		DataNetwork.getInstance().addEdge(1100001, 1200001, 100);
		DataNetwork.getInstance().addEdge(1200002, 1300001, 100);
		DataNetwork.getInstance().addEdge(1300002, 1400001, 100);
		DataNetwork.getInstance().addEdge(1400002, 1100002, 100);
		
		Controller c2 = new Controller(1002,c.getId());
		Switch sw21 = new Switch(21, nports, c2.getId());
		Switch sw22 = new Switch(22, nports, c2.getId());
		Switch sw23 = new Switch(23, nports, c2.getId());
		Switch sw24 = new Switch(24, nports, c2.getId());
		DataNetwork.getInstance().addEdge(2100001, 2200001, 100);
		DataNetwork.getInstance().addEdge(2200002, 2300001, 100);
		DataNetwork.getInstance().addEdge(2300002, 2400001, 100);
		DataNetwork.getInstance().addEdge(2400002, 2100002, 100);
		
		Controller c3 = new Controller(1003,c.getId());
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
		
		Host h1 = new Host(10001, "1.1.1.1");
		Host h2 = new Host(10002, "1.1.1.2");
		Host h3 = new Host(10003, "1.1.1.3");

		sw13.addHost(h1, 1300003);
		sw23.addHost(h2, 2300003);
		sw33.addHost(h3, 3300003);	
		
		
		
		c1.populatePorts();
		c1.populateExternalPorts();
		
		c2.populatePorts();
		c2.populateExternalPorts();
		
		c3.populatePorts();
		c3.populateExternalPorts();
		
		c.populatePorts();
		c.populateExternalPorts();
		
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
		pkt13 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.3"), 10, 2);
		pkt13.last=1;
		h1.endFlow(pkt13);
		
		Packet pkt23 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.2"),
				IPUtil.stringToIP("1.1.1.3"), 10, 3);
		h2.sendPkt(pkt23);
		h2.startFlow(pkt23);
		pkt23 = new Packet((long) 0, IPUtil.stringToIP("1.1.1.2"),
				IPUtil.stringToIP("1.1.1.3"), 10, 3);
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

	static void samplerun1() {
		/*
		 * Start network elements
		 */

		// c1 - s1 - h1
		// c1 - s2 - h2
		// c2 - s3 - h3
		long c1_id = 1001l, c2_id = 1002l, s1_id = 1, s2_id = 2, s3_id = 3, h1_id = 1, h2_id = 2, h3_id = 3;

		Controller c1 = new Controller(c1_id);
		Controller c2 = new Controller(c2_id);

		Switch s1 = new Switch(s1_id, 4, c1_id);
		Switch s2 = new Switch(s2_id, 4, c1_id);
		Switch s3 = new Switch(s3_id, 4, c2_id);

		Host h1 = new Host(h1_id, "1.1.1.1");
		Host h2 = new Host(h2_id, "1.1.1.2");
		Host h3 = new Host(h3_id, "2.1.1.1");

		s1.addHost(h1, (short) 1);
		s2.addHost(h2, (short) 3);
		s3.addHost(h3, (short) 2);

		// Add edge s1:2 <-> s2:2 and s2:4 <-> s3:1
		DataNetwork.getInstance().addEdge(s1_id, (short) 2, s2_id, (short) 2, 100);
		DataNetwork.getInstance().addEdge(s2_id, (short) 4, s3_id, (short) 1, 100);

		System.out.println(c1.getTopology().toString());
		System.out.println(c2.getTopology().toString());

		// Add flows on s1 and s2 towards h2
		Match match = new Match(MatchField.DST, IPUtil.stringToIP("1.1.1.2"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 2));
		Flow flow = new Flow(match, actions, (short) 10);
		//c1.addFlowToSwitch(s1_id, flow);
		System.out.println(s1.toString());

		match = new Match(MatchField.DST, IPUtil.stringToIP("1.1.1.2"));
		actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 3));
		flow = new Flow(match, actions, (short) 10);
		//c1.addFlowToSwitch(s2_id, flow);
		System.out.println(s2.toString());

		// Send pkt from h1 to h2
		Packet pkt = new Packet((short) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.2"), 10);
		h1.sendPkt(pkt);
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		System.out.println(h1.toString());
		System.out.println(h2.toString());

		// this one shouldn't match - pkt will go to controller
		pkt = new Packet((short) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("2.1.1.1"), 15);
		h1.sendPkt(pkt);
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		System.out.println(h1.toString());
		System.out.println(h2.toString());
	}

	
	static void samplerun2() {
		/*
		 * Start network elements
		 */

		// c3 - c1 - s1 - h1
		// c3 - c2 - s2 - h2
		long c1_id = 1001l, c2_id = 1002l, c3_id = 1003l, s1_id = 1, s2_id = 2, h1_id = 1, h2_id = 2, s3_id=3;

		Controller c3 = new Controller(c3_id);
		Controller c1 = new Controller(c1_id, c3_id);
		Controller c2 = new Controller(c2_id, c3_id);
		
		Switch s1 = new Switch(s1_id, 4, c1_id);
		Switch s2 = new Switch(s2_id, 4, c2_id);
		Switch s3 = new Switch(s3_id, 4, c1_id);

		Host h1 = new Host(h1_id, "1.1.1.1");
		Host h2 = new Host(h2_id, "1.1.1.2");

		s1.addHost(h1, 100001);
		s2.addHost(h2, 200003);

		// Add edge s1:3 <-> s2:2
		DataNetwork.getInstance().addEdge((long) 100003, (long) 200002, 100);
		DataNetwork.getInstance().addEdge((long) 100004, (long) 300001, 100);
		
		//populate ports at controllers
		c1.populatePorts(); c1.populateExternalPorts();
		c2.populatePorts(); c2.populateExternalPorts();
		c3.populatePorts(); c3.populateExternalPorts();
		
		
		//Topology is complete now


		/*System.out.println(c1.getTopology().toString());
		System.out.println(c2.getTopology().toString());
		System.out.println(c3.getTopology().toString());*/

		// Add flows on s3 for outport 
		/*Match match = new Match(MatchField.DST, IPUtil.stringToIP("1.1.1.2"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 3));
		Flow flow = new Flow(match, actions, (short) 10);
		c3.addFlowToSwitch(c1_id, flow);
		c3.addFlowToSwitch(c2_id, flow);
		System.out.println(s1.toString());
		System.out.println(s2.toString());*/

		// Send pkt from h1 to h2
		Packet pkt = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("1.1.1.2"), 10);
		h1.sendPkt(pkt);
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		System.out.println(h1.toString());
		System.out.println(h2.toString());

		// this one shouldn't match - pkt will go to controller
		pkt = new Packet((long) 0, IPUtil.stringToIP("1.1.1.1"),
				IPUtil.stringToIP("2.1.1.1"), 15);
		h1.sendPkt(pkt);
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		System.out.println(h1.toString());
		System.out.println(h2.toString());
		
		
		
		//System.out.println(c1.nodes);  System.out.println(c1.csb.switches);
		//System.out.println(c2.nodes);  System.out.println(c2.csb.switches);
		//System.out.println(c3.nodes);  System.out.println(c3.csb.switches);
		
		System.out.println(c1.ports);
		System.out.println(c2.ports);
		System.out.println(c3.ports);
		
		System.out.println(s1.ports);
		System.out.println(s2.ports);
		System.out.println(s3.ports);
		
		
		System.out.println(c1.ExternalPorts);
		System.out.println(c2.ExternalPorts);
		System.out.println(c3.ExternalPorts);
		
		
		//for(long a : ManagementNetwork.getInstance().getVirtualPortIdMap().keySet()){
		//	System.out.println(a + " " + ManagementNetwork.getInstance().getVirtualPortIdMap().get(a));
		//}
		
	}
	
}
