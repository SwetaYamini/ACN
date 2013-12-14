package swiconsim.node;

import swiconsim.messages.Message;
import swiconsim.packet.Packet;

public abstract class ManagementNode extends Node {
	
	public ManagementNode(long id){
		super(id);
	}

	public void handlePacket(Packet payload) {
		
	}

	public void receiveNotificationFromSwitch(Message msg) {
		
	}

	public void updateUtilization(Packet pkt) {
		
	}

}
