package de.unibi.cebitec.bibiserv.server.manager.utilities;

public abstract class AdminModClassLoader extends ClassLoader{
    public AdminModClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String string) throws ClassNotFoundException {
        return super.findClass(string);
    }
}
