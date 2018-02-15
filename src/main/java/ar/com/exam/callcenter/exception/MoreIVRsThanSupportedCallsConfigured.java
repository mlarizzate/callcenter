package ar.com.exam.callcenter.exception;

public class MoreIVRsThanSupportedCallsConfigured extends RuntimeException {
    public MoreIVRsThanSupportedCallsConfigured() {
        super("Configuration file application.properties has been configured with more IVRS than supported Calls");
    }
}
