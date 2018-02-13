package ar.com.exam.callcenter.model;

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
    public void testAgentAttendWhileAvailable() throws InterruptedException {
        Agent operator = new OperatorAgent();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(operator);
        operator.attend(Customer.createCustomerWithRamdomCallDuration(0, 1));

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(1, operator.getAttendedCustomers().size());
    }

    @Test
    public void testAgentStatesWhileAttend() throws InterruptedException {
        Agent operator = new OperatorAgent();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(operator);
        assertEquals(AgentStatus.AVAILABLE, operator.getAgentStatus());
        TimeUnit.SECONDS.sleep(1);
        operator.attend(Customer.createCustomerWithRamdomCallDuration(2, 3));
        operator.attend(Customer.createCustomerWithRamdomCallDuration(0, 1));
        TimeUnit.SECONDS.sleep(1);
        assertEquals(AgentStatus.BUSY, operator.getAgentStatus());

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(2, operator.getAttendedCustomers().size());
    }
}
