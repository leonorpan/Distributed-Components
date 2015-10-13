package se.kth.ict.id2203.components.epfd;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatRequestMessage extends Pp2pDeliver{
	private final int sn;

	protected HeartbeatRequestMessage(Address source,Integer sn) {
		super(source);
		// TODO Auto-generated constructor stub
		this.sn=sn;
	}

	public int getSn() {
		return sn;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3830255588022005317L;

}
