package net.wrightnz.simple.testing;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
      if (mock.getName().equals(method.getName())
              && isSameType(mockReturnedClass, expectedReturnedType)
              && hasSameParameters(method, mock)) {
        return mock;
      }
    }
    return null;
  }

  public static boolean hasSameParameters(Method method, MockMethod<?> mock) {
    Class<?>[] methodParams = method.getParameterTypes();
    Class<?>[] mockTypes = mock.getParameterTypes();
    if (method.getParameterCount() != mockTypes.length) {
      return false;
    }
    for (int i = 0; i < methodParams.length; i++) {
      if (methodParams[i] != mockTypes[i]) {
        return false;
      }
    }
    return true;
  }
  
  public static void pushType(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Type returnType, MockMethod<?> mockMethod) {
    // For Debugging: System.out.printf("Called: pushType(ConstantPoolGen, InstructionFactory, InstructionList, %s, %s)%n", returnType, mockMethod);
    Object value = MockConsts.TYPE_2_DEFAULT_VALUE.get(returnType);
    if (returnType.equals(Type.BOOLEAN)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Boolean.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (boolean)value));
      }
    } else if (returnType.equals(Type.BYTE)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Byte.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (byte)value));
      }
    } else if (returnType.equals(Type.CHAR)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        // For Debugging: System.out.printf(">>>>>>>>>> %c %n", ((Character) mockMethod.getReturned()).charValue());
        code.append(new PUSH(constantPool, ((Character) mockMethod.getReturned())));
      } else {
        code.append(new PUSH(constantPool, (char) value));
      }
    } else if (returnType.equals(Type.INT)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Integer.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (int)value));
      }
    } else if (returnType.equals(Type.LONG)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Long.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (long)value));
      }
    } else if (returnType.equals(Type.FLOAT)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Float.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (float)value));
      }
    } else if (returnType.equals(Type.DOUBLE)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Double.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (double)value));
      }
    } else if (returnType.equals(Type.STRING)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, mockMethod.getReturned().toString()));
      } else {
        code.append(new PUSH(constantPool, (String)value));
      }
    } else {
      if (mockMethod != null) {
        pushObjectOntoStack(factory, code, mockMethod);
      }
      code.append(new ACONST_NULL());
    }
  }

  private static void pushObjectOntoStack(InstructionFactory factory, InstructionList code, MockMethod<?> mockMethod) {
    Class returnedClass = mockMethod.getReturned().getClass();
    try {
      // ToDo: Add support for returned Objects with parameters.
      Constructor constructor = returnedClass.getConstructor();
    } catch (NoSuchMethodException e) {
      throw new FailedToMockException(
          "Sorry only returned object with null constructors are currently supported. "
              + "Unsupported returned object type: " + returnedClass.getName(),
          e
      );
    }
    code.append(factory.createNew(returnedClass.getName()));
    code.append(InstructionConst.DUP);
    code.append(factory.createInvoke(returnedClass.getName(), "<init>", Type.VOID, Type.NO_ARGS, Const.INVOKESPECIAL));
    code.append(InstructionConst.NOP);
    code.append(InstructionFactory.createReturn(Type.OBJECT));
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
