/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

import static org.apache.bcel.Const.ACC_PUBLIC;
import static org.apache.bcel.Const.ACC_SUPER;

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
        String className = clazz.getName() + "Sub";

        ClassGen cg = new ClassGen(className, clazz.getName(), "<generated>", ACC_PUBLIC | ACC_SUPER, null);
        cg.addEmptyConstructor(ACC_PUBLIC);
        ConstantPoolGen cp = cg.getConstantPool();

        for (Method method : clazz.getMethods()) {
            if (!DO_NOT_MOCK_METHODS.contains(method.getName())) {
                cg.addMethod(generateMethod(
                    cp,
                    className,
                    method,
                    getMockMethod(method, methods)
                ));
            }
        }

        System.out.printf("########## JavaClass: %s %n", cg.getJavaClass());
        ByteClassLoader byteClassLoader = new ByteClassLoader(SimpleMocker.class.getClassLoader());
        byteClassLoader.loadDataInBytes(className, cg.getJavaClass().getBytes());

        Class loadedClass = byteClassLoader.loadClass(className);
        Constructor<T> constructor = loadedClass.getConstructor();
        return constructor.newInstance();
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

        InstructionList code = generateCode(cpg, argTypes, returnType, mockMethod);

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

    private static InstructionList generateCode(ConstantPoolGen constantPool, Type[] argTypes, Type returnType, MockMethod mockMethod) {
        InstructionList code = new InstructionList();
        if (returnType.equals(Type.BOOLEAN)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, Boolean.valueOf(mockMethod.getReturned().toString())));
            } else {
                code.append(new PUSH(constantPool, false));
            }
        }
        if (returnType.equals(Type.BYTE)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, Byte.valueOf(mockMethod.getReturned().toString())));
            } else {
                code.append(new PUSH(constantPool, 0));
            }
        }
        if (returnType.equals(Type.CHAR)) {
            System.out.printf(">>>>>>>>>> mockMethod: %s %n", mockMethod);
            if (mockMethod != null && mockMethod.getReturned() != null) {
                System.out.printf(">>>>>>>>>> %c %n", ((Character) mockMethod.getReturned()).charValue());
                code.append(new PUSH(constantPool, ((Character) mockMethod.getReturned()).charValue()));
            } else {
                code.append(new PUSH(constantPool, Character.MIN_VALUE));
            }
        }
        if (returnType.equals(Type.INT)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, Integer.valueOf(mockMethod.getReturned().toString())));
            } else {
                code.append(new PUSH(constantPool, 0));
            }
        }
        if (returnType.equals(Type.LONG)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, Long.valueOf(mockMethod.getReturned().toString())));
            } else {
                code.append(new PUSH(constantPool, 0L));
            }
        }
        if (returnType.equals(Type.FLOAT)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, Float.valueOf(mockMethod.getReturned().toString())));
            } else {
                code.append(new PUSH(constantPool, 0.0F));
            }
        }
        if (returnType.equals(Type.DOUBLE)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, Double.valueOf(mockMethod.getReturned().toString())));
            } else {
                code.append(new PUSH(constantPool, 0.0D));
            }
        }
        if (returnType.equals(Type.STRING)) {
            if (mockMethod != null && mockMethod.getReturned() != null) {
                code.append(new PUSH(constantPool, mockMethod.getReturned().toString()));
            } else {
                code.append(new PUSH(constantPool, "-"));
            }
        }
        if (returnType.equals(Type.OBJECT)) {
            throw new FailedToMockException("Sorry mocking Object return tips is not supported yet", new UnsupportedOperationException("Unsupported Type"));
            // @ToDo: code.append(new PUSH(constantPool, new ObjectType(returnType.toString())));
        }
        code.append(InstructionFactory.createReturn(returnType));
        return code;
    }

    private static MockMethod<?> getMockMethod(Method method, MockMethod<?>... mocks) {
        for (MockMethod<?> mock : mocks) {
            Class<?> mockReturnedClass = mock.getReturned().getClass();
            Class<?> expectedReturnedType = method.getReturnType();
            if (mock.getName().equals(method.getName())) {
                if (isSameType(mockReturnedClass, expectedReturnedType)) {
                    return mock;
                }
            }
        }
        return null;
    }

    private static boolean isSameType(Class<?> mockReturnedClass, Class<?> expectedReturnedType) {
        System.out.printf(">>> mockReturnedClass: %s, expectedReturnType: %s %n", mockReturnedClass, expectedReturnedType);
        if (expectedReturnedType.isPrimitive()) {
            if ("java.lang.Integer".equals(mockReturnedClass.getCanonicalName()) && "int".equals(expectedReturnedType.toString())) {
                return true;
            }
            if ("java.lang.Double".equals(mockReturnedClass.getCanonicalName()) && "double".equals(expectedReturnedType.toString())) {
                return true;
            }
            if ("java.lang.Character".equals(mockReturnedClass.getCanonicalName()) && "char".equals(expectedReturnedType.toString())) {
                return true;
            }
            if ("java.lang.Float".equals(mockReturnedClass.getCanonicalName()) && "float".equals(expectedReturnedType.toString())) {
                return true;
            }
            if ("java.lang.Byte".equals(mockReturnedClass.getCanonicalName()) && "byte".equals(expectedReturnedType.toString())) {
                return true;
            }
            if ("java.lang.Long".equals(mockReturnedClass.getCanonicalName()) && "long".equals(expectedReturnedType.toString())) {
                return true;
            }
            if ("java.lang.Boolean".equals(mockReturnedClass.getCanonicalName()) && "boolean".equals(expectedReturnedType.toString())) {
                return true;
            }
        }
        if (mockReturnedClass.getTypeName().equals(expectedReturnedType.getName())) {
            return true;
        }
        return false;
    }

    /**
     * Converts a primitive into its corresponding object wrapper reference.
     * This method assumes the primitve is already pushed to the top of the operand
     * stack. The stack is modified so that the primitive value is replaced
     * by a reference to its corresponding wrapper object that has been
     * initialized to contain the same value.
     *
     * @param   cp    constant pool
     * @param   type  type string of the primitive, for example {@link java.lang.Integer#TYPE Integer.TYPE.getName()}
     * @return  an instruction list that replaces the primitive type at the top of
     *          the operand stack with its corresponding, initialized, wrapper object
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
