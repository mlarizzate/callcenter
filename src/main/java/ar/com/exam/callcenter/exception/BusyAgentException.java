package ar.com.exam.callcenter.exception;

/**
 * Exception is thrown when a process tryes to set more than one Customer to an Agent.
 */
public class BusyAgentException extends RuntimeException{
    public BusyAgentException() {
        super("Assigned Agent can't receive more than one Customer at the same time");
    }
}
