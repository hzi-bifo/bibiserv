package de.unibi.cebitec.bibiserv.server.manager.utilities;

import java.io.File;

public abstract class AppClassLoader extends ClassLoader {

    public AppClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String string) throws ClassNotFoundException {
        return super.findClass(string);
    }

    public abstract void removeClassPath(File path);

    public abstract void removeJarPath(File path);
}
