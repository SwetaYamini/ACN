package swiconsim.messages;

import swiconsim.packet.Packet;



/**
 * @author praveen
 *
 * Messages between switch and controller 
 */
public class Message {
	long to;
	MessageType type;
	Object payload;
	long from;
	public Packet packet;
	
	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public Message(Long to, MessageType type, Object payload, Long from, Packet p) {
		super();
		this.to = to;
		this.type = type;
		this.payload = payload;
		this.from = from;
		this.packet = p;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
	
}
