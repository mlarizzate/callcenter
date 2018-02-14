package ar.com.exam.callcenter.server;

import ar.com.exam.callcenter.dispatch.Dispatcher;
import ar.com.exam.callcenter.model.AgentType;
import ar.com.exam.callcenter.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/status")
    public String status(Model model) {
        model.addAttribute("maxCalls", maxCalls);
        model.addAttribute("connectedAgents", dispatcher.getAvailableAgentsCount());
        model.addAttribute("dispatchedCustomers", dispatcher.countDispatchedCustomersCount());
        model.addAttribute("pendingCustomers", dispatcher.getPendingPooledCustomersSize());
        model.addAttribute("activeCalls", dispatcher.getWorkingThreadsCount());
        return "status";
    }

    @RequestMapping("/connect")
    public String connectAgent(@RequestParam("type") String agentType, Model model){
        dispatcher.connectAgent(AgentType.valueOf(agentType.toUpperCase()));

        model.addAttribute("message", "Agent Successfully Connected");
        return "genericMessage";
    }

    @RequestMapping("/simulateCustomer")
    public String simulateCustomer(Model model){
        Customer cu = Customer.createCustomerWithRamdomCallDuration(5,10);
        dispatcher.dispatch(cu);
        model.addAttribute("message", "SimulatedCall");
        return "genericMessage";
    }

}
