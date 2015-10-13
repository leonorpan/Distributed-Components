package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatReplyMessage extends Pp2pDeliver{

	/**
	 * 
	 */
	
	private final Integer sn;
	private static final long serialVersionUID = -7678165393077733049L;

	protected HeartbeatReplyMessage(Address source,Integer sn) {
		super(source);
		// TODO Auto-generated constructor stub
		this.sn=sn;
	}

	public Integer getSn() {
		return sn;
	}

}
