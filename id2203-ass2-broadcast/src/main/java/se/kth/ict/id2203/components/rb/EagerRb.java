package se.kth.ict.id2203.components.rb;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.pa.broadcast.BebMessage;
import se.kth.ict.id2203.pa.broadcast.RbMessage;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.rb.RbBroadcast;
import se.kth.ict.id2203.ports.rb.RbDeliver;
import se.kth.ict.id2203.ports.rb.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;

public class EagerRb extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(EagerRb.class);

	Negative<ReliableBroadcast> rb = provides(ReliableBroadcast.class);
	Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	EagerRbInit init;
	
	Set<RbDataMessage> delivered;
	Integer seqnum;
	Set<Address> allAddresses;
	Address selfAddress;

	public EagerRb(EagerRbInit init) {
		this.init = init;
		subscribe(handleStart, control);
		subscribe(handleRbBroadcast,rb);
		subscribe(handleRbDataMessage,beb);
	}

	Handler<Start> handleStart = new Handler<Start>(){
		@Override
		public void handle(Start event) {
			// TODO Auto-generated method stub
			allAddresses = init.getAllAddresses();
			selfAddress = init.getSelfAddress();
			delivered= new HashSet<RbDataMessage>();
			seqnum=0;
		}
		
	};
	
	Handler<RbBroadcast> handleRbBroadcast = new Handler<RbBroadcast>(){

		@Override
		public void handle(RbBroadcast event) {
			// TODO Auto-generated method stub
			seqnum++;
			logger.info("Will broadcast message {}"+seqnum);
			trigger(new BebBroadcast(new RbDataMessage(init.getSelfAddress(),seqnum,event.getDeliverEvent())), beb);
		}
		
	};
	
	Handler<RbDataMessage> handleRbDataMessage = new Handler<RbDataMessage>(){

		@Override
		public void handle(RbDataMessage event) {
			// TODO Auto-generated method stub
			//System.out.println("received broadcasted message!!!");
			logger.info("Receinved Rb   data message ");
			if (!isIncluded(event.getSeq(),event.getSource().getId())){
				delivered.add(event);
				trigger(event.getDeliver(), rb);
				trigger(new BebBroadcast(new RbDataMessage(event.getSource(), event.getSeq(), event.getDeliver())), beb);
				logger.info("Re broacasting message {}",event.getSeq());
			}
		}
	};

	
	private boolean isIncluded(int sn,int id){
		logger.info("Delivered number of messages {}",+delivered.size());
		for (RbDataMessage rbmsg: delivered){
			if ((rbmsg.getSource().getId()==id)&&(rbmsg.getSeq()==sn)){
				return true;
			}
		}
		return false;
	}
}
