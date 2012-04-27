package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BaseModelProxy implements InvocationHandler {

    protected static <T> T newModelInstance(Class<T> ifaceCls, BaseModelProxy handler) {
        if (!ifaceCls.isAnnotationPresent(Model.class)) {
            throw new IllegalArgumentException("model interface must have the Model annotation");
        }

        return ifaceCls.cast(Proxy.newProxyInstance(ifaceCls.getClassLoader(),
                new Class[]{ifaceCls},
                handler));
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        if (method.isAnnotationPresent(Getter.class)) {
            return get(method.getAnnotation(Getter.class).value());
        }

        throw new UnsupportedOperationException();
    }

    abstract public Object get(String field);

}
