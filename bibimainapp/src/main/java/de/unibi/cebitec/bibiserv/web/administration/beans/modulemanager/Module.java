package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

public class Module {

    private String id;
    private boolean active;
    private ModuleInfo info;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setActive(int active) {
        this.active = active > 0;
    }

    public ModuleInfo getInfo() {
        return info;
    }

    public void setInfo(ModuleInfo info) {
        this.info = info;
    }
}
