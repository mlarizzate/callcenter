package ar.com.exam.callcenter.model;

import org.apache.commons.lang3.Validate;

public class SupervisorAgent  extends Agent{
    public SupervisorAgent() {
        super();
    }
    @Override
    public AgentType getAgentType() {
        return AgentType.SUPERVISOR;
    }
}
