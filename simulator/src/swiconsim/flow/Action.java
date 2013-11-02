package swiconsim.flow;

/**
 * @author praveen
 *
 * Action for a flow
 */
public class Action {
	ActionType type;
	int value;
	public Action(ActionType type, int value) {
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
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public String toString(){
		return "Action["+type+","+value+"]";
	}
}
