package swiconsim.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author praveen
 *
 * Flow - Match, Action and priority
 */
public class Flow {
	Match match;
	List<Action> actions;
	short priority;
	public Flow(Match match, List<Action> action, short priority) {
		super();
		this.match = match;
		this.actions = action;
		this.priority = priority;
	}
	public Flow() {
		this.match = new Match();
		this.actions = new ArrayList<Action>();
		this.priority = 0;
	}
	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> action) {
		this.actions = action;
	}
	public short getPriority() {
		return priority;
	}
	public void setPriority(short priority) {
		this.priority = priority;
	}
	public void addAction(Action action){
		this.actions.add(action);
	}
	public void removeAction(Action action){
		this.actions.remove(action);
	}
	public String toString(){
		String ret = "{" + this.match.toString() + "} \t {";
		for(Action action : this.actions){
			ret += action.toString()+ " ,";
		}
		ret += "}";
		return ret;
	}

}
