package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.sics.kompics.address.Address;

public class BebWriteRequest extends BebDeliver{

	private Integer rid;
	private Integer[] triple;
	
	public BebWriteRequest(Address source,Integer rid,Integer[] triple) {
		super(source);
		this.rid=rid;
		this.triple=triple;
		// TODO Auto-generated constructor stub
	}

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	public Integer[] getTriple() {
		return triple;
	}

	public void setTriple(Integer[] triple) {
		this.triple = triple;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1908553446344424404L;

}
