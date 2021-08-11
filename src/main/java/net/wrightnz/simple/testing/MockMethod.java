package net.wrightnz.simple.testing;

/**
 *
 * @author Richard Wright
 * @param <T> the return type of this method.
 */
public class MockMethod<T> {


    private final String name;
    private final T returned;
    private int invocationCount;

    public MockMethod(String name, T returned) {
        this.name = name;
        this.returned = returned;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the returned
     */
    public T getReturned() {
        return returned;
    }

    /**
     * @return the invocationCount
     */
    public int getInvocationCount() {
        return invocationCount;
    }

    /**
     *
     */
    public void incrementInvocationCount() {
        this.invocationCount++;
    }

}
