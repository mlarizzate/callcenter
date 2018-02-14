package ar.com.exam.callcenter.model;


import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Customer extends Thread{
    private Integer callDuration;

    /**
     * Creates a new Customer. Its call duration will be received
     *
     * @param callDuration seconds. Must be zero or higher
     */
    public Customer(Integer callDuration) {

        Validate.notNull(callDuration);
        Validate.isTrue(callDuration >= 0);
        this.callDuration = callDuration;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    /**
     * Creates a new Customer
     *o matching bean found for vi
     * @param minDuration  Must be zero or higher. Seconds.
     * @param maxDuration Must be equal or higher than minDuration. Seconds
     * @return A new Customer.Its call duration will be randomly configured.
     */
    public static Customer createCustomerWithRamdomCallDuration(Integer minDuration, Integer maxDuration) {
        Validate.isTrue(maxDuration >= minDuration && minDuration >= 0);
        return new Customer(ThreadLocalRandom.current().nextInt(minDuration, maxDuration + 1));
    }

    /**
     * Creates a List containing Randomly generated Customers
     *
     * @param listSize quantity o Customers
     * @param minDuration Must be zero or higher. Seconds.
     * @param maxDuration Must be equal or higher than minDuration. Seconds
     * @return A List containing Randomly generated Customers.
     */
    public static List<Customer> generateCustomers(Integer listSize, Integer minDuration, Integer maxDuration) {
        Validate.isTrue(listSize >= 0);
        List<Customer> callList = new ArrayList<>();

        IntStream.range(0,listSize).forEach(i->{ //This could be easily make with a for sentence but I thought it could be a good oportunity to show my Java 8 Experience
            callList.add(createCustomerWithRamdomCallDuration(minDuration, maxDuration));
        });

        return callList;
    }
}
