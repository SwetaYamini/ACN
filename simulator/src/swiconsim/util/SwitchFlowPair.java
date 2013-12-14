package swiconsim.util;

import swiconsim.flow.Flow;

public class SwitchFlowPair {
	public long sw;
	public Flow flow;
	
	public SwitchFlowPair(long sw, Flow flow){
		this.sw = sw;
		this.flow = flow;
	}
}
