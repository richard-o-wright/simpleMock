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

  public static final Map<String, Class<?>> WRAPPER_3_PRIMITIVE = Map.of(
      "java.lang.Boolean", boolean.class,
      "java.lang.Byte", byte.class,
      "java.lang.Short", short.class,
      "java.lang.Character", char.class,
      "java.lang.Integer", int.class,
      "java.lang.Long", long.class,
      "java.lang.Double", double.class,
      "java.lang.Float", float.class
  );

  private SimpleMockUtils() {
  }

  public static void pushValue(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Type returnType, Object value)
      throws NoSuchMethodException {
    if (value == null) {
      value = MockConsts.TYPE_2_DEFAULT_VALUE.get(returnType);
    }
    if (returnType.equals(Type.BOOLEAN)) {
      code.append(new PUSH(constantPool, (boolean) value));
    } else if (returnType.equals(Type.BYTE)) {
      code.append(new PUSH(constantPool, (byte) value));
    } else if (returnType.equals(Type.CHAR)) {
      code.append(new PUSH(constantPool, (char) value));
    } else if (returnType.equals(Type.INT)) {
      code.append(new PUSH(constantPool, (int) value));
    } else if (returnType.equals(Type.LONG)) {
      code.append(new PUSH(constantPool, (long) value));
    } else if (returnType.equals(Type.FLOAT)) {
      code.append(new PUSH(constantPool, (float) value));
    } else if (returnType.equals(Type.DOUBLE)) {
      code.append(new PUSH(constantPool, (double) value));
    } else if (returnType.equals(Type.STRING)) {
      code.append(new PUSH(constantPool, (String) value));
    } else if (!returnType.equals(Type.VOID)) {
      if (value != null) {
        pushObjectOntoStack(constantPool, factory, code, value);
      } else {
        code.append(new ACONST_NULL());
      }
    }
  }

  public static void pushMockReturnValue(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Type returnType, MockMethod<?> mockMethod)
      throws NoSuchMethodException {
    // For Debugging: System.out.printf("Called: pushMockReturnValue(ConstantPoolGen, InstructionFactory, InstructionList, %s, %s)%n", returnType, mockMethod);
    if (mockMethod != null) {
      pushValue(constantPool, factory, code, returnType, mockMethod.getReturned());
    } else {
      pushValue(constantPool, factory, code, returnType, null);
    }
  }

  public static boolean hasNullConstructor(Class<?> clazz) {
    Constructor<?>[] constructors = clazz.getConstructors();
    for (Constructor<?> constructor : constructors) {
      if (constructor.getParameterCount() == 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Push an instance of an arbitrary object onto the callstack.
   *
   * @param constantPool
   * @param factory
   * @param code
   * @param object       the object to create an instance of on the callstack.
   * @throws NoSuchMethodException (won't actually throw this as condition is checked and handled within the method.)
   */
  private static void pushObjectOntoStack(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Object object)
      throws NoSuchMethodException {
    code.append(factory.createNew(object.getClass().getName()));
    code.append(InstructionConst.DUP);
    if (!handleWrappers(constantPool, factory, code, object)) {
      handleOtherObjects(constantPool, factory, code, object);
    }
    code.append(InstructionConst.NOP);
  }

  /**
   * Push an instance of an arbitrary object onto the callstack.
   *
   * @param constantPool
   * @param factory
   * @param code
   * @param object       the object to create an instance of on the callstack.
   * @throws NoSuchMethodException (won't actually throw this as condition is checked and handled within the method.)
   */
  private static void handleOtherObjects(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Object object)
      throws NoSuchMethodException {
    // System.out.printf(">>>>>>>>>> %s %n", object.getClass());
    Constructor<?> constructor;
    if (SimpleMockUtils.hasNullConstructor(object.getClass())) {
      constructor = object.getClass().getConstructor();
    } else {
      constructor = object.getClass().getConstructors()[0];
    }
    Parameters parameters = new Parameters(constructor.getParameters());
    for (Type type : parameters.getTypes()) {
      Object paramValue = MockConsts.TYPE_2_DEFAULT_VALUE.get(type);
      pushValue(constantPool, factory, code, type, paramValue);
    }
    code.append(factory.createInvoke(object.getClass().getName(), "<init>", Type.VOID, parameters.getTypes(), Const.INVOKESPECIAL));
  }

  /**
   * Push an Object onto the callstack assuming the object is a primitive type wrapper.
   * @param constantPool
   * @param factory
   * @param code
   * @param object
   * @return true if the primitive type wrapper was pushed onto the callstack false if it failed (because the object isn't a primitive type wrapper)
   */
  private static boolean handleWrappers(ConstantPoolGen constantPool, InstructionFactory factory, InstructionList code, Object object) {
    // System.out.printf("Called: handleWrappers(constantPool, factory, code, %s)%n", object);
    try {
      Class<?> clazz = object.getClass();
      Class<?> primType = WRAPPER_3_PRIMITIVE.get(clazz.getName());
      Constructor<?> constructor = clazz.getConstructor(primType);
      Parameters parameters = new Parameters(constructor.getParameters());
      pushValue(constantPool, factory, code, Type.getType(primType), object);
      code.append(factory.createInvoke(clazz.getName(), "<init>", Type.VOID, parameters.getTypes(), Const.INVOKESPECIAL));
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  public static boolean isSameType(Class<?> mockReturnedClass, Class<?> expectedReturnedType) {
    // For Debugging: System.out.printf(">>> mockReturnedClass: %s, expectedReturnType: %s %n", mockReturnedClass, expectedReturnedType);
    if (expectedReturnedType.isPrimitive()) {
      Class<?> primitiveType = WRAPPER_3_PRIMITIVE.get(mockReturnedClass.getCanonicalName());
      if (expectedReturnedType.toString().equals(primitiveType.getName())) {
        return true;
      }
    }
    return mockReturnedClass.getTypeName().equals(expectedReturnedType.getTypeName());
  }

}
