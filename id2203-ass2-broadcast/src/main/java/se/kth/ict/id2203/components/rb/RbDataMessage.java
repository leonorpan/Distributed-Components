package se.kth.ict.id2203.components.rb;

import se.kth.ict.id2203.ports.beb.BebDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

public class RbDataMessage extends BebDeliver{
	
	Address source;
	Integer seq;
	RbDeliver deliver;

	public RbDataMessage(Address source,Integer seq,RbDeliver deliver) {
		super(source);
		// TODO Auto-generated constructor stub
		this.source=source;
		this.seq=seq;
		this.deliver=deliver;
	}

	public RbDeliver getDeliver() {
		return deliver;
	}

	public void setDeliver(RbDeliver deliver) {
		this.deliver = deliver;
	}

	public Address getSource() {
		return source;
	}

	public void setSource(Address source) {
		this.source = source;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2021283271786402377L;

}
