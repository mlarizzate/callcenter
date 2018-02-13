package ar.com.exam.callcenter.model;

public enum AgentType {
    OPERATOR(1, "Operator"), SUPERVISOR(2, "Supervisor"), DIRECTOR(3, "Director");

    Integer priority;
    String description;

    AgentType(Integer priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }
    public String getDescription(){
        return  description;
    }
}