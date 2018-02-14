package ar.com.exam.callcenter.exception;

public class BusyAgentException extends RuntimeException{
    public BusyAgentException() {
        super("Assigned Agent can't receive more than one Customer at the same time");
    }
}
