package swiconsim.test;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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

/**
 * @author praveen
 *
 * testing a switch
 */
public class SwitchTest {

	@Test
	public void test() {
		Controller cont1 = new Controller(101);
		Switch sw = new Switch(11, 3, 101);
		Switch sw2 = new Switch(12, 3, 101);
		System.out.println(sw.toString());
		
		// Add a flow on sw
		Match match = new Match((short) 0, IPUtil.stringToIP("1.2.3.4"), 16,
				IPUtil.stringToIP("6.7.8.9"), 0);
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionType.OUT_PORT, 2));
		Flow flow = new Flow(match, actions, (short) 10);
		Controller cont = ManagementNetwork.getInstance().getController(101);
		cont.addFlowToSwitch(11, flow);
		System.out.println(sw.toString());
		
		// Add edge 1100002 <-> 1200001
		DataNetwork.getInstance().addEdge(11, (short)2, 12, (short)1);
		
		// A pkt that matches the installed flow
		Packet pkt = new Packet((short) 0, IPUtil.stringToIP("1.2.3.4"), 5, 3);
		DataNetwork.getInstance().pushPkt(pkt, 1100001);
		System.out.println(sw.toString());
		
		// this one shouldn't match - pkt will go to controller
		pkt = new Packet((short) 0, IPUtil.stringToIP("1.3.3.4"), 5, 7);
		DataNetwork.getInstance().pushPkt(pkt, 1100001);
		System.out.println(sw.toString());
		
		// print topology seen by cont1
		System.out.print(cont.getTopology().toString());
	}

}
