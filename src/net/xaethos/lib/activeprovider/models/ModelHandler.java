package net.xaethos.lib.activeprovider.models;

import android.content.ContentProviderOperation;
import android.net.Uri;
import net.xaethos.lib.activeprovider.annotations.Getter;
import net.xaethos.lib.activeprovider.annotations.ModelInfo;
import net.xaethos.lib.activeprovider.annotations.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class ModelHandler<T extends Model> implements
        ModelManager.Utils, InvocationHandler, ReadOnlyModelHandler
{

    ////////// Static methods //////////

    protected static boolean validateModelInterface(Class<? extends Model> modelType) {
        return modelType.isAnnotationPresent(ModelInfo.class);
    }

    protected static String getGetterName(Class<?> cls) {
        return "get" + (cls.isArray() ?
                cls.getComponentType().getSimpleName() + "Array" :
                cls.getSimpleName());
    }

    ////////// Instance fields //////////

    private final Class<T> mModelInterface;
    private T mModelProxy;

    ////////// Instance methods //////////

    public ModelHandler(Class<T> modelInterface) {
        if (!validateModelInterface(modelInterface)) {
            throw new IllegalArgumentException(
                    "model interface must have the annotation @" + ModelInfo.class.getSimpleName());
        }
        mModelInterface = modelInterface;
    }

    public Class<T> getModelInterface() {
        return mModelInterface;
    }

    public T getModelProxy() {
        if (mModelProxy == null) {
            mModelProxy = mModelInterface.cast(
                    Proxy.newProxyInstance(mModelInterface.getClassLoader(),
                    new Class[]{mModelInterface},
                    this));
        }
        return mModelProxy;
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
            ((ReadWriteModelHandler)this).setNull(field);
        }
    }

    ////////// Model implementation //////////

    @Override
    public Uri getUri() {
        Long id = getLong(Model._ID);

        if (id == null || id < 1) {
            return null;
        }

        return ModelManager.getContentUri(mModelInterface, id);
    }

    @Override
    public boolean isReadOnly() {
        return !ReadWriteModelHandler.class.isInstance(this);
    }

    @Override
    public ContentProviderOperation saveOperation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentProviderOperation deleteOperation() {
        throw new UnsupportedOperationException();
    }

    ////////// InvocationHandler //////////

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodClass = method.getDeclaringClass();

        if (method.isAnnotationPresent(Getter.class)) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 0) {
                throw new IllegalArgumentException("@Getter methods must take no parameters");
            }
            return invokeGetter(method.getAnnotation(Getter.class).value(), method.getReturnType());
        }
        else if (method.isAnnotationPresent(Setter.class)) {
            if (isReadOnly()) {
                throw new ReadOnlyModelException();
            }

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                throw new IllegalArgumentException("@Setter methods must take a single parameter");
            }
            invokeSetter(method.getAnnotation(Setter.class).value(), args[0], params[0]);
            return null;
        }
        else if (methodClass == Object.class || methodClass == ModelManager.Utils.class) {
            return method.invoke(this, args);
        }

        throw new UnsupportedOperationException("Unhandled method: " + method.getName());
    }

}
