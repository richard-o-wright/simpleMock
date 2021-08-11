/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author Richard Wright
 */
public class MockInvocationHandler implements InvocationHandler {

    private final MockMethod[] methods;

    public MockInvocationHandler(MockMethod... methods) {
        this.methods = methods;
    }

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

}
