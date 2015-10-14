package se.kth.ict.id2203.components.beb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class BebDataMessage extends Pp2pDeliver{
	
	BebDeliver deliver;

	protected BebDataMessage(Address source,BebDeliver deliver) {
		super(source);
		this.deliver = deliver;
		// TODO Auto-generated constructor stub
	}
	
	public BebDeliver getDeliver() {
		return deliver;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 9183185042302932366L;

}
