package ar.com.exam.callcenter.model;

import ar.com.exam.callcenter.exception.BusyAgentException;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class AgentTest {

    @Test
    public void testOperatorValidCreation() {
        Agent operator = new OperatorAgent();

        assertNotNull(operator);
        assertEquals(AgentType.OPERATOR, operator.getAgentType());
        assertEquals(AgentStatus.AVAILABLE, operator.getAgentStatus());
    }
    @Test
    public void testSupervisorValidCreation() {
        Agent supervisor = new SupervisorAgent();

        assertNotNull(supervisor);
        assertEquals(AgentType.SUPERVISOR, supervisor.getAgentType());
        assertEquals(AgentStatus.AVAILABLE, supervisor.getAgentStatus());
    }

    @Test
    public void testDirectorValidCreation() {
        Agent director = new DirectorAgent();

        assertNotNull(director);
        assertEquals(AgentType.DIRECTOR, director.getAgentType());
        assertEquals(AgentStatus.AVAILABLE, director.getAgentStatus());

    }

    @Test
    public void testAgentAttendWhileAvailable() throws InterruptedException,Exception {
        Agent operator = new OperatorAgent();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(operator);
        operator.delegateCustomer(Customer.createCustomerWithRamdomCallDuration(0, 1));

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(1, operator.getAttendedCustomers().size());
    }

    @Test
    public void testAgentStatesWhileAttend() throws Exception {
        Agent operator = new OperatorAgent();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        assertEquals(AgentStatus.AVAILABLE, operator.getAgentStatus());
        TimeUnit.SECONDS.sleep(1);
        Customer customer = Customer.createCustomerWithRamdomCallDuration(2, 3);
        operator.delegateCustomer(customer);
        assertEquals(AgentStatus.BUSY, operator.getAgentStatus());
        executorService.execute(operator);
        TimeUnit.SECONDS.sleep(customer.getCallDuration()+1);
        assertEquals(AgentStatus.AVAILABLE, operator.getAgentStatus());

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(1, operator.getAttendedCustomers().size());
    }

    @Test(expected = BusyAgentException.class)
    public void testTryesToAssignMoreThanOneCustomer() throws InterruptedException {
        Agent operator = new OperatorAgent();
        assertEquals(AgentStatus.AVAILABLE, operator.getAgentStatus());
        TimeUnit.SECONDS.sleep(1);
        Customer customer = Customer.createCustomerWithRamdomCallDuration(2, 3);
        operator.delegateCustomer(customer);
        assertEquals(AgentStatus.BUSY,operator.getAgentStatus());
        operator.delegateCustomer(customer);

    }
}
