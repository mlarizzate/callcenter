package ar.com.exam.callcenter.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class OnHoldIVR extends Agent implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(OnHoldIVR.class);

    private Boolean active;

    /**
     * Used to restore the customer to the dispatcher Queue
     */
    private ConcurrentLinkedDeque<Customer> dispatcherCustomersCalls;

    private Integer onHoldTimeSeconds;

    public OnHoldIVR(ConcurrentLinkedDeque<Customer> dispatcherCustomersCalls, Integer onHoldTimeSeconds) {
        this.dispatcherCustomersCalls = dispatcherCustomersCalls;
        this.onHoldTimeSeconds = onHoldTimeSeconds;
        this.active = true;
    }

    public void shutdown(){
        this.active = false;
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.IVR;
    }

    public void addToHoldQueue(Customer customer){
        this.incomingCalls.add(customer);
        this.setAgentStatus(AgentStatus.BUSY);

    }
    @Override
    public void run() {
        while (this.active){
            if(incomingCalls.size()>0){
                Customer customer = incomingCalls.poll();
                logger.info("OnHold Customer, Waiting for an Available Agent");
                try {
                    customer.onHold();
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    logger.error("IVR " + Thread.currentThread().getName() + " was interrupted and could not finish customer of 5 seconds");
                } finally {
                    this.setAgentStatus(AgentStatus.AVAILABLE);
                    this.attendedCalls.add(customer);
                    dispatcherCustomersCalls.addFirst(customer);
                }

            }

        }

    }


}
