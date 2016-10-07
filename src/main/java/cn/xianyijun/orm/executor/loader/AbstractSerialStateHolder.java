/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xianyijun.orm.reflection.ObjectFactory;

/**
 * The type Abstract serial state holder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class AbstractSerialStateHolder implements Externalizable {

    private static final long serialVersionUID = 8940388717901644661L;
    private static final ThreadLocal<ObjectOutputStream> stream = new ThreadLocal<ObjectOutputStream>();
    private byte[] userBeanBytes = new byte[0];
    private Object userBean;
    private Map<String, ResultLoaderMap.LoadPair> unloadedProperties;
    private ObjectFactory objectFactory;
    private Class<?>[] constructorArgTypes;
    private Object[] constructorArgs;

    /**
     * Instantiates a new Abstract serial state holder.
     */
    public AbstractSerialStateHolder() {
    }

    /**
     * Instantiates a new Abstract serial state holder.
     *
     * @param userBean            the user bean
     * @param unloadedProperties  the unloaded properties
     * @param objectFactory       the object factory
     * @param constructorArgTypes the constructor arg types
     * @param constructorArgs     the constructor args
     */
    public AbstractSerialStateHolder(
            final Object userBean,
            final Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
            final ObjectFactory objectFactory,
            List<Class<?>> constructorArgTypes,
            List<Object> constructorArgs) {
        this.userBean = userBean;
        this.unloadedProperties = new HashMap<String, ResultLoaderMap.LoadPair>(unloadedProperties);
        this.objectFactory = objectFactory;
        this.constructorArgTypes = constructorArgTypes.toArray(new Class<?>[constructorArgTypes.size()]);
        this.constructorArgs = constructorArgs.toArray(new Object[constructorArgs.size()]);
    }

    /**
     * Write external.
     *
     * @param out the out
     * @throws IOException the io exception
     */
    @Override
    public final void writeExternal(final ObjectOutput out) throws IOException {
        boolean firstRound = false;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = stream.get();
        if (os == null) {
            os = new ObjectOutputStream(baos);
            firstRound = true;
            stream.set(os);
        }

        os.writeObject(this.userBean);
        os.writeObject(this.unloadedProperties);
        os.writeObject(this.objectFactory);
        os.writeObject(this.constructorArgTypes);
        os.writeObject(this.constructorArgs);

        final byte[] bytes = baos.toByteArray();
        out.writeObject(bytes);

        if (firstRound) {
            stream.remove();
        }
    }

    /**
     * Read external.
     *
     * @param in the in
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    @Override
    public final void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final Object data = in.readObject();
        if (data.getClass().isArray()) {
            this.userBeanBytes = (byte[]) data;
        } else {
            this.userBean = data;
        }
    }

    /**
     * Read resolve object.
     *
     * @return the object
     * @throws ObjectStreamException the object stream exception
     */
    @SuppressWarnings("unchecked")
    protected final Object readResolve() throws ObjectStreamException {
    /* Second run */
        if (this.userBean != null && this.userBeanBytes.length == 0) {
            return this.userBean;
        }

    /* First run */
        try {
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(this.userBeanBytes));
            this.userBean = in.readObject();
            this.unloadedProperties = (Map<String, ResultLoaderMap.LoadPair>) in.readObject();
            this.objectFactory = (ObjectFactory) in.readObject();
            this.constructorArgTypes = (Class<?>[]) in.readObject();
            this.constructorArgs = (Object[]) in.readObject();
        } catch (final IOException ex) {
            throw (ObjectStreamException) new StreamCorruptedException().initCause(ex);
        } catch (final ClassNotFoundException ex) {
            throw (ObjectStreamException) new InvalidClassException(ex.getLocalizedMessage()).initCause(ex);
        }

        final Map<String, ResultLoaderMap.LoadPair> arrayProps = new HashMap<String, ResultLoaderMap.LoadPair>(this.unloadedProperties);
        final List<Class<?>> arrayTypes = Arrays.asList(this.constructorArgTypes);
        final List<Object> arrayValues = Arrays.asList(this.constructorArgs);

        return this.createDeserializationProxy(userBean, arrayProps, objectFactory, arrayTypes, arrayValues);
    }

    /**
     * Create deserialization proxy object.
     *
     * @param target              the target
     * @param unloadedProperties  the unloaded properties
     * @param objectFactory       the object factory
     * @param constructorArgTypes the constructor arg types
     * @param constructorArgs     the constructor args
     * @return the object
     */
    protected abstract Object createDeserializationProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
                                                         List<Class<?>> constructorArgTypes, List<Object> constructorArgs);
}
