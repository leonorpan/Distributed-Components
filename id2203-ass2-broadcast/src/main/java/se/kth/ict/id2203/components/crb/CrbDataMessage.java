package se.kth.ict.id2203.components.crb;

import java.util.Map;

import se.kth.ict.id2203.ports.crb.CrbDeliver;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.sics.kompics.address.Address;

public class CrbDataMessage extends RbDeliver{
	
	private Address source;
	private int[] vector;
	private String msg;
	private CrbDeliver deliver;

	public CrbDataMessage(Address source,int[] vector,CrbDeliver event) {
		super(source);
		// TODO Auto-generated constructor stub
		this.vector=vector;
		this.deliver=event;
		
	}

	public int[] getVector() {
		return vector;
	}

	public void setVector(int[] vector) {
		this.vector = vector;
	}





	public CrbDeliver getDeliver() {
		return deliver;
	}

	public void setDeliver(CrbDeliver deliver) {
		this.deliver = deliver;
	}





	/**
	 * 
	 */
	private static final long serialVersionUID = -2490125738135088267L;

}
