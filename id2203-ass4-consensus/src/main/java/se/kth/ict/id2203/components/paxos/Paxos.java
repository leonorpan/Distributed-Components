package se.kth.ict.id2203.components.paxos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.ports.ac.AbortableConsensus;
import se.kth.ict.id2203.ports.ac.AcAbort;
import se.kth.ict.id2203.ports.ac.AcDecide;
import se.kth.ict.id2203.ports.ac.AcPropose;
import se.kth.ict.id2203.ports.beb.BebBroadcast;
import se.kth.ict.id2203.ports.beb.BestEffortBroadcast;
import se.kth.ict.id2203.ports.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.ports.pp2p.Pp2pDeliver;
import se.kth.ict.id2203.ports.pp2p.Pp2pSend;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;

public class Paxos extends ComponentDefinition {

	private static final Logger logger = LoggerFactory.getLogger(Paxos.class);

	private Negative<AbortableConsensus> ac = provides(AbortableConsensus.class);
	private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
	private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	
	Set<Address> allAddresses;
	Address selfAddress;
	
	PaxosInit init;
	Integer t;
	Integer prepts;
	
	Integer[] accv;
	Integer[] pv;
	
	List<Integer[]> readList;
	
	Integer ACKS;

	public Paxos(PaxosInit init) {
		this.init=init;
		this.allAddresses=init.getAllAddresses();
		this.selfAddress=init.getSelfAddress();
		subscribe(handleStart,control);
		subscribe(handleAcPropose,ac);
		subscribe(handlePrepareMessage,beb);
		subscribe(handleNackMessage,pp2p);
		subscribe(handlePrepareAckMessage,pp2p);
		subscribe(handleAcceptMessage,beb);
		subscribe(handleAcceptAckMessage,pp2p);
	}
	
	Handler<Start> handleStart = new Handler<Start>(){

		@Override
		public void handle(Start event) {
			// TODO Auto-generated method stub
			t=0;
			prepts=0;
			accv = new Integer[]{0,null}; //ats,av
			pv = new Integer[]{0,null}; //pts,pv
			//NOT SURE ABOUT CONTEXT OF ARRAYLIST, SHOULD IT BE INTEGER????
			readList = new ArrayList<Integer[]>();
			
			ACKS=0;
			logger.info("Initializing Paxos......");
		}
		
	};
	
	Handler<AcPropose> handleAcPropose = new Handler<AcPropose>(){

		@Override
		public void handle(AcPropose event) {
			// TODO Auto-generated method stub
			t++;
			pv[0] = (t*allAddresses.size())+selfAddress.getId();
			logger.info("new pv is {} and old t was {}",pv[0],t);
			pv[1] = event.getValue();
			readList=new ArrayList<Integer[]>();
			ACKS=0;
			logger.info("Received Ac Propose request... Will broadcast PREPARE message....",+event.getValue());
			trigger(new BebBroadcast(new PrepareMessage(selfAddress,pv[0],t)), beb);
		}
		
	};
	
	Handler<PrepareMessage> handlePrepareMessage = new Handler<PrepareMessage>(){

		@Override
		public void handle(PrepareMessage event) {
			// TODO Auto-generated method stub
			t = getMax(t,event.getT())+1;
			logger.info("GOT PREPARE message... will now NACK or PREPARE ACK.....");
			if ((event.getPts())<(prepts)){
				logger.info("WILL NACK HERE");
				trigger(new Pp2pSend(event.getSource(), new NackMessage(selfAddress,event.getPts(),t)), pp2p);
			}else {
				prepts = event.getPts();
				logger.info("WILL PREPARE ACK.....");
				trigger(new Pp2pSend(event.getSource(), new PrepareAckMessage(selfAddress,accv[0],accv[1],event.getPts(),t)), pp2p);
			}
		}
		
		
		
	};
	
	
	private Integer getMax(Integer i, Integer j){
		if (i>=j){
			return i;
		}else {
			return j;
		}
	}
	Handler<NackMessage> handleNackMessage = new Handler<NackMessage>(){

		@Override
		public void handle(NackMessage event) {
			// TODO Auto-generated method stub
			logger.info("RECEOVED NACK... WILL ABORT....");
			t=getMax(t,event.getT())+1;
			if (event.getTs().equals(pv[0])){
				pv[0] = 0;
				trigger(new AcAbort(), ac);
			}
		}
		
	};
	
	Handler<PrepareAckMessage> handlePrepareAckMessage = new Handler<PrepareAckMessage>(){

		@Override
		public void handle(PrepareAckMessage event) {
			// TODO Auto-generated method stub
			t = getMax(t,event.getT())+1;
			logger.info("GOT PREPAREACK message... but there are conditions {} {}",event.getTs(),pv[0]);
			if (event.getTs().equals(pv[0])){
				readList.add(new Integer[]{event.getAts(),event.getAv()});
				logger.info("Added to readList..");
				if (readList.size()>(allAddresses.size()/2)){
					logger.info("GOT MAJORITYYYY");
					accv= highest();
					if (accv[0]!=0){
						pv[1]=accv[1];
					}
					readList.clear();
					logger.info("GOT PREPAREACK message... will now  ACKmessage.....");
					trigger(new BebBroadcast(new AcceptMessage(selfAddress,pv[0],pv[1],t)), beb);
				}
			}
		}
		
		private Integer[] highest(){
			Integer[] max={0,0};
			for (Integer[] r:readList){
				if (r[0]>max[0]){
					max=r;
				}
			}
			return max;
		}
		
	};
	
	Handler<AcceptMessage> handleAcceptMessage = new Handler<AcceptMessage>(){

		@Override
		public void handle(AcceptMessage event) {
			// TODO Auto-generated method stub
			t = getMax(t,event.getT())+1;
			logger.info("GOT ACCEPT message.....");
			if (event.getPts()<prepts){
				logger.info("Will send NACK message.....");
				trigger(new Pp2pSend(event.getSource(), new NackMessage(selfAddress, event.getPts(), t)), pp2p);
			}else {
				prepts = event.getPts();
				accv[0]=prepts;
				accv[1]=event.getPv();
				logger.info("Will send ACCEPT ACK message.....");
				trigger(new Pp2pSend(event.getSource()
						,new AcceptAckMessage(selfAddress,  event.getPts(),t)),pp2p);
			}
		}
		
	};
	
	Handler<AcceptAckMessage> handleAcceptAckMessage = new Handler<AcceptAckMessage>(){

		@Override
		public void handle(AcceptAckMessage event) {
			// TODO Auto-generated method stub
			t = getMax(t,event.getT());
			logger.info("GOT ACCEPT ACK message.....");
			if (event.getTs().equals(pv[0])){
				ACKS++;
				if (ACKS>(allAddresses.size()/2)){
					pv[0]=0;
					logger.info("Will trigger accept event.....");
					trigger(new AcDecide(pv[1]), ac);
				}
			}
		}
		
	};
	
	
}
