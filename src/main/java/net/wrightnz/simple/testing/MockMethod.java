package net.wrightnz.simple.testing;

/**
 * Represents a mock of a given method.
 * 
 * Example usage:
 * If the method to be mocked is:
 * <code> 
 * Point getCentre(Shape shape);
 * </code>
 * The mock contractor will be:
 * <code>
 * Point expected = new Point(19. 24);
 * MockMethod&lt;Point&gt; example = new MockMethod(expected, "getCentre", Shape.class);
 * </code>
 * 
 * @author Richard Wright
 * @param <T> the return type of this method.
 */
public class MockMethod<T> {

  private final T returned;
  private final String name;
  private final Class<?>[] parameterTypes;
  private int invocationCount;

  /**
   * Creates a mock of a given method. Use this constructor for methods that have
   * no parameters.
   * @param returned the value the mocked method should return.
   * @param name the name of the method being mocked as a String.
   */
  public MockMethod(T returned, String name) {
    this(returned, name, new Class<?>[0]);
  }

  /**
   * Creates a mock of a given method. Use this constructor for methods that have
   * parameters.
   * @param returned the value the mocked method should return.
   * @param name the name of the method being mocked as a String.
   * @param parameterTypes the types of the parameters the method has (used to 
   *         match the method signature).
   */
  public MockMethod(T returned, String name, Class<?>... parameterTypes) {
    this.name = name;
    this.returned = returned;
    this.parameterTypes = parameterTypes;
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

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

}
