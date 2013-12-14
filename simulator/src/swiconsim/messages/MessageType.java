package swiconsim.messages;

public enum MessageType {
	HELLO, // sent by switch to controller when setting the controller for the
			// switch
	PKT_IN, // switch forwarding an in-pkt to controller
	OFPFC_ADD, // Add a flow on a switch
	OFPFC_DELETE, // delete a flow from a switch
	PEER_ADD, //Message sent by controller to another controller, requesting to add the flow on it's switches
	PEER_UPDATE
}