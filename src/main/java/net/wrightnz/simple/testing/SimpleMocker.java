package net.wrightnz.simple.testing;

import static java.lang.reflect.Proxy.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class SimpleMocker {

    private SimpleMocker(){
    }

    public static <T> T mock(final Class<T> c) throws FailedToMockException {
        return mock(c, new MockMethod<?>[0]);
    }

    public static <T> T mock(final Class<T> c, final Map<String, Object> responses) {
        MockMethod<?>[] methods = new MockMethod[responses.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : responses.entrySet()) {
            MockMethod<Object> method = new MockMethod<>(entry.getKey(), entry.getValue());
            methods[i] = method;
            i++;
        }

        return mock(c, methods);
    }

    public static <T> T mock(final Class<T> c, MockMethod<?>... methods) throws FailedToMockException {
        if (methods == null) {
            throw new FailedToMockException("MockMethod cannot be null", null);
        }

        if (c.isInterface()) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?>[] interfaces = new Class[]{c};
            InvocationHandler invocationHandler = new MockInvocationHandler(methods);
            return (T) newProxyInstance(cl, interfaces, invocationHandler);
        }
        try {
            return ClassMockGenerator.createSubClass(c, methods);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new FailedToMockException("Failed to create mock instance of: " + c.getName(), e);
        }
    }

}
