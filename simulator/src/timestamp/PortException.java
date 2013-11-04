package timestamp;

public class PortException extends Exception {
	int switch_id;
	public PortException(int switch_id){
		this.switch_id = switch_id;
	}

}
