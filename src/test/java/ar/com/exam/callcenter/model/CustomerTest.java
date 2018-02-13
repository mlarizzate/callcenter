package ar.com.exam.callcenter.model;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class CustomerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCustomerCreationWithInvalidCalldurationParameter() {
        new Customer(-1);
    }

    @Test(expected = NullPointerException.class)
    public void testCustomerCreationWithNullCallDurationParameter() {
        new Customer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomCustomerCreationWithInvalidMinDurationParameter() {
        Customer.createCustomerWithRamdomCallDuration(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomCustomerCreationWithInvalidMaxDurationParameter() {
        Customer.createCustomerWithRamdomCallDuration(1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomCustomerCreationWithMinDurationHigherThanMaxDuration() {
        Customer.createCustomerWithRamdomCallDuration(2, 1);
    }

    @Test
    public void testRandomCustomerCreationWithValidDurations() {
        Integer min = 5;
        Integer max = 10;
        Customer customer = Customer.createCustomerWithRamdomCallDuration(min, max);

        assertNotNull(customer);
        assertTrue(min <= customer.getCallDuration());
        assertTrue(customer.getCallDuration() <= max);
    }
}
