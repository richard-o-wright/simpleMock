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
import java.util.Map;

public final class SimpleMockUtils {

  public static final Map<String, String> WRAPPER_3_PRIMITIVE = Map.of(
      "java.lang.Integer", "int",
      "java.lang.Double", "double",
      "java.lang.Character", "char",
      "java.lang.Float", "float",
      "java.lang.Byte", "byte",
      "java.lang.Long", "long",
      "java.lang.Boolean", "boolean"
  );

  private SimpleMockUtils() {
  }

  public static void pushType(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Type returnType, MockMethod<?> mockMethod)
      throws NoSuchMethodException {
    // For Debugging: System.out.printf("Called: pushType(ConstantPoolGen, InstructionFactory, InstructionList, %s, %s)%n", returnType, mockMethod);
    Object value = MockConsts.TYPE_2_DEFAULT_VALUE.get(returnType);
    if (returnType.equals(Type.BOOLEAN)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Boolean.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (boolean) value));
      }
    } else if (returnType.equals(Type.BYTE)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Byte.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (byte) value));
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
        code.append(new PUSH(constantPool, (int) value));
      }
    } else if (returnType.equals(Type.LONG)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Long.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (long) value));
      }
    } else if (returnType.equals(Type.FLOAT)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Float.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (float) value));
      }
    } else if (returnType.equals(Type.DOUBLE)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Double.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, (double) value));
      }
    } else if (returnType.equals(Type.STRING)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, mockMethod.getReturned().toString()));
      } else {
        code.append(new PUSH(constantPool, (String) value));
      }
    } else if (!returnType.equals(Type.VOID)) {
      if (mockMethod != null) {
        pushObjectOntoStack(constantPool, factory, code, mockMethod);
      } else {
        code.append(new ACONST_NULL());
      }
    }
  }

  public static boolean hasNullConstructor(Class<?> clazz) {
    Constructor[] constructors = clazz.getConstructors();
    for (Constructor constructor : constructors) {
      if (constructor.getParameterCount() == 0) {
        return true;
      }
    }
    return false;
  }

  private static void pushObjectOntoStack(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, MockMethod<?> mockMethod)
      throws NoSuchMethodException {
    Class returnedClass = mockMethod.getReturned().getClass();
    Constructor constructor;
    if (SimpleMockUtils.hasNullConstructor(returnedClass)) {
      constructor = returnedClass.getConstructor();
    } else {
      constructor = returnedClass.getConstructors()[0];
    }
    Parameters parameters = new Parameters(constructor.getParameters());
    code.append(factory.createNew(returnedClass.getName()));
    code.append(InstructionConst.DUP);
    for (Type type : parameters.getTypes()) {
      pushType(constantPool, factory, code, type, null);
    }
    code.append(factory.createInvoke(returnedClass.getName(), "<init>", Type.VOID, parameters.getTypes(), Const.INVOKESPECIAL));
    code.append(InstructionConst.NOP);
  }

  public static boolean isSameType(Class<?> mockReturnedClass, Class<?> expectedReturnedType) {
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
