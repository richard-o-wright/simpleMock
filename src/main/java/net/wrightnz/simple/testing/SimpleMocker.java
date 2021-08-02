package net.wrightnz.simple.testing;

import java.lang.reflect.Constructor;
import static java.lang.reflect.Proxy.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?>[] interfaces = new Class[]{c};
            return (T) newProxyInstance(cl, interfaces, getInvocationHandler(methods));
        }
        try {
            Constructor<T> constructor = c.getConstructor();
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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

}
