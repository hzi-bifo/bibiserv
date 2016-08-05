package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

import de.unibi.cebitec.bibiserv.server.manager.ManagerException;
import java.sql.SQLException;
import java.util.Collection;
import org.primefaces.event.FileUploadEvent;

/**
 * Module Manager for Admin Module Hotplugging.
 * @author chenke
 */
public interface IModuleManager {

    /**
     * Is called when a module archive file is uploaded.
     * @param event event which contains the module archive file.
     */
    public void receiveModuleUpload(FileUploadEvent event);

    /**
     * Deploy module in running application.
     * @param id moduleid
     */
    public void deployModule(String id) throws SQLException, ManagerException;

    /**
     * Undeploy module in running application.
     * @param id moduleid
     */
    public void undeployModule(String id) throws ManagerException;

    /**
     * Undeploy and delete module in running application.
     * @param id moduleid
     */
    public void deleteModule(String id) throws SQLException, ManagerException;

    /**
     * 
     * @return List of existing modules (both active and inactive)
     */
    public Collection<Module> getModules();

    /**
     * Modify active state of a module.
     * @param id The id of the module to set the state of.
     * @param active The state to set as boolean. true for active, false for inactive.
     */
    public void modifyModuleActive(String id, boolean active) throws ManagerException, SQLException;
}
