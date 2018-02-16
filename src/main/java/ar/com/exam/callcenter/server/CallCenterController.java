package ar.com.exam.callcenter.server;

import ar.com.exam.callcenter.dispatch.Dispatcher;
import ar.com.exam.callcenter.model.AgentType;
import ar.com.exam.callcenter.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CallCenterController {

    private Integer maxCalls;

    private Dispatcher dispatcher;

    @Autowired
    public CallCenterController(Integer maxCalls, Dispatcher dispatcher) {
        this.maxCalls = maxCalls;
        this.dispatcher = dispatcher;
    }

    /**
     * Shows statistic on an easy way.
     * @param model variables to be shown on status page
     * @return view name
     */
    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public String status(Model model) {
        model.addAttribute("maxCalls", maxCalls);
        model.addAttribute("connectedAgents", dispatcher.getConnectedAgentsCount());
        model.addAttribute("dispatchedCustomers", dispatcher.countDispatchedCustomersCount());
        model.addAttribute("pendingCustomers", dispatcher.getPendingPooledCustomersSize());
        model.addAttribute("activeCalls", dispatcher.getWorkingThreadsCount());
        model.addAttribute("rejectedCalls",dispatcher.countRejectedCustomerCallsCount());
        model.addAttribute("rejectedCallsReasons", dispatcher.getRejectedCustomers());
        return "status";
    }

    /**
     * Avoids Agents to connect on the platform
     * @param agentType The type of agent to be created
     * @param model variables to be shown on status page
     * @return view name
     */
    @RequestMapping(method = RequestMethod.GET, value ="/connect")
    public String connectAgent(@RequestParam("type") String agentType, Model model){
        try{
            dispatcher.connectAgent(AgentType.valueOf(agentType.toUpperCase()));
        }catch (IllegalArgumentException e){

        }

        model.addAttribute("message", "Agent Successfully Connected");
        return "genericMessage";
    }

    /**
     * Simulates customer calls
     * @param model variables to be shown on status page
     * @return view name
     */
    @RequestMapping(method = RequestMethod.GET, value ="/simulateCustomer")
    public String simulateCustomer(Model model){
        Customer cu = Customer.createCustomerWithRamdomCallDuration(5,10);
        dispatcher.receiveCustomer(cu);
        model.addAttribute("message", "SimulatedCall");
        return "genericMessage";
    }

}
