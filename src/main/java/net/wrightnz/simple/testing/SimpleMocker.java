package net.wrightnz.simple.testing;

import java.lang.reflect.Constructor;
import static java.lang.reflect.Proxy.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import static org.apache.bcel.Const.*;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

@SuppressWarnings("Convert2Lambda")
public final class SimpleMocker {

    public static <T> T mock(final Class<T> c) throws FailedToMockException {
        return mock(c, (MockMethod[]) null);
    }

    public static <T> T mock(final Class<T> c, final Map<String, Object> responses) {
        MockMethod[] methods = new MockMethod[responses.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : responses.entrySet()) {
            MockMethod method = new MockMethod(entry.getKey(), entry.getValue());
            methods[i] = method;
            i++;
        }

        return mock(c, methods);
    }

    public static <T> T mock(final Class<T> c, final MockMethod... methods) throws FailedToMockException {
        if (c.isInterface()) {
            // System.out.printf("##### Is Interface ######\n");
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?>[] interfaces = new Class[]{c};
            return (T) newProxyInstance(cl, interfaces, getInvocationHandler(methods));
        }
        try {
            return createSubClass(c, methods);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
            throw new FailedToMockException("Failed to create mock instance of: " + c.getName(), e);
        } catch (NoSuchMethodException e) {
            throw new FailedToMockException(
                    "Failed to create mock instance of: "
                    + c.getName()
                    + ". Most likely because that class does not have a null"
                    + " constructor (it's currently only possible to mock null"
                    + " contructor classes with SimpleMock)",
                    e
            );
        }
    }

    private static InvocationHandler getInvocationHandler(final MockMethod... methods) {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (methods != null) {
                    for (MockMethod meth : methods) {
                        if (meth.getName().equals(method.getName())) {
                            meth.incrementInvocationCount();
                            return meth.getReturned();
                        }
                    }
                }
                return null;
            }
        };
    }

    private static <T> T createSubClass(final Class<T> clazz, final MockMethod... methods) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        System.out.printf("Called: createSubClass()\n");
        String className = clazz.getName() + "Sub";

        ClassGen cg = new ClassGen(className, clazz.getName(), "<generated>", ACC_PUBLIC | ACC_SUPER, null);
        cg.addEmptyConstructor(ACC_PUBLIC);
        ConstantPoolGen cp = cg.getConstantPool();

        InstructionList il = new InstructionList();
        InstructionFactory fac = new InstructionFactory(cg, cp);
        // MethodGen mg = new MethodGen(
        //        ACC_PUBLIC, // access flags , ACC_STATIC |
        //        Type.VOID, // return type
           //     new Type[]{new ArrayType(Type.STRING, 1)}, // argument types
          //      new String[]{"argv"}, // arg names
         //       "test", clazz.getName(), // method, class
        //        il, cp);
        /*MethodGen mg = new MethodGen(
                ACC_PUBLIC,
                Type.INT,
                new Type[]{new ArrayType(Type.INT, 1)},
                new String[]{"i"},
                "zero", clazz.getName(),
                il, cp);*/
/*
        InstructionList getter = new InstructionList();
        MethodGen getterMethod = new MethodGen(
                ACC_PUBLIC,
                Type.INT,
                new Type[]{Type.INT},
                new String[]{"i"},
                "zero", clazz.getName(),
                il, cp);
        getterMethod.setMaxStack(1);

        InstructionHandle getter_ih_0 = getter.append(InstructionFactory.createLoad(Type.OBJECT, 0));
        InstructionHandle getter_ih_1 = getter.append(
                fac.createGetField(cg.getClassName(),
                "zero",
                Type.INT)
        );
        InstructionHandle getter_ih_4 = getter.append(InstructionFactory.createReturn(Type.INT));
        */

        cg.addMethod(generateMethod(cp, className));

        // mg.setMaxStack();
        // cg.addMethod(getterMethod.getMethod());
        // getter.dispose();

        System.out.printf("Stub Class Name is: %s\n", cg.getClassName());

        JavaClass stubClass = cg.getJavaClass();
        System.out.printf("Stub Class is: %s\n", stubClass);

        byte[] bytes = cg.getJavaClass().getBytes();


        ByteClassLoader byteClassLoader = new ByteClassLoader(SimpleMocker.class.getClassLoader());
        byteClassLoader.loadDataInBytes(className, bytes);

        Class loadedClass = byteClassLoader.loadClass(className);

        Constructor<T> constructor = loadedClass.getConstructor();
        return constructor.newInstance();
    }

    public static class ByteClassLoader extends ClassLoader {

        private HashMap<String, byte[]> byteDataMap = new HashMap<>();

        public ByteClassLoader(ClassLoader parent) {
            super(parent);
        }

        public void loadDataInBytes(String resourcesName, byte[] byteData) {
            byteDataMap.put(resourcesName, byteData);
        }

        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {
            if (byteDataMap.isEmpty()) {
                throw new ClassNotFoundException("byte data is empty");
            }
            // String filePath = className.replaceAll("\\.", "/").concat(".class");
            byte[] extractedBytes = byteDataMap.get(className);
            if (extractedBytes == null || extractedBytes.length == 0) {
                throw new ClassNotFoundException("Cannot find " + className + " in bytes");
            }
            return defineClass(className, extractedBytes, 0, extractedBytes.length);
        }
    }

    private static org.apache.bcel.classfile.Method generateMethod(ConstantPoolGen constants, String className) {
        int flags = ACC_PUBLIC;
        Type returnType = Type.INT;
        String methodName = "zero";
        Type[] argTypes = new Type[]{Type.INT};
        String[] argNames = new String[]{"i"};
        InstructionList code = generateCode(constants, argTypes, returnType, className, "foo");
        MethodGen methodGen = new MethodGen(flags, returnType, argTypes, argNames, methodName, className, code, constants);
        return methodGen.getMethod();
    }

    private static InstructionList generateCode(ConstantPoolGen constants, Type[] argTypes, Type returnType, String className, String memberName) {
        InstructionList code = new InstructionList();
        Instruction ins = generateInstruction(constants, className, memberName); // TODO: Allocate some locals
        if (ins instanceof BranchInstruction) {
            // Call the overloaded append()
            code.append((BranchInstruction) ins);
        } else {
            code.append(ins);
        }
        return code;
    }

    private static Instruction generateInstruction(ConstantPoolGen constants, String className, String memberName) {
        return new GETFIELD(generateFieldRef(constants, className, memberName));
    }

    static int generateFieldRef(ConstantPoolGen constants, String className, String memberName) {
        // String sig = "()" + generateTypeSignature("Blah");
        String sig = "I";
        return constants.addFieldref(className, memberName, sig);
    }

    private static String[] primitiveTypes = {
            "Z", "B", "C", "S", "I", "J", "F", "D"
    };


    private static String generateTypeSignature(String returnedClassName) {
        String typeSig;
        if (true) {
            // Primitive
            typeSig = "I";
        } else {
            // Class type
            typeSig = "L" + returnedClassName + ";";
        }
        /*if (false) {
            // Generate array depth with geometric distribution
            int depth = geom.sampleWithMean(MEAN_ARRAY_DEPTH, r);
            for (int i = 0; i < depth; i++) {
                typeSig = "[" + typeSig;
            }
        }*/
        return typeSig;
    }

}
