package se.kth.ict.id2203.components.riwcm;

import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class Pp2pReadRequest extends Pp2pDeliver{
	
	private String value;
	private Integer rid;
	private Integer[] triple;

	protected Pp2pReadRequest(Address source,String value,Integer rid, Integer[] triple) {
		super(source);
		this.value=value;
		this.rid=rid;
		this.triple=triple;
		// TODO Auto-generated constructor stub
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	private static final long serialVersionUID = 8780100641071393136L;

}
