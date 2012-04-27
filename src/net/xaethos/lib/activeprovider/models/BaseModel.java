package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BaseModel extends Proxy {

    private static class ModelInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            throw new UnsupportedOperationException();
        }
    }

    public static <T extends Object> T newModelInstance(Class<T> iface) {
        if (!iface.isAnnotationPresent(Model.class)) {
            throw new IllegalArgumentException("model interface must have the Model annotation");
        }

        return iface.cast(newProxyInstance(iface.getClassLoader(),
                new Class[]{iface},
                new ModelInvocationHandler()));
    }

    protected BaseModel(ModelInvocationHandler handler) {
        super(handler);
    }

    public Object get(String field) {
        return null;
    }
}
