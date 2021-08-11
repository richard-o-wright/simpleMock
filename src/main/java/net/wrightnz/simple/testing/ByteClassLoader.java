/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

import java.util.HashMap;

/**
 *
 * @author Richard Wright
 */
public class ByteClassLoader extends ClassLoader {

    private final HashMap<String, byte[]> byteDataMap = new HashMap<>();

    public ByteClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void loadDataInBytes(String resourcesName, byte[] byteData) {
        byteDataMap.put(resourcesName, byteData);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (byteDataMap.isEmpty()) {
            throw new ClassNotFoundException("byte data is empty");
        }
        byte[] extractedBytes = byteDataMap.get(className);
        if (extractedBytes == null || extractedBytes.length == 0) {
            throw new ClassNotFoundException("Cannot find " + className + " in bytes");
        }
        return defineClass(className, extractedBytes, 0, extractedBytes.length);
    }
}
