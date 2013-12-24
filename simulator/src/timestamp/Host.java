package timestamp;



public class Host {
	public int id;
	public int port;
	public int controller;


	public Host(int id, int controller, int port) {
		this.id = id;
		this.controller = controller;
		this.port = port;
	}


	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void receivePkt() {
		
	}

	public void sendPkt() {
		
	}

	@Override
	public String toString() {
		return "Host [id=" + id + ", port=" + port + "]";
	}

	public void startFlow() {

	}

	public void endFlow() {
	
	}
}
