/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

import static org.apache.bcel.Const.ACC_PUBLIC;
import static org.apache.bcel.Const.ACC_SUPER;

import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Richard Wright
 */
public class MockClassGenerator<T> {

  public T createSubClass(final Class<T> clazz, final MockMethod<?>... methods)
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      ClassNotFoundException {

    String subclassName = clazz.getName() + "Sub";
    ClassGen classGen = new ClassGen(subclassName, clazz.getName(), "<generated>", ACC_PUBLIC | ACC_SUPER, null);
    ConstantPoolGen cp = classGen.getConstantPool();
    if (SimpleMockUtils.hasNullConstructor(clazz)) {
      classGen.addEmptyConstructor(ACC_PUBLIC);
    } else {
      classGen.addMethod(MockConstructorFactory.makeNullConstructor(cp, subclassName, clazz));
    }

    for (Method method : clazz.getMethods()) {
      if (!MockConsts.DO_NOT_MOCK_METHODS.contains(method.getName())) {
        classGen.addMethod(MockMethodFactory.makeMockMethod(
            cp,
            subclassName,
            method,
            MockMethodFactory.findMockMethod(method, methods)
        ));
      }
    }
    // For Debugging: System.out.printf("########## JavaClass: %s %n", classGen.getJavaClass());
    return loadClass(classGen, subclassName);
  }

  private T loadClass(ClassGen classGen, String subclassName)
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    ByteClassLoader byteClassLoader = new ByteClassLoader(SimpleMocker.class.getClassLoader());
    byteClassLoader.loadDataInBytes(subclassName, classGen.getJavaClass().getBytes());

    Class loadedClass = byteClassLoader.loadClass(subclassName);
    Constructor<T> constructor = loadedClass.getConstructor();
    return constructor.newInstance();
  }

}
