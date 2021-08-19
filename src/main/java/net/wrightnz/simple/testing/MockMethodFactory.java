package net.wrightnz.simple.testing;

import static org.apache.bcel.Const.ACC_PUBLIC;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import java.lang.reflect.Method;

public final class MockMethodFactory {

  private MockMethodFactory() {
  }

  public static org.apache.bcel.classfile.Method makeMockMethod(ConstantPoolGen cpg, String className, Method method, MockMethod<?> mockMethod)
      throws NoSuchMethodException {
    Parameters parameters = new Parameters(method.getParameters());
    Type returnType = Type.getType(method.getReturnType());
    InstructionList code = generateCode(cpg, returnType, mockMethod);
    MethodGen methodGen = new MethodGen(
        ACC_PUBLIC,
        returnType,
        parameters.getTypes(),
        parameters.getNames(),
        method.getName(),
        className,
        code,
        cpg
    );

    methodGen.setMaxStack();
    methodGen.setMaxLocals();
    return methodGen.getMethod();
  }

  /**
   * Look for a MockMethod version of the real method "method" in the array
   * of MockMethods.
   * @param method real Method defining the method signature to look for.
   * @param mocks array of MockMethods to search in.
   * @return the equivalent MockMethod to method if one os found otherwise null.
   */
  public static MockMethod<?> findMockMethod(Method method, MockMethod<?>... mocks) {
    for (MockMethod<?> mock : mocks) {
      if (SimpleMockUtils.isSameMethod(method, mock)) {
        return mock;
      }
    }
    return null;
  }

  private static InstructionList generateCode(ConstantPoolGen constantPool, Type returnType, MockMethod mockMethod) throws NoSuchMethodException {
    InstructionFactory factory = new InstructionFactory(constantPool);
    InstructionList code = new InstructionList();
    SimpleMockUtils.pushMockReturnValue(constantPool, factory, code, returnType, mockMethod);
    code.append(InstructionFactory.createReturn(returnType));
    return code;
  }

}
