package ar.com.exam.callcenter.model;

import org.apache.commons.lang3.Validate;

public class OperatorAgent extends Agent {
    public OperatorAgent() {
        super();
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.OPERATOR;
    }

}
