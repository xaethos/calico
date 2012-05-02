package net.xaethos.lib.activeprovider.models;

import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.Model;
import net.xaethos.lib.activeprovider.annotations.Setter;

import java.lang.reflect.*;

public abstract class BaseModelHandler implements InvocationHandler {

    protected static <T> T newModelInstance(Class<T> modelType, BaseModelHandler handler) {
        if (!modelType.isAnnotationPresent(Model.class)) {
            throw new IllegalArgumentException("model interface must have the Model annotation");
        }

        return modelType.cast(Proxy.newProxyInstance(modelType.getClassLoader(),
                new Class[]{modelType},
                handler));
    }

    protected static String getGetterName(Class<?> cls) {
        return "get" + (cls.isArray() ?
                cls.getComponentType().getSimpleName() + "Array" :
                cls.getSimpleName());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(Getter.class)) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 0) {
                throw new IllegalArgumentException("@Getter methods must take no parameters");
            }
            return invokeGetter(method.getAnnotation(Getter.class).value(), method.getReturnType());
        }
        if (method.isAnnotationPresent(Setter.class)) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                throw new IllegalArgumentException("@Setter methods must take a single parameter");
            }
            invokeSetter(method.getAnnotation(Setter.class).value(), args[0], params[0]);
            return null;
        }

        throw new UnsupportedOperationException("Unhandled method: " + method.getName());
    }

    public Object invokeGetter(String field, Class<?> valueType) throws Throwable {
        try{
            return this.getClass().getMethod(getGetterName(valueType), String.class).invoke(this, field);
        }
        catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Unhandled field type: " + valueType.getName());
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    public void invokeSetter(String field, Object value, Class<?> valueType) throws Throwable {
        if (value != null) {
            try{
                this.getClass().getMethod("set", String.class, valueType).invoke(this, field, value);
            }
            catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("Unhandled field type: " + valueType.getName());
            }
            catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
        else {
            setNull(field);
        }
    }

    public abstract String  getString(String field);
    public abstract Boolean getBoolean(String field);
    public abstract Byte    getByte(String field);
    public abstract Short   getShort(String field);
    public abstract Integer getInteger(String field);
    public abstract Long    getLong(String field);
    public abstract Float   getFloat(String field);
    public abstract Double  getDouble(String field);
    public abstract byte[]  getbyteArray(String field);

    public abstract void set(String field, String value);
    public abstract void set(String field, Byte value);
    public abstract void set(String field, Short value);
    public abstract void set(String field, Integer value);
    public abstract void set(String field, Long value);
    public abstract void set(String field, Float value);
    public abstract void set(String field, Double value);
    public abstract void set(String field, Boolean value);
    public abstract void set(String field, byte[] value);

    public abstract void setNull(String field);

}
