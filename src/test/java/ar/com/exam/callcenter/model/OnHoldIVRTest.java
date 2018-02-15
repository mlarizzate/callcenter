package ar.com.exam.callcenter.model;

import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class OnHoldIVRTest {

    @Test
    public void testValidIVRCreation(){
        final Integer onHoldTimeSeconds = 5;
        OnHoldIVR ivr = new OnHoldIVR(new ConcurrentLinkedDeque<>(), onHoldTimeSeconds);
        assertEquals(AgentType.IVR, ivr.getAgentType());
        assertEquals(AgentStatus.AVAILABLE, ivr.getAgentStatus());
    }

    @Test
    public void testIVRAttendWhileAvailable() throws Exception {
        final Integer onHoldTimeSeconds = 5;
        OnHoldIVR ivr = new OnHoldIVR(new ConcurrentLinkedDeque<>(), onHoldTimeSeconds);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(ivr);
        ivr.delegateCustomer(Customer.createCustomerWithRamdomCallDuration(0, 1));

        executorService.awaitTermination(10, TimeUnit.SECONDS);
        assertEquals(1, ivr.getAttendedCustomers().size());
    }
}
