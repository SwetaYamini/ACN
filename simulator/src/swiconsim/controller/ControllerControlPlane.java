package swiconsim.controller;

import java.util.Collection;

import swiconsim.api.IControlPlane;
import swiconsim.flow.Flow;
import swiconsim.messages.Message;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;

public class ControllerControlPlane implements IControlPlane{

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addFlow(Flow flow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFlow(Flow flow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Port> getPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendPktInController(Packet pkt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerWithController(long cid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveNotificationFromController(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
