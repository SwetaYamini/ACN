package swiconsim.packet;

import swiconsim.util.IPUtil;

/**
 * @author praveen
 * 
 *         the fields in a pkt that have to be matched
 */
public class PacketIdentifier {
	long in_port;
	int nw_src;
	int nw_dst;

	public PacketIdentifier(long in_port, int nw_src, int nw_dst) {
		super();
		this.in_port = in_port;
		this.nw_src = nw_src;
		this.nw_dst = nw_dst;
	}

	public PacketIdentifier(Packet pkt) {
		this.in_port = pkt.in_port;
		this.nw_src = pkt.nw_src;
		this.nw_dst = pkt.nw_dst;
	}

	public long getIn_port() {
		return in_port;
	}

	public void setIn_port(long in_port2) {
		this.in_port = in_port2;
	}

	public int getNw_src() {
		return nw_src;
	}

	public void setNw_src(int nw_src) {
		this.nw_src = nw_src;
	}

	public int getNw_dst() {
		return nw_dst;
	}

	public void setNw_dst(int nw_dst) {
		this.nw_dst = nw_dst;
	}

	@Override
	public String toString() {
		return "PacketIdentifier [in_port=" + in_port + ", nw_src="
				+ IPUtil.toString(nw_src) + ", nw_dst="
				+ IPUtil.toString(nw_dst) + "]";
	}

}
