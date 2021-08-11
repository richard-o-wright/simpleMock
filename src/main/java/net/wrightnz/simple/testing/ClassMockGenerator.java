/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

import static org.apache.bcel.Const.ACC_PUBLIC;
import static org.apache.bcel.Const.ACC_SUPER;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
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

    private static final List<String> DO_NOT_MOCK_METHODS = List.of("getClass", "toString", "wait", "notify", "notifyAll"); //

    public static <T> T createSubClass(final Class<T> clazz, final MockMethod... methods)
        throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        ClassNotFoundException {
        System.out.printf("Called: createSubClass(%s, %d)\n", clazz.getName(), methods.length);
        String className = clazz.getName() + "Sub";

        ClassGen cg = new ClassGen(className, clazz.getName(), "<generated>", ACC_PUBLIC | ACC_SUPER, null);
        cg.addEmptyConstructor(ACC_PUBLIC);
        ConstantPoolGen cp = cg.getConstantPool();

        int emptyStringRef = cp.addString("-");

        for (Method method : clazz.getMethods()) {
            MockMethod<?> mockMethod = getMockMethod(method, methods);
            if (mockMethod == null) {
                if (!DO_NOT_MOCK_METHODS.contains(method.getName())) {
                    cg.addMethod(generateMethod(cg, cp, className, method, emptyStringRef));
                }
            } else {
                cg.addMethod(generateMethod(cg, cp, className, mockMethod));
            }
        }

        JavaClass stubClass = cg.getJavaClass();
        System.out.printf("Stub Class is: %s\n", stubClass);

        byte[] bytes = cg.getJavaClass().getBytes();

        ByteClassLoader byteClassLoader = new ByteClassLoader(SimpleMocker.class.getClassLoader());
        byteClassLoader.loadDataInBytes(className, bytes);

        Class loadedClass = byteClassLoader.loadClass(className);
        Constructor<T> constructor = loadedClass.getConstructor();
        return constructor.newInstance();
    }

    private static org.apache.bcel.classfile.Method generateMethod(ClassGen cg, ConstantPoolGen cpg, String className, MockMethod mockMethod) {
        int flags = ACC_PUBLIC;
        Type returnType = Type.getType(mockMethod.getReturned().getClass().getTypeName());
        System.out.printf("Return type %s\n", mockMethod.getReturned().getClass().getTypeName());
        String methodName = mockMethod.getName();
        Type[] argTypes = new Type[]{Type.INT};
        String[] argNames = new String[]{"i"};
        InstructionList code = generateCode(cg, cpg, argTypes, returnType, 0);
        MethodGen methodGen = new MethodGen(flags, returnType, argTypes, argNames, methodName, className, code, cpg);

        code.append(InstructionFactory.createReturn(returnType));
        methodGen.setMaxStack();
        methodGen.setMaxLocals();

        return methodGen.getMethod();
    }

    private static org.apache.bcel.classfile.Method generateMethod(ClassGen cg, ConstantPoolGen cpg, String className, Method method, int emptyStringRef) {
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

        InstructionList code = generateCode(cg, cpg, argTypes, returnType, emptyStringRef);

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

    private static InstructionList generateCode(ClassGen cg, ConstantPoolGen constantPool, Type[] argTypes, Type returnType, int emptyStringRef) {
        InstructionFactory instructionFactory = new InstructionFactory(cg, constantPool);
        InstructionList code = new InstructionList();
        if (returnType == Type.BOOLEAN) {
            code.append(new PUSH(constantPool, false));
        }
        if (returnType == Type.BYTE) {
            code.append(new PUSH(constantPool, 0));
        }
        if (returnType == Type.INT) {
            code.append(new PUSH(constantPool, 0));
        }
        if (returnType == Type.LONG) {
            code.append(new PUSH(constantPool, 0L));
        }
        if (returnType == Type.FLOAT) {
            code.append(new PUSH(constantPool, 0.0F));
        }
        if (returnType == Type.DOUBLE) {
            code.append(new PUSH(constantPool, 0.0D));
        }
        if (returnType == Type.STRING) {
            // PUTFIELD putfield = instructionFactory.createPutField("net.wrightnz.simple.testing.ExampleClassSub", "strVal", returnType);
            // code.append(putfield);
            code.append(new PUSH(constantPool, (String) null));
            // code.append(new ARETURN());
            // CompoundInstruction push = new PUSH(constantPool, emptyStringRef);
            // StoreInstruction store =  new org.apache.bcel.generic.ASTORE(emptyStringRef);
            // code.append(store);
            // ReturnInstruction ret = new org.apache.bcel.generic.ARETURN();
            // code.append(ret);
            // instructionFactory.createPutField(, "strVal", returnType);
        }
        code.append(InstructionFactory.createReturn(returnType));
        System.out.printf("Code is: %s\n", code.toString(true));
        return code;
    }

    /*
    private static int generateFieldRef(ConstantPoolGen constants, String className, String memberName) {
        // String sig = "()" + generateTypeSignature("Blah");
        String sig = generateTypeSignature("Blah");
        return constants.addFieldref(className, memberName, sig);
    }
    */
    private static MockMethod getMockMethod(Method method, MockMethod... mocks) {
        System.out.printf("Called: getMockMethod(%s)\n", method.getName());
        for (MockMethod mock : mocks) {
            System.out.printf(
                    "Return type check getMockMethod() %s %s\n",
                    mock.getReturned().getClass().getTypeName(),
                    method.getReturnType().getName()
            );
            if (mock.getName().equals(method.getName())
                    && mock.getReturned().getClass().getTypeName().equals(method.getReturnType().getName())) {

                System.out.printf("Called: getMockMethod(%s), returning %s\n", method.getName(), mock.getName());

                return mock;
            }
        }
        System.out.printf("Called: getMockMethod(%s), returning null\n", method.getName());
        return null;
    }

}
