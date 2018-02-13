package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private static Integer maxSupportedAgents;

    private Boolean active;

    private ExecutorService executorService;

    private ConcurrentLinkedDeque<Agent> agents;

    private ConcurrentLinkedDeque<Customer> customersCalls;

    private CustomerDispatchStrategy callAttendStrategy;

    public Dispatcher(Integer maxSupportedAgents){
        Dispatcher.maxSupportedAgents = maxSupportedAgents;
        this.agents = new ConcurrentLinkedDeque<>();
        this.customersCalls = new ConcurrentLinkedDeque<>();
        this.callAttendStrategy = new DefaultCustomerDispatchStrategy();
        this.executorService = Executors.newFixedThreadPool(maxSupportedAgents);
        this.start();
    }

    public Dispatcher(List<Agent> agents, Integer maxSupportedAgents) {
        this(maxSupportedAgents);
        Validate.notEmpty(agents);
        Validate.notNull(agents);
        Validate.notNull(callAttendStrategy);
        this.agents = new ConcurrentLinkedDeque(agents);
        this.customersCalls = new ConcurrentLinkedDeque<>();

    }

    /**
     * Creates de Agent Object and starts its own thread.
     * @param agentType is the type for the new connected agent
     * @return true if Dispatcher could add the new Agent correctly. false if not.
     */
    public synchronized Boolean connectAgent(AgentType agentType){
        try {
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
                default:throw new IllegalArgumentException("Unexpected AgentType Received");
            }
            agents.add(agent);
            this.executorService.execute(agent);
            logger.info("New Agent added Successfully. Role: " + agent.getAgentType());
            return true;
        }catch (Exception e){
            logger.error("Unexpected Error occurred when connecting agent");
            return false;

        }
    }
    public synchronized void dispatch(Customer customer) {
        logger.info("Dispatch new customer call of " + customer.getCallDuration() + " seconds");
        this.customersCalls.add(customer);
    }

    /**
     * Allows the dispatcher run method to execute
     */
    public synchronized void start() {
        this.active = true;
    }

    /**
     * Stops the Agent threads and the dispatcher run method immediately
     */
    public synchronized void stop() {
        this.active = false;
        this.executorService.shutdown();
    }

    public synchronized Boolean getActive() {
        return active;
    }

    public Integer countDispatchedCustomers(){
        return this.agents.stream().mapToInt(agent -> agent.getAttendedCustomers().size()).sum();
    }

    public Integer getAvailableAgents(){
        return agents.size();
    }

    /**
     * This is the method that runs on the thread.
     * If the incoming calls queue is not empty, then it searches for and available employee to take the call.
     * Calls will queue up until some workers becomes available.
     */
    @Override
    public void run() {
        while (getActive()) {
            if (!this.agents.isEmpty() && !this.customersCalls.isEmpty()) {
                Agent employee = this.callAttendStrategy.findEmployee(this.agents);
                if (employee == null) {
                    continue;
                }
                Customer call = this.customersCalls.poll();
                try {
                    employee.attend(call);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    this.customersCalls.addFirst(call);
                }
            }
        }
    }
}
