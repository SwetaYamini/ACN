package swiconsim;

import java.util.ArrayList;
import java.util.List;

import swiconsim.controller.Controller;
import swiconsim.flow.Action;
import swiconsim.flow.ActionType;
import swiconsim.flow.Flow;
import swiconsim.flow.Match;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.Switch;
import swiconsim.packet.Packet;
import swiconsim.util.IPUtil;
import swiconsim.util.PortUtil;

public class SwiConSim {

	public static void main(String[] args) {
		samplerun1();
	}

	static void samplerun1() {
		// Start a controller and two switches
		long c1_id = 1001l, s1_id=1, s2_id=2;
		Controller c1 = new Controller(c1_id);
		Switch s1 = new Switch(s1_id, 4, c1_id);
		Switch s2 = new Switch(s2_id, 4, c1_id);
		System.out.print(c1.getTopology().toString());
		
		// Add a flow on s1
		Match match = new Match((short) 0, IPUtil.stringToIP("1.2.3.4"), 16,
				IPUtil.stringToIP("6.7.8.9"), 0);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 2));
		Flow flow = new Flow(match, actions, (short) 10);
		c1.addFlowToSwitch(s1_id, flow);
		System.out.println(s1.toString());
		
		// Add edge s1:2 <-> s2:0
		DataNetwork.getInstance().addEdge(s1_id, (short)2, s2_id, (short)0);
		
		// A pkt that matches the installed flow - pushed in at s1:1
		Packet pkt = new Packet((short) 0, IPUtil.stringToIP("1.2.3.4"), IPUtil.stringToIP("2.2.2.2"), 10);
		DataNetwork.getInstance().pushPkt(pkt, PortUtil.calculatePortId(s1_id, (short)1));
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		
		// this one shouldn't match - pkt will go to controller
		pkt = new Packet((short) 0, IPUtil.stringToIP("1.3.3.4"), IPUtil.stringToIP("2.2.2.2"), 15);
		DataNetwork.getInstance().pushPkt(pkt, PortUtil.calculatePortId(s1_id, (short)1));
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		
	}

}
