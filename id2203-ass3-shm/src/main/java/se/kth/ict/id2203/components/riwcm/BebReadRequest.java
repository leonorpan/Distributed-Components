package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class BebReadRequest extends BebDeliver{
	
	private Integer rid;

	public BebReadRequest(Address source, Integer rid) {
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
	private static final long serialVersionUID = 5320707015530480112L;

}
