package net.wrightnz.simple.testing;

/**
 *
 * @author Richard Wright
 * @param <T> the return type of this method.
 */
public class MockMethod<T> {

    private String name;
    private T returned;

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

}
