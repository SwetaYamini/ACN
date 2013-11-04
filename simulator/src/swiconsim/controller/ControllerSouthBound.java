package swiconsim.controller;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import swiconsim.api.IControllerSouthBound;
import swiconsim.flow.Flow;
import swiconsim.messages.Message;
import swiconsim.messages.MessageType;
import swiconsim.network.ManagementNetwork;
import swiconsim.nwswitch.Switch;
import swiconsim.nwswitch.port.Port;
import swiconsim.util.PortUtil;

public class ControllerSouthBound implements IControllerSouthBound {
	private static Logger logger = Logger.getLogger("sim:");
	List<Long> switches;
	long id;
	Controller controller;

	public ControllerSouthBound(long id, List<Long> switches,
			Controller controller) {
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
			// add its ports to controller's ports
			/*Set<Port> swPorts = ((Switch) (ManagementNetwork.getInstance()
					.getNode(switchId))).getPorts();
			for (Port swPort : swPorts) {
				short portNum = (short) this.controller.getPorts().size();
				Port port = new Port(swPort);
				port.setId(PortUtil.calculatePortId(id, portNum));
				this.controller.addPort(portNum, port);
			}
			*/
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
	public void addFlowToSwitch(long swid, Flow flow) {
		Message msg = new Message(swid, MessageType.OFPFC_ADD, flow, this.id);
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
		Message msg = new Message(swid, MessageType.OFPFC_DELETE, flow, this.id);
		ManagementNetwork.getInstance().sendNotificationToSwitch(msg);
	}

	@Override
	public void registerWithMgmtNetAsController() {
		ManagementNetwork.getInstance().registerController(id, this.controller);
	}

}