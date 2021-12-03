package com.dutchtech.dutchgo;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GenericInstance<T> {
    public T create(Class<T> classToCreate) {
        T instance=null;
        try {
           Constructor<T> cs=classToCreate.getDeclaredConstructor(Context.class);
           cs.setAccessible(true);
           instance=cs.newInstance(MainActivity.getContext());
        } catch (IllegalAccessException e) {
            Log.d("error",e.toString());
        } catch (InstantiationException e) {
            Log.d("error",e.toString());
        } catch (NoSuchMethodException e) {
            Log.d("error",e.toString());
        } catch (InvocationTargetException e) {
            Log.d("error",e.toString());
        }
        return instance;
    }
}
