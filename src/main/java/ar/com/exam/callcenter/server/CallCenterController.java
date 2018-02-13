package ar.com.exam.callcenter.server;

import ar.com.exam.callcenter.dispatch.Dispatcher;
import ar.com.exam.callcenter.model.AgentType;
import ar.com.exam.callcenter.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CallCenterController {

    private Integer maxCalls;

    private Dispatcher dispatcher;

    @Autowired
    public CallCenterController(Integer maxCalls, Dispatcher dispatcher) {
        this.maxCalls = maxCalls;
        this.dispatcher = dispatcher;
    }

    @RequestMapping("/status")
    public String status(Map<String, Object> model) {

        model.put("maxCalls", maxCalls);
        model.put("connectedAgents", dispatcher.getAvailableAgents());
        model.put("dispatchedCustomers", dispatcher.countDispatchedCustomers());
        return "status";
    }

    @RequestMapping("/connect")
    public String connectAgent(@RequestParam("type") String agentType){
        dispatcher.connectAgent(AgentType.valueOf(agentType.toUpperCase()));
        return "connected";
    }

    @RequestMapping("/simulateCustomer")
    public String simulateCustomer(){
        Customer cu = Customer.createCustomerWithRamdomCallDuration(5,10);
        dispatcher.dispatch(cu);
        return "dispatched";
    }

}
