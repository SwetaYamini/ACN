package swiconsim;

import java.util.ArrayList;
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
		samplerun2();
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
		DataNetwork.getInstance().addEdge(s1_id, (short) 2, s2_id, (short) 2);
		DataNetwork.getInstance().addEdge(s2_id, (short) 4, s3_id, (short) 1);

		System.out.println(c1.getTopology().toString());
		System.out.println(c2.getTopology().toString());

		// Add flows on s1 and s2 towards h2
		Match match = new Match(MatchField.DST, IPUtil.stringToIP("1.1.1.2"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 2));
		Flow flow = new Flow(match, actions, (short) 10);
		c1.addFlowToSwitch(s1_id, flow);
		System.out.println(s1.toString());

		match = new Match(MatchField.DST, IPUtil.stringToIP("1.1.1.2"));
		actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 3));
		flow = new Flow(match, actions, (short) 10);
		c1.addFlowToSwitch(s2_id, flow);
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
		long c1_id = 1001l, c2_id = 1002l, c3_id = 1003l, s1_id = 1, s2_id = 2, h1_id = 1, h2_id = 2;

		Controller c3 = new Controller(c3_id);
		Controller c1 = new Controller(c1_id, c3_id);
		Controller c2 = new Controller(c2_id, c3_id);
		
		Switch s1 = new Switch(s1_id, 4, c1_id);
		Switch s2 = new Switch(s2_id, 4, c2_id);

		Host h1 = new Host(h1_id, "1.1.1.1");
		Host h2 = new Host(h2_id, "1.1.1.2");

		s1.addHost(h1, (short) 1);
		s2.addHost(h2, (short) 3);

		// Add edge s1:3 <-> s2:2
		DataNetwork.getInstance().addEdge(s1_id, (short) 3, s2_id, (short) 2);

		System.out.println(c1.getTopology().toString());
		System.out.println(c2.getTopology().toString());
		System.out.println(c3.getTopology().toString());

		// Add flows on s3 for outport 
		Match match = new Match(MatchField.DST, IPUtil.stringToIP("1.1.1.2"));
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 3));
		Flow flow = new Flow(match, actions, (short) 10);
		c3.addFlowToSwitch(c1_id, flow);
		c3.addFlowToSwitch(c2_id, flow);
		System.out.println(s1.toString());
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
		
		//for(long a : ManagementNetwork.getInstance().getVirtualPortIdMap().keySet()){
		//	System.out.println(a + " " + ManagementNetwork.getInstance().getVirtualPortIdMap().get(a));
		//}
		
	}
	
}
