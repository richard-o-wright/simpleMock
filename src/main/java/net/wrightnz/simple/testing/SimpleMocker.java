package net.wrightnz.simple.testing;

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("Convert2Lambda")
public final class SimpleMocker {

    public static <T>T mock(Class<T> c) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = new Class[] { c };

        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)  {
                return null;
            }
        };
        return (T) newProxyInstance(cl, interfaces, invocationHandler);
    }

    public static <T>T mock(final Class<T> c, final Map<String, Object> responses) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<?>[] interfaces = new Class[] { c };

        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (responses != null && responses.containsKey(method.getName())) {
                    return responses.get(method.getName());
                }
                return null;
            }
        };
        return (T) newProxyInstance(cl, interfaces, invocationHandler);
    }
}
