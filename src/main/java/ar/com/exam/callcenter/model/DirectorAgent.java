package ar.com.exam.callcenter.model;

public class DirectorAgent extends Agent {
    public DirectorAgent() {
        super();
    }
    @Override
    public AgentType getAgentType() {
        return AgentType.DIRECTOR;
    }
}
