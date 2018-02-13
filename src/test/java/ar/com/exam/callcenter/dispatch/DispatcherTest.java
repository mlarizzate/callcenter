package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DispatcherTest {

    private static final int MAX_CONNECTIONS=10;

    private static final int CALLS_QTY = 10;

    private static final int MIN_CALL_DURATION = 5;

    private static final int MAX_CALL_DURATION = 10;

    @Test(expected = IllegalArgumentException.class)
    public void testDispatcherCreationWithEmptyEmployeesList() {
        new Dispatcher(new ArrayList<>(), MAX_CONNECTIONS);
    }

    @Test(expected = NullPointerException.class)
    public void testDispatcherCreationWithNullStrategy() {
        new Dispatcher(new ArrayList<>(), null);
    }

    @Test
    public void testAddOperatorSuccessfully(){
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        assertEquals(true,dispatcher.connectAgent(AgentType.OPERATOR));
    }

    @Test
    public void testAddSupervisorSuccessfully(){
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        assertEquals(true,dispatcher.connectAgent(AgentType.SUPERVISOR));
    }    @Test
    public void testAddDirectorSuccessfully(){
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        assertEquals(true,dispatcher.connectAgent(AgentType.DIRECTOR));
    }

    @Test
    public void testDispatchCallsToEmployees() throws InterruptedException {
        List<AgentType> agentList = buildEmployeeList();
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        agentList.forEach(agent -> dispatcher.connectAgent(agent));
        TimeUnit.SECONDS.sleep(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(dispatcher);
        TimeUnit.SECONDS.sleep(1);

        buildCustomerList().forEach(call -> {
            dispatcher.dispatch(call);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                fail();
            }
        });

        executorService.awaitTermination(MAX_CALL_DURATION * 2, TimeUnit.SECONDS);
        Integer dispatchedCustomers = dispatcher.countDispatchedCustomers();
        assertEquals(String.valueOf(CALLS_QTY), String.valueOf(dispatchedCustomers));
    }



    private static List<AgentType> buildEmployeeList() {
        return Arrays.asList(AgentType.OPERATOR,
                             AgentType.OPERATOR,
                             AgentType.OPERATOR,
                             AgentType.OPERATOR,
                             AgentType.OPERATOR,
                             AgentType.OPERATOR,
                             AgentType.OPERATOR,
                             AgentType.SUPERVISOR,
                             AgentType.SUPERVISOR,
                             AgentType.DIRECTOR);
    }

    private static List<Customer> buildCustomerList() {
        return Customer.generateCustomers(CALLS_QTY, MIN_CALL_DURATION, MAX_CALL_DURATION);
    }

}