package net.wrightnz.simple.testing;

import org.apache.bcel.generic.Type;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public final class MockMethodUtils {

  public static final Map<String, String> WRAPPER_3_PRIMITIVE = Map.of(
      "java.lang.Integer", "int",
      "java.lang.Double", "double",
      "java.lang.Character", "char",
      "java.lang.Float", "float",
      "java.lang.Byte", "byte",
      "java.lang.Long", "long",
      "java.lang.Boolean", "boolean"
  );

  private MockMethodUtils() {
  }

  public static MockMethod<?> getMockMethod(Method method, MockMethod<?>... mocks) {
    for (MockMethod<?> mock : mocks) {
      Class<?> mockReturnedClass = mock.getReturned().getClass();
      Class<?> expectedReturnedType = method.getReturnType();
      if (mock.getName().equals(method.getName()) && isSameType(mockReturnedClass, expectedReturnedType)) {
        return mock;
      }
    }
    return null;
  }

  /**
   * @param paramTypes input types.
   * @return array of Objects containing default values for all the input types.
   */
  public static Object[] getArgs(List<Type> paramTypes) {
    Object[] args = new Object[paramTypes.size()];
    for (int i = 0; i < args.length; i++) {
      Type paramType = paramTypes.get(i);
      args[i] = MockConsts.TYPE_2_DEFAULT_VALUE.get(paramType);
    }
    // For Debugging: System.out.printf(">>>>> Args length %d %n", args.length);
    return args;
  }


  private static boolean isSameType(Class<?> mockReturnedClass, Class<?> expectedReturnedType) {
    // For Debugging: System.out.printf(">>> mockReturnedClass: %s, expectedReturnType: %s %n", mockReturnedClass, expectedReturnedType);
    if (expectedReturnedType.isPrimitive()) {
      String primitiveType = WRAPPER_3_PRIMITIVE.get(mockReturnedClass.getCanonicalName());
      if (expectedReturnedType.toString().equals(primitiveType)) {
        return true;
      }
    }
    return mockReturnedClass.getTypeName().equals(expectedReturnedType.getTypeName());
  }

}
