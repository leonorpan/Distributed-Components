package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class ACKDeliverPp2p extends Pp2pDeliver{
	private Integer rid;

	protected ACKDeliverPp2p(Address source,Integer rid) {
		super(source);
		// TODO Auto-generated constructor stub
		this.rid=rid;
	}

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3795289111973615312L;

}
