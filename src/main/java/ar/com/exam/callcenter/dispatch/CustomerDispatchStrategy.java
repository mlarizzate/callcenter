package ar.com.exam.callcenter.dispatch;

import ar.com.exam.callcenter.model.Agent;

import java.util.Collection;

public interface CustomerDispatchStrategy {
    /**
     * Finds next available employee
     *
     * @param agentList available Agents
     * @return Next available agent to take on a call, or null if all employees are busy
     */
    Agent findEmployee(Collection<Agent> agentList);
}
