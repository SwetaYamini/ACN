package swiconsim.nwswitch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swiconsim.flow.Action;
import swiconsim.flow.Flow;
import swiconsim.packet.PacketIdentifier;

/**
 * @author praveen
 * 
 *         Flow Table on a switch
 * 
 */
public class FlowTable {
	Map<Flow, FlowCounter> flowEntries;
	int nLookups = 0;
	int nMatches = 0;

	public FlowTable() {
		super();
		flowEntries = new HashMap<Flow, FlowCounter>();
		nLookups = 0;
		nMatches = 0;
	}

	/**
	 * @param flowEntries
	 */
	public FlowTable(Map<Flow, FlowCounter> flowEntries) {
		super();
		this.flowEntries = flowEntries;
	}

	public Map<Flow, FlowCounter> getFlowEntries() {
		return flowEntries;
	}

	public void setFlowEntries(Map<Flow, FlowCounter> flowEntries) {
		this.flowEntries = flowEntries;
	}

	/**
	 * @param flow
	 */
	public void addFlowEntry(Flow flow) {
		this.flowEntries.put(flow, new FlowCounter());
	}

	/**
	 * @param flow
	 */
	public void removeFlowEntry(Flow flow) {
		this.flowEntries.remove(flow);
	}

	/**
	 * @param flow
	 * @return
	 */
	public boolean hasFlowEntry(Flow flow) {
		return this.flowEntries.containsKey(flow);
	}

	/**
	 * 
	 * search the table for a entry to which the pkt matches in case of multiple
	 * matches, pick one with highest priority
	 * 
	 * @param pktIden
	 * @return
	 */
	public List<Action> lookup(PacketIdentifier pktIden, int size) {
		int maxPri = 0;
		Flow matchedFlow = new Flow();
		this.nLookups++;
		for (Flow flow : this.flowEntries.keySet()) {
			if (flow.getMatch().isMatch(pktIden)) {
				if (flow.getPriority() > maxPri) {
					matchedFlow = flow;
				}
			}
		}

		List<Action> actions = matchedFlow.getActions();
		if (!actions.isEmpty()) {
			flowEntries.get(matchedFlow).nPackets++;
			flowEntries.get(matchedFlow).nBytes += size;
			this.nMatches++;
		}

		return actions;
	}

	public String toString() {
		String ret = "FlowTable:\nnLookups=" + nLookups + "\tnMatches="
				+ nMatches + "\n";
		for (Flow flow : flowEntries.keySet()) {
			ret += flow.toString() + "\t" + flowEntries.get(flow).toString()
					+ "\n";
		}
		return ret;
	}
}
