package swiconsim.nwswitch;

/**
 * @author praveen
 * Per-flow statistics
 *
 */
public class FlowCounter {
	long nPackets = 0;
	long nBytes = 0;

	public FlowCounter() {
		super();
		this.nPackets = 0;
		this.nBytes = 0;
	}
	
	public String toString(){
		return "nPackets="+nPackets+"\tnBytes="+nBytes;
	}
}
