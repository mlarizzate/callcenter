package ar.com.exam.callcenter.model;

import ar.com.exam.callcenter.exception.BusyAgentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public abstract class Agent implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);

    private AgentStatus agentStatus;

    protected ConcurrentLinkedDeque<Customer> incomingCalls;

    protected ConcurrentLinkedDeque<Customer> attendedCalls;

    public Agent() {
        this.agentStatus = AgentStatus.AVAILABLE;
        this.incomingCalls = new ConcurrentLinkedDeque<>();
        this.attendedCalls = new ConcurrentLinkedDeque<>();
    }

    /**
     * Gets the actual status of an Agent
     * @return AgentStatus
     */
    public synchronized AgentStatus getAgentStatus() {
        return agentStatus;
    }

    protected synchronized void setAgentStatus(AgentStatus agentStatus) {
        logger.info("Agent " + Thread.currentThread().getName() + " changes its state to " + agentStatus);
        this.agentStatus = agentStatus;
    }

    /**
     * Gets a list of all Attended Customers at the moment.
     * @return attendedCalls size
     */
    public synchronized List<Customer> getAttendedCustomers() {
        return new ArrayList<>(attendedCalls);
    }

    /**
     * Queues customers to be attended by an Available Agent.
     *
     * @param customer customer thats calling
     */
    public synchronized void delegateCustomer(Customer customer) throws BusyAgentException {
        if(this.incomingCalls.isEmpty()){
            this.setAgentStatus(AgentStatus.BUSY);
            logger.info("Agent " + Thread.currentThread().getName() + " queues a call of " + customer.getCallDuration() + " seconds");
            this.incomingCalls.add(customer);
        }else{
            throw new BusyAgentException();
        }
    }

    /**
     * Returns the priority value for Agent Role
     * @return AgentType for AgentRole
     */
    public abstract AgentType getAgentType();

    /**
     * This is the method that runs on the thread.
     * If the incoming calls queue is not empty, then it changes its state from AVAILABLE to BUSY, takes the call
     * and when it finishes it changes its state from BUSY back to AVAILABLE. This allows a Thread Pool to decide
     * to dispatch another call to another employee.
     */
    @Override
    public void run() {
        logger.info("Agent " + Thread.currentThread().getName() + " starts to work");
        while (!this.incomingCalls.isEmpty()) {
                Customer customer = this.incomingCalls.poll();
                logger.info("Agent " + Thread.currentThread().getName() + " receives a call of " + customer.getCallDuration() + " seconds");
                try {
                    TimeUnit.SECONDS.sleep(customer.getCallDuration());
                } catch (InterruptedException e) {
                    logger.error("Agent " + Thread.currentThread().getName() + " was interrupted and could not finish customer of " + customer.getCallDuration() + " seconds");
                } finally {
                    this.setAgentStatus(AgentStatus.AVAILABLE);
                }
                this.attendedCalls.add(customer);
                logger.info("Agent " + Thread.currentThread().getName() + " finished a customer call of " + customer.getCallDuration() + " seconds");
        }
    }
}
