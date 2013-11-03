package swiconsim.host;

import java.util.logging.Logger;

import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

/**
 * @author praveen
 * 
 *         representation of a host
 */
public class Host {
	String ip;
	long id;
	long rx, tx;
	Port port;

	private static Logger logger = Logger.getLogger("sim:");

	public Host(long id, String ip) {
		super();
		this.id = id;
		this.ip = ip;
		this.rx = 0;
		this.tx = 0;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}

	public void receivePkt(Packet pkt) {
		logger.info("host pkt rcvd: " + pkt.toString());
		rx++;
	}

	public void sendPkt(Packet pkt) {
		logger.info("host pkt sent: " + pkt.toString());
		this.port.getSw().receivePkt(pkt,
				PortUtil.getPortNumFromPortId(this.port.getId()));
		tx++;
	}

	@Override
	public String toString() {
		return "Host [ip=" + ip + ", id=" + id + ", rx=" + rx + ", tx=" + tx
				+ ", port=" + port.getId() + "]";
	}
}
