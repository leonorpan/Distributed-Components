package se.kth.ict.id2203.components.riwcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.ar.ArReadRequest;
import se.kth.ict.id2203.ports.ar.ArReadResponse;
import se.kth.ict.id2203.ports.ar.ArWriteRequest;
import se.kth.ict.id2203.ports.ar.ArWriteResponse;
import se.kth.ict.id2203.ports.ar.AtomicRegister;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);
	
	private ReadImposeWriteConsultMajorityInit init;
	
	private Negative<AtomicRegister> ar = provides(AtomicRegister.class);
	private Positive<BestEffortBroadcast> beb =requires(BestEffortBroadcast.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	
	private Set<Address> allAddresses;
	private Address  selfAddress;
	
	//timestamp , rank, value (ts,wr,val)
	Integer[] triple;
	
	//acknowledgments received
	Integer ACKS;
	
	Integer writeVal=null;
	Integer readVal=null;
	
	//request identifier - to identify the messages belonging to different reads
	Integer rid;
	
	//list of triples
	List<Integer[]> readList;
	Boolean reading;
	
	public ReadImposeWriteConsultMajority(ReadImposeWriteConsultMajorityInit event) {
		this.init=event;
		subscribe(handleStart, control);
		subscribe(handleArReadRequest,ar);
		subscribe(handleArWriteRequest,ar);
		subscribe(handleBebReadRequest,beb);
		subscribe(handlePp2pReadRequest,pp2p);
		subscribe(handleBebWriteRequest,beb);
		subscribe(handleACKDeliverPp2p,pp2p);
		
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			// TODO Auto-generated method stub
			allAddresses=init.getAllAddresses();
			selfAddress=init.getSelfAddress();			
			
			triple =new Integer[]{0,0,0};
			ACKS=0;
			rid=0;
			readList  = new ArrayList<Integer[]>();
			reading=false;
			
			writeVal=null;
			readVal=null;
			
			
		}
		
	};
	
	Handler<ArReadRequest> handleArReadRequest = new Handler<ArReadRequest>(){

		@Override
		public void handle(ArReadRequest event) {
			// TODO Auto-generated method stub
			logger.info("Handling ar read request from upper layers.");
			//increment the identifier
			rid++;
			logger.info("Rid is now: {}",rid);
			//WE haven 't received any acks yet
			ACKS=0;
			//clear our list.
			readList =  new ArrayList<Integer[]>();
			reading=true;
			logger.info("Reading is now: {}",reading);
			trigger(new BebBroadcast(new BebReadRequest(selfAddress,rid)), beb);
		}
		
	};
	
	Handler<ArWriteRequest> handleArWriteRequest= new Handler<ArWriteRequest>(){

		@Override
		public void handle(ArWriteRequest event) {
			// TODO Auto-generated method stub
			logger.info("Handling ar write request from upper layers....");
			rid++;
			writeVal = event.getValue();
			logger.info("Value to be written: {}",writeVal);
			ACKS=0;
			readList.clear();
			trigger(new BebBroadcast(new BebReadRequest(selfAddress, rid)), beb);
		}
		
	};
	
	Handler<BebReadRequest> handleBebReadRequest = new Handler<BebReadRequest>(){

		@Override
		public void handle(BebReadRequest event) {
			// TODO Auto-generated method stub
			logger.info("Handling the bebreadREquest message....Rid is: {} ",event.getRid());
			trigger(new Pp2pSend(event.getSource(), new Pp2pReadRequest(selfAddress, "shit", event.getRid(), triple)),pp2p);
		}
		
	};
	
	
	//receiving reads from majority of other nodes....
	Handler<Pp2pReadRequest> handlePp2pReadRequest = new Handler<Pp2pReadRequest>(){

		@Override
		public void handle(Pp2pReadRequest event) {
			// TODO Auto-generated method stub
			logger.info("Handling Pp2pReadRequest ......Now i will have to receivemajority...");
			if (rid.equals(event.getRid())){
				readList.add(event.getTriple());
				//got majority?
				logger.info("I have received {} responses",readList.size());
				if (readList.size()>(allAddresses.size()/2)){
					Integer[] tripleToSendBack = highest();
					readVal=tripleToSendBack[2];
					readList.clear();
					logger.info("Will now broadcast a write message....");
					if (reading){
						trigger(new BebBroadcast(new BebWriteRequest(selfAddress, rid, tripleToSendBack)), beb);
					}else {
						logger.info("Have to write the value: {}",writeVal);
						//writeVal=tripleToSendBack[2];
						trigger(new BebBroadcast(new BebWriteRequest(selfAddress, rid,new Integer[] {tripleToSendBack[0]+1,selfAddress.getId(),writeVal})), beb);
					}
				}
			
			}
		}
		
		private Integer[] highest(){
			Integer[] maxTriple={0,0,0};
			for (Integer[] tr: readList){
				if (tr[0]>maxTriple[0]){
					maxTriple = tr;
				}else if (tr[0]==maxTriple[0]){
					if (tr[1]>maxTriple[1]){
						maxTriple=tr;
					}
				}
			}
			logger.info("Max triple is: {"+maxTriple[0]+", "+maxTriple[1]+" , "+maxTriple[2]+" }");
			return maxTriple;
		}
		
	};
	
	Handler<BebWriteRequest> handleBebWriteRequest = new Handler<BebWriteRequest>(){

		@Override
		public void handle(BebWriteRequest event) {
			// TODO Auto-generated method stub
			logger.info("Handling beb write request from read or write....");
			
			if ((event.getTriple()[0]>triple[0])){
				triple = event.getTriple();
				//trigger(new Pp2pSend(event.getSource(), new ACKDeliverPp2p(selfAddress, event.getRid())), pp2p);
			}else if (event.getTriple()[0]==triple[0]){
				if (event.getTriple()[1]>triple[1]){
					triple = event.getTriple();
					//trigger(new Pp2pSend(event.getSource(), new ACKDeliverPp2p(selfAddress, event.getRid())), pp2p);
				}
			}
			trigger(new Pp2pSend(event.getSource(), new ACKDeliverPp2p(selfAddress, event.getRid())), pp2p);
			
		}
		
	};
	
	Handler<ACKDeliverPp2p> handleACKDeliverPp2p = new Handler<ACKDeliverPp2p>(){

		@Override
		public void handle(ACKDeliverPp2p event) {
			// TODO Auto-generated method stub
			logger.info("r: {}  AND RID: {}",event.getRid(),rid);
			if (rid.equals(event.getRid())){
			logger.info("Handling ACK delivery event.....");
			ACKS++;
			if (ACKS>(allAddresses.size()/2)){
				ACKS=0;
				if (reading){
					reading=false;
					trigger(new ArReadResponse(readVal), ar);
				}else{
					trigger(new ArWriteResponse(), ar);
				}
			}
		}
	}
	};

}
