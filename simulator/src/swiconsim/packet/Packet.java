package swiconsim.packet;

import swiconsim.util.IPUtil;

/**
 * @author praveen
 * 
 *         data packet
 */
public class Packet extends PacketIdentifier implements Comparable<Packet> {
	public int nhops=0;
	public double maxutil=0;
	public int id=0;
	public int last=0;
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	int size;

	public Packet(long in_port, int nw_src, int nw_dst, int size, int id) {
		super(in_port, nw_src, nw_dst);
		this.size = size;
		this.nhops=0;
		this.id=id;
	}
	
	public Packet(long in_port, int nw_src, int nw_dst, int size) {
		super(in_port, nw_src, nw_dst);
		this.size = size;
		this.nhops=0;
		this.id=0;
	}

	@Override
	public String toString() {
		return "Packet [port=" + in_port +", size=" + size + ", nw_src=" + IPUtil.toString(nw_src)
				+ ", nw_dst=" + IPUtil.toString(nw_dst) + "]";
	}
	
	public boolean equals(Packet pkt){
		if(pkt.id==id) return true;
		return false;
	}

	@Override
	public int compareTo(Packet packet) {
		if(packet.id==this.id) return 0;
		if(packet.id < this.id) return 1;
		return -1;
	}

}
