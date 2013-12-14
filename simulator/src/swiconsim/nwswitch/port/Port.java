package swiconsim.nwswitch.port;


import swiconsim.host.Host;
import swiconsim.nwswitch.Switch;

public class Port {
	long id;
	Host host = null;
	Switch sw;
	PortStatus status;
	public int external=0;

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

	public Port(Port swPort) {
		this.id = swPort.id;
		this.host = swPort.host;
		this.sw = swPort.sw;
		this.status = swPort.status;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
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
		if(host!=null){
			return "Port [id=" + id + ", host=" + host.getId() + ", sw=" + sw.getId() + ", status="
				+ status + "]";
		}
		return "Port [id=" + id + ", host=NO_HOST, sw=" + sw.getId() + ", status="
		+ status + "]";
	}
	
}
