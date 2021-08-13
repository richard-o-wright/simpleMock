/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

import static org.apache.bcel.Const.ACC_PUBLIC;
import static org.apache.bcel.Const.ACC_SUPER;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Richard Wright
 */
public class ClassMockGenerator {

  private static final List<String> DO_NOT_MOCK_METHODS = List.of("getClass", "wait", "notify", "notifyAll");

  public static <T> T createSubClass(final Class<T> clazz, final MockMethod<?>... methods)
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      ClassNotFoundException {

    String subclassName = clazz.getName() + "Sub";

    ClassGen cg = new ClassGen(subclassName, clazz.getName(), "<generated>", ACC_PUBLIC | ACC_SUPER, null);
    ConstantPoolGen cp = cg.getConstantPool();

    Parameter[] parameters = clazz.getConstructors()[0].getParameters();
    if (parameters.length == 0) {
      cg.addEmptyConstructor(ACC_PUBLIC);
    } else {
      cg.addMethod(generateNullConstructor(cp, subclassName, clazz.getName(), parameters));
    }

    for (Method method : clazz.getMethods()) {
      if (!DO_NOT_MOCK_METHODS.contains(method.getName())) {
        cg.addMethod(generateMethod(
            cp,
            subclassName,
            method,
            MockMethodUtils.getMockMethod(method, methods)
        ));
      }
    }

    // For Debugging:
    System.out.printf("########## JavaClass: %s %n", cg.getJavaClass());
    ByteClassLoader byteClassLoader = new ByteClassLoader(SimpleMocker.class.getClassLoader());
    byteClassLoader.loadDataInBytes(subclassName, cg.getJavaClass().getBytes());

    Class loadedClass = byteClassLoader.loadClass(subclassName);
    Constructor<T> constructor = loadedClass.getConstructor();
    return constructor.newInstance();
  }

  private static org.apache.bcel.classfile.Method generateNullConstructor(ConstantPoolGen cpg, String className, String superClass, Parameter[] parameters) {
    Type returnType = Type.VOID;
    InstructionList code = generateNullConstructorCode(cpg, superClass, parameters);
    MethodGen methodGen = new MethodGen(
        ACC_PUBLIC,
        returnType,
        new Type[0],
        new String[0],
        MockConsts.CONSTRUCTOR_METHOD_NAME,
        className,
        code,
        cpg
    );
    methodGen.setMaxStack();
    methodGen.setMaxLocals();
    return methodGen.getMethod();
  }

  private static org.apache.bcel.classfile.Method generateMethod(ConstantPoolGen cpg, String className, Method method, MockMethod<?> mockMethod) {
    int flags = ACC_PUBLIC;
    List<String> paramNames = new ArrayList<>();
    List<Type> paramTypes = new ArrayList<>();
    for (Parameter param : method.getParameters()) {
      paramNames.add(param.getName());
      paramTypes.add(Type.getType(param.getType()));
    }
    Type returnType = Type.getType(method.getReturnType());
    String methodName = method.getName();
    Type[] argTypes = paramTypes.toArray(new Type[0]);
    String[] argNames = paramNames.toArray(new String[0]);

    InstructionList code = generateCode(cpg, className, argTypes, returnType, mockMethod);

    MethodGen methodGen = new MethodGen(
        flags,
        returnType,
        argTypes,
        argNames,
        methodName,
        className,
        code,
        cpg
    );

    methodGen.setMaxStack();
    methodGen.setMaxLocals();
    return methodGen.getMethod();
  }

  private static InstructionList generateNullConstructorCode(ConstantPoolGen constantPool, String superClass, Parameter[] parameters) {
    List<Type> paramTypes = new ArrayList<>();
    for (Parameter param : parameters) {
      paramTypes.add(Type.getType(param.getType()));
    }
    Type[] argTypes = paramTypes.toArray(new Type[0]);

    InstructionList code = new InstructionList();
    InstructionFactory factory = new InstructionFactory(constantPool);
    code.append(InstructionFactory.createLoad(Type.OBJECT, 0));
    for (Type argType : argTypes) {
      Object value = MockConsts.TYPE_2_DEFAULT_VALUE.get(argType);
      if (argType.equals(Type.INT)) {
        code.append(new PUSH(constantPool, (int) value));
      } else if (argType.equals(Type.LONG)) {
        code.append(new PUSH(constantPool, (long) value));
      } else if (argType.equals(Type.FLOAT)) {
        code.append(new PUSH(constantPool, (float) value));
      } else if (argType.equals(Type.DOUBLE)) {
        code.append(new PUSH(constantPool, (double) value));
      } else if (argType.equals(Type.SHORT)) {
        code.append(new PUSH(constantPool, (short) value));
      } else if (argType.equals(Type.BOOLEAN)) {
        code.append(new PUSH(constantPool, (boolean) value));
      } else if (argType.equals(Type.STRING)) {
        code.append(new PUSH(constantPool, (String) value));
      }
    }
    code.append(factory.createInvoke(superClass, MockConsts.CONSTRUCTOR_METHOD_NAME, Type.VOID, argTypes, Const.INVOKESPECIAL));
    code.append(InstructionFactory.createReturn(Type.VOID));
    return code;
  }


  private static InstructionList generateCode(ConstantPoolGen constantPool, String className, Type[] argTypes, Type returnType, MockMethod mockMethod) {
    InstructionList code = new InstructionList();
    if (returnType.equals(Type.BOOLEAN)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Boolean.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, false));
      }
    } else if (returnType.equals(Type.BYTE)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Byte.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, 0));
      }
    } else if (returnType.equals(Type.CHAR)) {
      // For Debugging: System.out.printf(">>>>>>>>>> mockMethod: %s %n", mockMethod);
      if (mockMethod != null && mockMethod.getReturned() != null) {
        // For Debugging: System.out.printf(">>>>>>>>>> %c %n", ((Character) mockMethod.getReturned()).charValue());
        code.append(new PUSH(constantPool, ((Character) mockMethod.getReturned()).charValue()));
      } else {
        code.append(new PUSH(constantPool, Character.MIN_VALUE));
      }
    } else if (returnType.equals(Type.INT)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Integer.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, 0));
      }
    } else if (returnType.equals(Type.LONG)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Long.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, 0L));
      }
    } else if (returnType.equals(Type.FLOAT)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Float.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, 0.0F));
      }
    } else if (returnType.equals(Type.DOUBLE)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, Double.valueOf(mockMethod.getReturned().toString())));
      } else {
        code.append(new PUSH(constantPool, 0.0D));
      }
    } else if (returnType.equals(Type.STRING)) {
      if (mockMethod != null && mockMethod.getReturned() != null) {
        code.append(new PUSH(constantPool, mockMethod.getReturned().toString()));
      } else {
        code.append(new PUSH(constantPool, "-"));
      }
    } else if (returnType.equals(Type.OBJECT)) {
      if (mockMethod != null) {
        throw new FailedToMockException("Sorry mocking returned Objects is not supported yet",
                                        new UnsupportedOperationException("Unsupported Type"));
      }
      code.append(new NEW(constantPool.addClass(returnType.toString())));
      code.append(new ACONST_NULL());
    } else {
      if (mockMethod != null) {
        throw new FailedToMockException("Sorry mocking returned objects is not supported yet",
                                        new UnsupportedOperationException("Unsupported Type" + returnType.toString()));
      }
      code.append(new NEW(constantPool.addClass(returnType.toString())));
      code.append(new ACONST_NULL());
    }
    code.append(InstructionFactory.createReturn(returnType));
    return code;
  }





  /**
   * Converts a primitive into its corresponding object wrapper reference.
   * This method assumes the primitve is already pushed to the top of the operand
   * stack. The stack is modified so that the primitive value is replaced
   * by a reference to its corresponding wrapper object that has been
   * initialized to contain the same value.
   *
   * @param cp   constant pool
   * @param type type string of the primitive, for example {@link java.lang.Integer#TYPE Integer.TYPE.getName()}
   * @return an instruction list that replaces the primitive type at the top of
   *     the operand stack with its corresponding, initialized, wrapper object
   */
  protected static InstructionList convertPrimitiveToObject(ConstantPoolGen cp, String type) {
    InstructionList il = new InstructionList();

    if (type.equals(Boolean.TYPE.getName())) {
      int x = cp.addClass("java.lang.Boolean");
      int constrIndex = cp.addMethodref("java.lang.Boolean", "<init>", "(B)V");

      il.append(new ISTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(5));
      il.append(new ALOAD(5));
      il.append(new ILOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(5));
    } else if (type.equals(Short.TYPE.getName())) {
      int x = cp.addClass("java.lang.Short");
      int constrIndex = cp.addMethodref("java.lang.Short", "<init>", "(S)V");

      il.append(new ISTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(5));
      il.append(new ALOAD(5));
      il.append(new ILOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(5));
    } else if (type.equals(Long.TYPE.getName())) {
      int x = cp.addClass("java.lang.Long");
      int constrIndex = cp.addMethodref("java.lang.Long", "<init>", "(J)V");

      il.append(new LSTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(6));
      il.append(new ALOAD(6));
      il.append(new LLOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(6));
    } else if (type.equals(Integer.TYPE.getName())) {
      int x = cp.addClass("java.lang.Integer");
      int constrIndex = cp.addMethodref("java.lang.Integer", "<init>", "(I)V");

      il.append(new ISTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(5));
      il.append(new ALOAD(5));
      il.append(new ILOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(5));
    } else if (type.equals(Float.TYPE.getName())) {
      int x = cp.addClass("java.lang.Float");
      int constrIndex = cp.addMethodref("java.lang.Float", "<init>", "(F)V");

      il.append(new FSTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(5));
      il.append(new ALOAD(5));
      il.append(new FLOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(5));
    } else if (type.equals(Double.TYPE.getName())) {
      int x = cp.addClass("java.lang.Double");
      int constrIndex = cp.addMethodref("java.lang.Double", "<init>", "(D)V");

      il.append(new DSTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(6));
      il.append(new ALOAD(6));
      il.append(new DLOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(6));
    } else if (type.equals(Character.TYPE.getName())) {
      int x = cp.addClass("java.lang.Character");
      int constrIndex = cp.addMethodref("java.lang.Character", "<init>", "(C)V");

      il.append(new ISTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(5));
      il.append(new ALOAD(5));
      il.append(new ILOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(5));
    } else if (type.equals(Byte.TYPE.getName())) {
      int x = cp.addClass("java.lang.Byte");
      int constrIndex = cp.addMethodref("java.lang.Byte", "<init>", "(B)V");

      il.append(new ISTORE(4));
      il.append(new NEW(x));
      il.append(new ASTORE(5));
      il.append(new ALOAD(5));
      il.append(new ILOAD(4));
      il.append(new INVOKESPECIAL(constrIndex));
      il.append(new ALOAD(5));
    }
    return il;
  }

  /**
   * Converts a reference of a primitive wrapper object into a primitive value type.
   * This method assumes that the wrapper object reference is already loaded at the
   * top of the operand stack. The stack is modified so that the object reference
   * to a primitive wrapper is replaced by the corresponding value in the stack.
   *
   * @param cp   constant pool
   * @param type class name of the primitive wrapper object to convert
   * @return an instruction list that replaces an object reference of a primitive
   *     wrapper object to its corresponding value in the operand stack
   */
  protected static InstructionList convertObjectToPrimitive(ConstantPoolGen cp, String type) {
    InstructionList il = new InstructionList();

    int intValueIndex = cp.addMethodref(Integer.class.getName(), "intValue", "()I");
    int byteValueIndex = cp.addMethodref(Byte.class.getName(), "byteValue", "()B");
    int charValueIndex = cp.addMethodref(Character.class.getName(), "charValue", "()C");
    int doubleValueIndex = cp.addMethodref(Double.class.getName(), "doubleValue", "()D");
    int floatValueIndex = cp.addMethodref(Float.class.getName(), "floatValue", "()F");
    int longValueIndex = cp.addMethodref(Long.class.getName(), "longValue", "()J");
    int shortValueIndex = cp.addMethodref(Short.class.getName(), "shortValue", "()S");
    int booleanValueIndex = cp.addMethodref(Boolean.class.getName(), "booleanValue", "()Z");

    //
    // Assumes the wrapper object reference is on top of the stack
    //

    if (type.equals(Integer.TYPE.getName())) {
      int x = cp.addClass("java.lang.Integer");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Integer]
      il.append(new INVOKEVIRTUAL(intValueIndex));                            // Stack:  => ..., value [int]
    } else if (type.equals(Byte.TYPE.getName())) {
      int x = cp.addClass("java.lang.Byte");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Boolean]
      il.append(new INVOKEVIRTUAL(byteValueIndex));                           // Stack:  => ..., 0 | 1 [boolean]
    } else if (type.equals(Character.TYPE.getName())) {
      int x = cp.addClass("java.lang.Character");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Character]
      il.append(new INVOKEVIRTUAL(charValueIndex));                           // Stack:  => ..., value [char]
    } else if (type.equals(Double.TYPE.getName())) {
      int x = cp.addClass("java.lang.Double");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Double]
      il.append(new INVOKEVIRTUAL(doubleValueIndex));                         // Stack:  => ..., value [double]
    } else if (type.equals(Float.TYPE.getName())) {
      int x = cp.addClass("java.lang.Float");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Float]
      il.append(new INVOKEVIRTUAL(floatValueIndex));                          // Stack:  => ..., value [float]
    } else if (type.equals(Long.TYPE.getName())) {
      int x = cp.addClass("java.lang.Long");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Long]
      il.append(new INVOKEVIRTUAL(longValueIndex));                           // Stack:  => ..., value [long]
    } else if (type.equals(Short.TYPE.getName())) {
      int x = cp.addClass("java.lang.Short");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Short]
      il.append(new INVOKEVIRTUAL(shortValueIndex));                          // Stack:  => ..., value [short]
    } else if (type.equals(Boolean.TYPE.getName())) {
      int x = cp.addClass("java.lang.Boolean");
      il.append(new CHECKCAST(x));                                            // Stack:  => ..., type [Boolean]
      il.append(new INVOKEVIRTUAL(booleanValueIndex));                        // Stack:  => ..., value [boolean]
    }

    return il;
  }

}
