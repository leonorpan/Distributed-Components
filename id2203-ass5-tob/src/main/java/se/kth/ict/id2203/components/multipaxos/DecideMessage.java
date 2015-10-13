package se.kth.ict.id2203.components.multipaxos;

import se.kth.ict.id2203.ports.fpl.FplDeliver;
import se.sics.kompics.address.Address;

public class DecideMessage extends FplDeliver{
	
	private Integer pts;
	private Integer pl;
	private Integer t;

	protected DecideMessage(Address source,Integer pts,Integer pl, Integer t) {
		super(source);
		// TODO Auto-generated constructor stub
		this.pts=pts;
		this.pl=pl;
		this.t=t;
	}

	public Integer getPts() {
		return pts;
	}

	public void setPts(Integer pts) {
		this.pts = pts;
	}

	public Integer getPl() {
		return pl;
	}

	public void setPl(Integer pl) {
		this.pl = pl;
	}

	public Integer getT() {
		return t;
	}

	public void setT(Integer t) {
		this.t = t;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6314167232607148059L;

}
