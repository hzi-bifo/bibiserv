package de.unibi.cebitec.bibiserv.server.manager.utilities;

public interface AdminRootClassLoader {

    public void addAdminModClassLoader(String name, AdminModClassLoader cl);

    public void removeAdminModClassLoader(String name);
}
