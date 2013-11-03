package swiconsim.packet;

import swiconsim.util.IPUtil;

/**
 * @author praveen
 * 
 *         data packet
 */
public class Packet extends PacketIdentifier {
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	int size;

	public Packet(short in_port, int nw_src, int nw_dst, int size) {
		super(in_port, nw_src, nw_dst);
		this.size = size;
	}

	@Override
	public String toString() {
		return "Packet [size=" + size + ", nw_src=" + IPUtil.toString(nw_src)
				+ ", nw_dst=" + IPUtil.toString(nw_dst) + "]";
	}

}
