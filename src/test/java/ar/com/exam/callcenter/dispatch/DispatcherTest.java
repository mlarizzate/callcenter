package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DispatcherTest {

    private static final int MAX_CONNECTIONS=10;

    private static final int CALLS_QTY = 10;

    private static final int MIN_CALL_DURATION = 5;

    private static final int MAX_CALL_DURATION = 10;

    @Test
    public void testDispatcherCreationWithoutAgents() {
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        assertEquals(String.valueOf(0),String.valueOf(dispatcher.getConnectedAgentsCount()));
    }

    @Test
    public void testDispatcherCreationAndRunning() {
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        threadPoolExecutor.execute(dispatcher);
        assertEquals(true, dispatcher.getActive());
        assertEquals(String.valueOf(1),String.valueOf(threadPoolExecutor.getActiveCount()));
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
        List<AgentType> agentList = buildAgentList();
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS);
        agentList.forEach(agent -> dispatcher.connectAgent(agent));
        TimeUnit.SECONDS.sleep(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(dispatcher);
        TimeUnit.SECONDS.sleep(1);

        buildCustomerList().forEach(customer -> {
            dispatcher.dispatch(customer);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                fail();
            }
        });

        executorService.awaitTermination(MAX_CALL_DURATION * 2, TimeUnit.SECONDS);
        Integer dispatchedCustomers = dispatcher.countDispatchedCustomersCount();
        assertEquals(String.valueOf(CALLS_QTY), String.valueOf(dispatchedCustomers));
    }



    private static List<AgentType> buildAgentList() {
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