package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.*;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DefaultCustomerDispatchStrategyTest {
    private CustomerDispatchStrategy callAttendStrategy;

    public DefaultCustomerDispatchStrategyTest() {
        this.callAttendStrategy = new DefaultCustomerDispatchStrategy();
    }

    @Test
    public void testAssignToOperator() {
        Agent director = new DirectorAgent();
        Agent supervisor = new SupervisorAgent();
        Agent operator = new OperatorAgent();
        List<Agent> agentList = Arrays.asList(operator, supervisor, director);

        Agent agent = this.callAttendStrategy.findEmployee(agentList);

        assertNotNull(agent);
        assertEquals(AgentType.OPERATOR, agent.getAgentType());
    }

    @Test
    public void testAssignToSupervisor() {
        Agent operator = mock(Agent.class);
        when(operator.getAgentStatus()).thenReturn(AgentStatus.BUSY);
        Agent director = new DirectorAgent();
        Agent supervisor = new SupervisorAgent();
        List<Agent> agentList = Arrays.asList(operator, supervisor, director);

        Agent agent = this.callAttendStrategy.findEmployee(agentList);

        assertNotNull(agent);
        assertEquals(AgentType.SUPERVISOR, agent.getAgentType());
    }

    @Test
    public void testAssignToDirector() {
        Agent operator = mockBusyEmployee(AgentType.OPERATOR);
        Agent supervisor = mockBusyEmployee(AgentType.SUPERVISOR);
        Agent director = new DirectorAgent();
        List<Agent> employeeList = Arrays.asList(operator, supervisor, director);

        Agent agent = this.callAttendStrategy.findEmployee(employeeList);

        assertNotNull(agent);
        assertEquals(AgentType.DIRECTOR, agent.getAgentType());
    }

    @Test
    public void testAssignToNone() {
        Agent operator = mockBusyEmployee(AgentType.OPERATOR);
        Agent supervisor = mockBusyEmployee(AgentType.SUPERVISOR);
        Agent director = mockBusyEmployee(AgentType.DIRECTOR);
        List<Agent> employeeList = Arrays.asList(operator, supervisor, director);

        Agent employee = this.callAttendStrategy.findEmployee(employeeList);

        assertNull(employee);
    }

    @Test
    public void testFindOnHoldIVR(){
        final Integer onHoldTimeSeconds = 5;
        OnHoldIVR listedIvr = new OnHoldIVR(new ConcurrentLinkedDeque<>(), onHoldTimeSeconds);
        ConcurrentLinkedDeque<OnHoldIVR> onHoldIVRList = new ConcurrentLinkedDeque<>();
        onHoldIVRList.add(listedIvr);

        OnHoldIVR ivr = this.callAttendStrategy.findOnHoldIvr(onHoldIVRList);
        assertNotNull(ivr);
    }

    @Test
    public void testUnavailableOnHoldIVR(){
        ConcurrentLinkedDeque<OnHoldIVR> onHoldIVRList = new ConcurrentLinkedDeque<>();
        onHoldIVRList.add(mockBusyIvr());

        OnHoldIVR ivr = this.callAttendStrategy.findOnHoldIvr(onHoldIVRList);
        assertNull(ivr);
    }

    private static OnHoldIVR mockBusyIvr(){
        OnHoldIVR ivr = mock(OnHoldIVR.class);
        when(ivr.getAgentType()).thenReturn(AgentType.IVR);
        when(ivr.getAgentStatus()).thenReturn(AgentStatus.BUSY);
        return ivr;
    }

    private static Agent mockBusyEmployee(AgentType agentType) {
        Agent employee = mock(Agent.class);
        when(employee.getAgentType()).thenReturn(agentType);
        when(employee.getAgentStatus()).thenReturn(AgentStatus.BUSY);
        return employee;
    }
}
