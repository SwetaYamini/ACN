package swiconsim.nwswitch.port;


import swiconsim.host.Host;
import swiconsim.nwswitch.Switch;

public class Port {
	long id;
	Host host = null;
	Switch sw;
	PortStatus status;

	public Port(int id) {
		super();
		this.id = id;
	}

	public Port(PortStatus status) {
		super();
		this.status = status;
	}

	public Port(long id, PortStatus status, Switch sw) {
		super();
		this.id = id;
		this.status = status;
		this.sw = sw;
	}

	public long getId() {
		return id;
	}

	public PortStatus getStatus() {
		return status;
	}

	public void setStatus(PortStatus status) {
		this.status = status;
	}
	
	public Switch getSw() {
		return sw;
	}

	public void setSw(Switch sw) {
		this.sw = sw;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		host.setPort(this);
		this.host = host;
	}

	public void delHost() {
		this.host = null;
	}

	@Override
	public String toString() {
		return "Port [id=" + id + ", host=" + host.getId() + ", sw=" + sw.getId() + ", status="
				+ status + "]";
	}
	
}
