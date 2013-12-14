package swiconsim.flow;

/**
 * @author praveen
 *
 * Action for a flow
 */
public class Action {
	ActionType type;
	long value;
	public Action(ActionType type, long value) {
		super();
		this.type = type;
		this.value = value;
	}
	public ActionType getType() {
		return type;
	}
	public void setType(ActionType type) {
		this.type = type;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
	public String toString(){
		return "Action["+type+","+value+"]";
	}
}
