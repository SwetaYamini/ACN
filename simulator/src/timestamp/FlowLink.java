package timestamp;

public class FlowLink {
	int link;
	int srcPort;
	int dstPort;
	
	public FlowLink(int link, int src, int dst){
		this.link = link;
		srcPort=  src;
		dstPort = dst;
	}
	
	public String toString(){
		String ret = "" +link;
		return ret;
	}
	
}
