package swiconsim.host;

import java.util.logging.Logger;

import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
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
		if(pkt.last==0){
			DataNetwork.getInstance().HopCount.put(pkt, pkt.nhops);
			DataNetwork.getInstance().UtilizationTracker.put(pkt, pkt.maxutil);
		}else{
			//System.out.println("Got last packet. Not updating hop count");
		}
		if(!ManagementNetwork.getInstance().MessageCount.containsKey(pkt)){
			ManagementNetwork.getInstance().MessageCount.put(pkt, 0);
		}					
		rx++;
	}

	public void sendPkt(Packet pkt) {
		logger.info("host pkt sent: " + pkt.toString());
		this.port.getSw().receivePkt(pkt,
				this.port.getId());
		tx++;
	}

	@Override
	public String toString() {
		return "Host [ip=" + ip + ", id=" + id + ", rx=" + rx + ", tx=" + tx
				+ ", port=" + port.getId() + "]";
	}

	public void startFlow(Packet pkt) {
		// TODO Auto-generated method stub
		logger.info("host starting flow: " + pkt.toString());
		this.port.getSw().receivePkt(pkt,
				this.port.getId());
		tx++;
		
	}

	public void endFlow(Packet pkt) {
		// TODO Auto-generated method stub
		logger.info("host terminating flow: " + pkt.toString());
		this.port.getSw().receivePkt(pkt,
				this.port.getId());
		tx++;

	}
}
