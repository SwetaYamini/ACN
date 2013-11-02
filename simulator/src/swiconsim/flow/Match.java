package swiconsim.flow;

import swiconsim.packet.PacketIdentifier;
import swiconsim.util.IPUtil;

/**
 * @author praveen
 * 
 * Match in a flow entry
 * 
 */
public class Match {
	/** Input switch port */
    public short in_port = 0;

    /** IP source address */
    public int nw_src = 0;
    
    public int nw_src_mask = 0;

    /** IP destination address */
    public int nw_dst = 0;
    public int nw_dst_mask = 0;
    
    public Match(){
    }
    
    /**
     * 
     * @param in_port
     * @param nw_src
     * @param nw_dst
     *  
     */
	public Match(short in_port, int nw_src, int nw_dst) {
		super();
		this.in_port = in_port;
		this.nw_src = nw_src;
		this.nw_src_mask = 32;
		this.nw_dst = nw_dst;
		this.nw_dst_mask = 32;
	}
	
	public Match(short in_port, int nw_src, int nw_src_mask, int nw_dst,
			int nw_dst_mask) {
		super();
		this.in_port = in_port;
		this.nw_src = nw_src;
		this.nw_src_mask = nw_src_mask;
		this.nw_dst = nw_dst;
		this.nw_dst_mask = nw_dst_mask;
	}

	public short getIn_port() {
		return in_port;
	}

	public void setIn_port(short in_port) {
		this.in_port = in_port;
	}

	public int getNw_src() {
		return nw_src;
	}

	public void setNw_src(int nw_src) {
		this.nw_src = nw_src;
	}

	public int getNw_src_mask() {
		return nw_src_mask;
	}

	public void setNw_src_mask(int nw_src_mask) {
		this.nw_src_mask = nw_src_mask;
	}

	public int getNw_dst() {
		return nw_dst;
	}

	public void setNw_dst(int nw_dst) {
		this.nw_dst = nw_dst;
	}

	public int getNw_dst_mask() {
		return nw_dst_mask;
	}

	public void setNw_dst_mask(int nw_dst_mask) {
		this.nw_dst_mask = nw_dst_mask;
	}

	public boolean equals(Object o){
		if(o!=null && o instanceof Match) {
            Match m = (Match)o;
            return in_port == m.in_port &&
            		nw_src == m.nw_src &&
            		nw_src_mask == m.nw_src_mask &&
                    nw_dst == m.nw_dst &&
                    nw_dst_mask == m.nw_dst_mask;
        }
        return false;
	}
	
	public String toString() {
        String ret = "Match: {";
        ret = "inputPort=" + (in_port==0 ? "*" : in_port);
        ret += ", IPSrc=" + (IPUtil.maskedIPToString(nw_src, nw_src_mask));
        ret += ", IPDst=" + (IPUtil.maskedIPToString(nw_dst, nw_dst_mask));
        return ret + "}";
    }
	
	public boolean isMatch(PacketIdentifier pktIden){
		short pktin_port = this.in_port==0?0:pktIden.getIn_port();
		Match candidate = new Match(pktin_port, pktIden.getNw_src(),  this.nw_src_mask, pktIden.getNw_dst(), this.nw_dst_mask);
		if(this.toString().equals(candidate.toString())) return true;
		return false;
	}
    
}
