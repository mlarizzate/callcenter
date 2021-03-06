package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.exception.MoreIVRsThanSupportedCallsConfigured;
import ar.com.exam.callcenter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Dispatcher implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private static Integer maxSupportedCalls;
    private Integer maxHoldedTimes;

    private Boolean active;

    private ThreadPoolExecutor threadPoolExecutor;

    private ConcurrentLinkedDeque<Agent> agents;

    private ConcurrentLinkedDeque<Customer> customersCalls;

    private Map<Customer, RejectReason> rejectedCustomers;

    private CustomerDispatchStrategy callAttendStrategy;

    private ConcurrentLinkedDeque<OnHoldIVR> onHoldIVRList;
    //private OnHoldIVR onHoldIVR;



    public Dispatcher(Integer maxSupportedCalls, Integer maxHoldedTimes, Integer onHoldIVRs, Integer onHoldTimeSeconds){
        if(onHoldIVRs > maxSupportedCalls){
            throw new MoreIVRsThanSupportedCallsConfigured();
        }

        Dispatcher.maxSupportedCalls = maxSupportedCalls;
        rejectedCustomers = new HashMap<>();
        this.agents = new ConcurrentLinkedDeque<>();
        this.onHoldIVRList = new ConcurrentLinkedDeque<>();
        this.customersCalls = new ConcurrentLinkedDeque<>();
        this.maxHoldedTimes = maxHoldedTimes;
        this.callAttendStrategy = new DefaultCustomerDispatchStrategy();
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Dispatcher.maxSupportedCalls);
        this.runOnHoldIvrs(this.threadPoolExecutor,onHoldIVRs, onHoldTimeSeconds);
        this.start();
    }

    /**
     * Starts OnHoldIVR threads
     *
     * @param threadPoolExecutor the dispatcher threadpool. Its the same pool than other Agents Pool
     * @param quantity quantity of IVRS to tart
     * @param onHoldTimeSeconds time in secods that a Customer is OnHold
     */
    private void runOnHoldIvrs(ThreadPoolExecutor threadPoolExecutor,final Integer quantity, Integer onHoldTimeSeconds){
        for (int i=0 ; i<quantity; i ++) {
            this.onHoldIVRList.add(new OnHoldIVR(this.customersCalls, onHoldTimeSeconds));
        }
        onHoldIVRList.forEach(onHoldIVR -> threadPoolExecutor.execute(onHoldIVR));

    }


    /**
     * Creates de Agent Object and adds it to Dispatcher Agents list.
     * @param agentType is the type for the new connected agent
     * @return true if Dispatcher could add the new Agent correctly. false if not.
     */
    public Boolean connectAgent(AgentType agentType){
            Agent agent;
            switch (agentType){
                case OPERATOR:
                    agent = new OperatorAgent();
                    break;
                case SUPERVISOR:
                    agent = new SupervisorAgent();
                    break;
                case DIRECTOR:
                    agent = new DirectorAgent();
                    break;
                default:
                    logger.error("Invalid AgentType."); //This is only for bestPractices purposes. The code is comparing an enum.
                    return false;
            }
            agents.add(agent);
            logger.info("New Agent added Successfully. Role: " + agent.getAgentType());
            return true;
    }

    /**
     * Receives the Customer calls and routes it to the CustomerCalls pool
     * or rejects the call if the Dispatcer has not available threads for attend it.
     * @param customer the new Customer call
     */
    public void receiveCustomer(Customer customer){
        if(this.getWorkingThreadsCount()<maxSupportedCalls){
            this.dispatchCall(customer);
        }else{
            this.rejects(customer, RejectReason.CENTRAL_OVERLOAD);
        }
    }
    /**
     * Receives the new Customer and saves de call to be delegated to an Agent,.
     * @param customer represents a new call that is being received.
     */
    public void dispatchCall(Customer customer) {
        logger.info("Dispatch new customer call of " + customer.getCallDuration() + " seconds");
        this.customersCalls.add(customer);
    }

    /**
     * Rejects a Customer call
     * @param customer Customer to be rejected
     * @param rejectReason rejec reason
     */
    private void rejects(Customer customer,RejectReason rejectReason){
        logger.info("Rejected Call");
        this.rejectedCustomers.put(customer,rejectReason);
    }

    /**
     * Lists the rejected customer calls
     * @return a Map of rejected Customer with its reject reason.
     */
    public Map<Customer, RejectReason> getRejectedCustomers(){
        return this.rejectedCustomers;
    }

    /**
     * Allows the dispatcher run method to execute
     */
    public void start() {
        this.active = true;
    }

    /**
     * Stops the Agent threads and the dispatcher run method immediately
     */
    public void stop() {
        this.active = false;
        this.threadPoolExecutor.shutdown();
    }

    public Boolean getActive() {
        return active;
    }
    /**
     * Gets the sum of all rejected calls
     */
    public Integer countRejectedCustomerCallsCount(){
        return this.rejectedCustomers.size();
    }

    /**
     * Gets the sum of all processed calls for all Agents.
     * @return sum of all processed calls
     */
    public Integer countDispatchedCustomersCount(){
        return this.agents.stream().mapToInt(agent -> agent.getAttendedCustomers().size()).sum();
    }

    /**
     * Gets the number of Connected Agents
     * @return the size of the agents List.
     */
    public Integer getConnectedAgentsCount(){
        return agents.size();
    }

    /**
     * Gets the number of active Thread on the Agents threadPool
     * @return active threads count
     */
    public Integer getWorkingThreadsCount(){
        return threadPoolExecutor.getActiveCount();
    }

    /**
     * Gets the number of customers waiting for being dispatched.
     * @return pooled customer calls
     */
    public Integer getPendingPooledCustomersSize(){
        return customersCalls.size();
    }

    /**
     * This is the method that runs on the thread.
     * If the incoming calls queue is not empty, then it searches for and available employee to take the call.
     * Calls will queue up until some workers becomes available.
     */
    @Override
    public void run() {
        while (getActive()) {
            if (!this.customersCalls.isEmpty()) {
                Agent agent = this.callAttendStrategy.findEmployee(this.agents);
                Customer customer = this.customersCalls.poll();
                if (agent != null) {
                    try {
                        agent.delegateCustomer(customer);
                        this.threadPoolExecutor.execute(agent);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        this.customersCalls.addFirst(customer);
                    }
                }else{

                    logger.info("Not Available Agent Found");
                    OnHoldIVR onHoldIVR = this.callAttendStrategy.findOnHoldIvr(this.onHoldIVRList);
                    if(onHoldIVR != null && customer.getHoldedTimes() < maxHoldedTimes){
                        onHoldIVR.addToHoldQueue(customer);
                    }else{
                        this.rejects(customer, RejectReason.UNAVAILABLE_AGENTS);
                    }
                }

            }
        }
    }


}
