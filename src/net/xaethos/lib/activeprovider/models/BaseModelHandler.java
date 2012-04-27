package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BaseModelHandler implements InvocationHandler {

    protected static <T> T newModelInstance(Class<T> ifaceCls, BaseModelHandler handler) {
        if (!ifaceCls.isAnnotationPresent(Model.class)) {
            throw new IllegalArgumentException("model interface must have the Model annotation");
        }

        return ifaceCls.cast(Proxy.newProxyInstance(ifaceCls.getClassLoader(),
                new Class[]{ifaceCls},
                handler));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(Getter.class)) {
            return get(method.getAnnotation(Getter.class).value());
        }
        if (method.isAnnotationPresent(Setter.class)) {
            set(method.getAnnotation(Setter.class).value(), args[0]);
            return null;
        }

        throw new UnsupportedOperationException();
    }

    public abstract Object get(String field);

    public abstract void set(String field, Object value);
}
