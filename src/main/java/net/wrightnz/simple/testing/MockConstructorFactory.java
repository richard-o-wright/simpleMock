package net.wrightnz.simple.testing;

import static org.apache.bcel.Const.ACC_PUBLIC;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import java.lang.reflect.Parameter;

public final class MockConstructorFactory {

  private MockConstructorFactory() {
  }

  public static org.apache.bcel.classfile.Method makeNullConstructor(ConstantPoolGen cpg, String className, Class<?> superClass) throws NoSuchMethodException {
    Parameter[] parameters = superClass.getConstructors()[0].getParameters();
    InstructionList code = generateNullConstructorCode(cpg, superClass.getName(), parameters);
    MethodGen methodGen = new MethodGen(
        ACC_PUBLIC,
        Type.VOID,
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

  private static InstructionList generateNullConstructorCode(ConstantPoolGen constantPool, String superClass, Parameter[] parameters)
      throws NoSuchMethodException {
    InstructionList code = new InstructionList();
    InstructionFactory factory = new InstructionFactory(constantPool);
    code.append(InstructionFactory.createLoad(Type.OBJECT, 0));
    final Parameters params = new Parameters(parameters);
    for (Type argType : params.getTypes()) {
      SimpleMockUtils.pushMockReturnValue(constantPool, factory, code, argType, null);
    }
    code.append(factory.createInvoke(superClass, MockConsts.CONSTRUCTOR_METHOD_NAME, Type.VOID, params.getTypes(), Const.INVOKESPECIAL));
    code.append(InstructionFactory.createReturn(Type.VOID));
    return code;
  }
}
