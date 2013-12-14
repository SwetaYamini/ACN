package swiconsim.controller;

import java.util.List;
import swiconsim.node.ManagementNode;

import java.util.Set;
import java.util.logging.Logger;

import swiconsim.api.IControllerSouthBound;
import swiconsim.flow.Flow;
import swiconsim.messages.Message;
import swiconsim.messages.MessageType;
import swiconsim.network.DataNetwork;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.packet.Packet;
import swiconsim.util.PortUtil;

public class ControllerSouthBound implements IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	public List<Long> switches;
	long id;
	ManagementNode controller;

	public ControllerSouthBound(long id, List<Long> switches,
			ManagementNode controller) {
		super();
		this.id = id;
		this.switches = switches;
		this.controller = controller;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * swiconsim.api.IControllerSouthBound#receiveNotificationFromSwitch(swiconsim
	 * .messages.Message)
	 */
	@Override
	public void receiveNotificationFromSwitch(Message msg) {
		switch (msg.getType()) {
		case HELLO:
			long switchId = (Long) msg.getPayload();
			logger.info(this.id + "Hello from " + switchId);
			
			switches.add(switchId);
			
			
			List<Port> swPorts = ManagementNetwork.getInstance()
					.getNode(switchId).getPorts();
			for (Port swPort : swPorts) {								
				controller.addPort(swPort.getId(), swPort);				
			}
			
			break;
		case PKT_IN:
			//System.out.println("Controller " + id + " received a packet in. Will do something :)");
			controller.handlePacket((Packet) msg.getPayload());
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControllerSouthBound#addFlowToSwitch(long,
	 * swiconsim.flow.Flow)
	 */
	@Override
	public void addFlowToSwitch(long swid, Flow flow, Packet packet) {
		Message msg = new Message(swid, MessageType.OFPFC_ADD, flow, this.id, packet);
		ManagementNetwork.getInstance().sendNotificationToSwitch(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swiconsim.api.IControllerSouthBound#deleteFlowFromSwitch(long,
	 * swiconsim.flow.Flow)
	 */
	@Override
	public void deleteFlowFromSwitch(long swid, Flow flow) {
		Message msg = new Message(swid, MessageType.OFPFC_DELETE, flow, this.id, null);
		ManagementNetwork.getInstance().sendNotificationToSwitch(msg);
	}

	@Override
	public void registerWithMgmtNetAsController() {
		ManagementNetwork.getInstance().registerController(id, this.controller);
	}

}
