package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.exception.MoreIVRsThanSupportedCallsConfigured;
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

    private static final int MAX_HOLDED_TIMES = 5;

    private static final int ONHOLD_IVRS_QTY = 3;

    private static final int ONHOLD_TIME = 5;

    @Test
    public void testDispatcherCreationWithoutAgents() {
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY,ONHOLD_TIME);
        assertEquals(String.valueOf(0),String.valueOf(dispatcher.getConnectedAgentsCount()));
    }


    @Test(expected = MoreIVRsThanSupportedCallsConfigured.class)
    public void testDispatcherCreationMoreIvrsThanSupportedConnections() {
        final int testMaxSupportedCalls = 5;
        final int testIvrsQty = 10;
        new Dispatcher(testMaxSupportedCalls, MAX_HOLDED_TIMES, testIvrsQty, ONHOLD_TIME);
    }

    @Test
    public void testDispatcherCreationWithoutIVRs() {
        final int zero = 0;
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, zero, ONHOLD_TIME);
        assertEquals(String.valueOf(0),String.valueOf(dispatcher.getWorkingThreadsCount()));
    }

    @Test
    public void testDispatcherCreationAndRunning() {
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY, ONHOLD_TIME);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        threadPoolExecutor.execute(dispatcher);
        assertEquals(true, dispatcher.getActive());
        assertEquals(String.valueOf(1),String.valueOf(threadPoolExecutor.getActiveCount()));
    }

    @Test
    public void testAddOperatorSuccessfully(){
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY, ONHOLD_TIME);
        assertEquals(true,dispatcher.connectAgent(AgentType.OPERATOR));
    }

    @Test
    public void testAddSupervisorSuccessfully(){
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY, ONHOLD_TIME);
        assertEquals(true,dispatcher.connectAgent(AgentType.SUPERVISOR));
    }

    @Test
    public void testAddDirectorSuccessfully(){
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY, ONHOLD_TIME);
        assertEquals(true,dispatcher.connectAgent(AgentType.DIRECTOR));
    }




    @Test
    public void testDispatchCallsToEmployees() throws InterruptedException {
        List<AgentType> agentList = buildAgentList();
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY, ONHOLD_TIME);
        agentList.forEach(agent -> dispatcher.connectAgent(agent));
        TimeUnit.SECONDS.sleep(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(dispatcher);
        TimeUnit.SECONDS.sleep(1);

        buildCustomerList().forEach(customer -> {
            dispatcher.dispatchCall(customer);
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

    @Test
    public void testDispatchCallsToIVRsAndRejectCalls() throws InterruptedException {
        Dispatcher dispatcher = new Dispatcher(MAX_CONNECTIONS, MAX_HOLDED_TIMES, ONHOLD_IVRS_QTY, ONHOLD_TIME);
        TimeUnit.SECONDS.sleep(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(dispatcher);
        TimeUnit.SECONDS.sleep(1);

        buildCustomerList().forEach(customer -> {
            dispatcher.dispatchCall(customer);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                fail();
            }
        });

        executorService.awaitTermination(MAX_CALL_DURATION * 2, TimeUnit.SECONDS);
        assertEquals(String.valueOf(CALLS_QTY), String.valueOf(dispatcher.getRejectedCustomers().size()));
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