/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
 * All rights reserved.
 * 
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You 
 * may not use this file except in compliance with the License. You can 
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 * 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  When distributing the software, include 
 * this License Header Notice in each file.  If applicable, add the following 
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 * 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 *	Christian Henke <chenke@cebitec.uni-bielefeld.de>
 * 
 */
package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

import de.unibi.cebitec.bibiserv.server.manager.Core;
import de.unibi.cebitec.bibiserv.server.manager.ManagerException;
import de.unibi.cebitec.bibiserv.web.administration.beans.AdminFilterInvocationSecurityMetadataSource;
import de.unibi.cebitec.bibiserv.web.administration.beans.Authority;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class ModuleManager implements IModuleManager, InitializingBean {

    /**
     * Module Descriptor File path.
     */
    public static final String INFO_FILENAME = "web/WEB-INF/info.xml";
    public static final String INFO_SCHEMA = "http://bibiserv.cebitec.uni-bielefeld.de/xsd/bibiserv2/admin-module-info.xsd";
    private final static Logger LOG = Logger.getLogger(ModuleManager.class);
    /**
     * Existing Modules (active and inactive).
     */
    private Map<String, Module> modulesMap;
    /**
     * Filter String for search in modules overview.
     */
    private String activeModulesFilterString;
    private ModuleDatabase moduleDatabase;
    private AdminFilterInvocationSecurityMetadataSource adminFilterInvocationSecurityMetadataSource;

    public ModuleManager() throws Exception {
        this.activeModulesFilterString = "";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //initialize map of modules
        update();
    }

    private void update() {
        this.modulesMap = this.moduleDatabase.getAll();
        this.adminFilterInvocationSecurityMetadataSource.recreateFilters();
    }

    /**
     * Handle module upload done through the ModuleManagers web interface.
     *
     * @param event the FileUploadEvent that contains the module archive
     */
    @Override
    public void receiveModuleUpload(FileUploadEvent event) {
        
        UploadedFile file = event.getFile();
        facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO, "Upload:", "Ok."));
        /**
         * take binary archive data and save it to db:
         */
        
        
        String id =  null;
        
        try {
            id = this.moduleDatabase.saveModuleToDb(IOUtils.toByteArray(file.getInputstream()));
        } catch (IOException e){
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Upload:","Inputstream is empty!"));
        }
        /**
         * if saving to db has been successful, deploy module automatically:
         */
        if (id != null) {
            try {
                deployModule(id);
                
            } catch (ManagerException | SQLException exc) {
                LOG.fatal(exc);
            }
        }
        update();
    }

    /**
     * Toggles the activity state of a module.
     *
     * @param id module id
     * @param active true if the module should be active otherwise false
     * @throws ManagerException
     * @throws SQLException
     */
    @Override
    public void modifyModuleActive(String id, boolean active) throws ManagerException, SQLException {
        if (!modulesMap.containsKey(id)) {
            LOG.error("invalid module id '" + id + "' during module activation");
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Enable module:", "'" + id + "' could not be enabled!"));
        } else {
            if (active) {
                try {
                    deployModule(id);
                } catch (ManagerException e) {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL,"Enable module:","'"+id+"' (deploy) failed! (see server.log for stack trace!)"));
                }
            } else {
                try {
                    undeployModule(id);
                } catch (ManagerException e) {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL,"Disable module:","'"+id+"' (undeploy) failed! (see server.log for stack trace!)"));
                }
            }
            boolean dbSuccess = this.moduleDatabase.setModuleActiveInDb(id, active);
            if (dbSuccess) {
                if (active) {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Enable module:", "'" + id + "' successfully enabled!"));
                } else {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Disable module:", "'" + id + "' successfully disabled!"));
                }
            } else {
                if (active) {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Enable module:", "'" + id + "' could not be enabled!"));
                } else {
                    facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Disable module:", "'" + id + "' could not be disabled!"));
                }
            }
        }
        update();
    }

    @Override
    public Collection<Module> getModules() {
        return new ArrayList<>(this.modulesMap.values());
    }

    @Override
    public void deployModule(String id) throws ManagerException, SQLException {
        Core.getInstance().deployModule(id);
        update();
    }

    @Override
    public void undeployModule(String id) throws ManagerException {
        Core.getInstance().undeployModule(id);
        update();
    }

    /**
     * List of active admin modules.
     *
     * @return List of active modules.
     */
    public List<Module> getActiveModules() {
        List<Module> modules = new ArrayList<>();
        for (Map.Entry<String, Module> mapEntry : modulesMap.entrySet()) {
            if (mapEntry.getValue().isActive()) {
                modules.add(mapEntry.getValue());
            }
        }
        return modules;
    }

    /**
     * List of active admin modules that match the current filter string AND
     * that are accessible with the role privileges the user has.
     *
     * @return List of modules that match the filter and are accessible to the
     * users role(s).
     */
    public List<Module> getAllowedAndActiveModulesFiltered() {

        List<Module> modules = new ArrayList<>();
        for (Map.Entry<String, Module> mapEntry : modulesMap.entrySet()) {
            if (accessAllowed(mapEntry.getValue())) {
                if (mapEntry.getValue().isActive()) {
                    if (mapEntry.getValue().getInfo().getName().toLowerCase().contains(this.activeModulesFilterString.toLowerCase())) {
                        modules.add(mapEntry.getValue());
                    }
                }
            }
        }
        return modules;
    }

    /**
     * Delete a specific module completely. Involves undeploying it and removing
     * it from the database.
     *
     * @param id
     * @throws ManagerException
     * @throws SQLException
     */
    @Override
    public void deleteModule(String id) throws ManagerException, SQLException {
        undeployModule(id);
        if (this.moduleDatabase.removeModuleFromDb(id)) {
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Remove module: ", "Module '" + id + "' successfully removed."));
        } else {
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Remove module: ", "Module '" + id + "' could not be removed!"));
        }
        update();
    }

    /**
     * Determine if currently logged in user has access to the specified module.
     * Bear in mind that this method should only be used for COSMETIC purposes
     * (i.e. hide elements from the user he can't access anyway). That means the
     * real access granting and denying is done by the
     *
     * @see
     * de.unibi.cebitec.bibiserv.web.administration.beans.AdminFilterInvocationSecurityMetadataSource
     *
     * @param module The module to check.
     * @return true if the user has access, otherwise false
     */
    public boolean accessAllowed(Module module) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for (GrantedAuthority granted : user.getAuthorities()) {
                boolean isValidAuthorityString = false;
                for (Authority existingAuthority : Authority.values()) {
                    if (granted.getAuthority().equalsIgnoreCase(existingAuthority.name())) {
                        isValidAuthorityString = true;
                    }
                }
                if (isValidAuthorityString) {
                    Authority currentAuthorityEnum = Authority.valueOf(granted.getAuthority());
                    if (module.getInfo().getRoleRestrictions().contains(currentAuthorityEnum)) {
                        return true;
                    }
                }
            }
            return false;
            /*
             * The following exceptions should never occur but are there because
             * this is security.
             */
        } catch (ClassCastException ce) {
            LOG.error("Attempt to create UserDetails object failed.");
            return false;
        } catch (NullPointerException npe) {
            LOG.error("Attempt to retrieve user roles from UserDetails object failed.");
            return false;
        } catch (Exception e) {
            LOG.fatal("Module access check failed!");
            return false;
        }
    }

    public void saveInfoChanges(ModuleInfo info) {
        LOG.debug("saving Info changes for: "+info.getId());
        this.moduleDatabase.writeModuleInfoToDb(info);
        update();
    }
    
    public List<Authority> getAuthorities(){
        return Arrays.asList(Authority.values());
    }

    /**
     * Simple utility function to display FacesMessages.
     *
     * @param msg FacesMessage to display.
     */
    public static void facesMsg(FacesMessage msg) {
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getActiveModulesFilterString() {
        return activeModulesFilterString;
    }

    public void setActiveModulesFilterString(String activeModulesFilterString) {
        this.activeModulesFilterString = activeModulesFilterString;
    }

    public ModuleDatabase getModuleDatabase() {
        return moduleDatabase;
    }

    public void setModuleDatabase(ModuleDatabase moduleDatabase) {
        this.moduleDatabase = moduleDatabase;
    }

    public AdminFilterInvocationSecurityMetadataSource getAdminFilterInvocationSecurityMetadataSource() {
        return adminFilterInvocationSecurityMetadataSource;
    }

    public void setAdminFilterInvocationSecurityMetadataSource(AdminFilterInvocationSecurityMetadataSource source) {
        this.adminFilterInvocationSecurityMetadataSource = source;
    }
}
