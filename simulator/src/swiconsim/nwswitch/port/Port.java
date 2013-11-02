package swiconsim.nwswitch.port;

public class Port {
	long id;
	PortStatus status;
	
	public Port(int id) {
		super();
		this.id = id;
	}
	public Port(PortStatus status) {
		super();
		this.status = status;
	}
	public Port(long id, PortStatus status) {
		super();
		this.id = id;
		this.status = status;
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
	
}
