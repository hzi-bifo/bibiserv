package de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager;

import de.unibi.cebitec.bibiserv.server.manager.utilities.ZIPTool;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.faces.application.FacesMessage;
import static de.unibi.cebitec.bibiserv.web.administration.beans.modulemanager.ModuleManager.*;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.sql.*;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;

/**
 * Shared database API to manage or query admin modules.
 */
public class ModuleDatabase {

    private final static Logger LOG = Logger.getLogger(ModuleDatabase.class);
 

    public ModuleDatabase() throws Exception {
       
    }

    /**
     * Persist a module archive to the database. The module id is determined
     * from the info file inside.
     *
     * @param archive
     * @return module id on success, null on failure.
     */
    protected String saveModuleToDb(byte[] archive) {
        String id = null;
        String version;
        Connection conn = null;
        try {          
            conn = BiBiTools.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO MODULES (ID,INFO,FILE) VALUES (?, ?, ?)");
            byte[] infoData = ZIPTool.extractNamedEntryfromZippedBuffer(
                    archive, INFO_FILENAME);
            SchemaValidation validation = new SchemaValidation();
            boolean valid = validation.validate(new URL(INFO_SCHEMA), infoData);
            if (valid) {
                facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO, "Validation:", "Ok."));
            } else {
                facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation:",
                        "Check of 'info.xml' against XML-Schema '"
                        + INFO_SCHEMA + "' failed. Reason: "
                        + validation.getMessage()));
                return null;
            }
            ModuleInfo info = unmarshalModuleInfo(new ByteArrayInputStream(infoData));
            id = info.getId();
            version = info.getVersion();
            if (id == null) {
                LOG.error("Module installation failed. Invalid/missing id or malformed "
                        + INFO_FILENAME);
                facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Installation:",
                        "Failed. Invalid/missing id or malformed " + INFO_FILENAME));
                return null;
            } else if (id.equals("moduleManager")) {
                LOG.error("Module installation failed. 'moduleManager' as module name is not allowed!");
                facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Installation:",
                        "Failed. 'moduleManager' as module name is not allowed!"));
                return null;
            }
            stmt.setString(1, id);
            stmt.setClob(2, new StringReader(new String(infoData)));
            stmt.setBlob(3, new ByteArrayInputStream(archive));
            /**
             * Delete previously installed module with same name from DB if it
             * already exists.
             */
            if (removeModuleFromDb(id)) {
                facesMsg(new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Replacing:", "Installed version of '" + id
                        + "' already exists and will be replaced with uploaded version "
                        + version + "."));
            }
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            String eStack = result.toString();
            LOG.error("Module installation failed. - " + eStack);
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Installation:", "Failed. - " + eStack));
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                LOG.fatal("Exception while closing statement/database connection:" + e);
            }
        }

        facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Installation:", "Ok."));
        facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Success!", "Module '" + id + "' has been installed."));
        facesMsg(new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Please enable the module to use it.", ""));
        return id;
    }

    /**
     * Delete Module with given id from the database.
     *
     * @param id
     * @return
     */
    protected boolean removeModuleFromDb(String id) {
        Connection conn = null;
        try {
            conn = BiBiTools.getDataSource().getConnection();
            PreparedStatement pst = conn.prepareStatement("DELETE FROM MODULES WHERE ID=?");
            pst.setString(1, id);
            int changed = pst.executeUpdate();
            pst.close();
            return (changed > 0);
        } catch (DBConnectionException | SQLException e) {
            LOG.fatal("Error while deleting module '" + id + "' from db: " + e);
            facesMsg(new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Error while deleting older version if this module:", e.toString()));
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                LOG.fatal("Exception while closing database connection:" + ex);
            }
        }
    }

    /**
     * Set active state of a module in the database.
     *
     * @param id The id of the module to set the state of.
     * @param active The state to set as boolean. true for active, false for
     * inactive.
     * @return true if successful, false otherwise.
     */
    protected boolean setModuleActiveInDb(String id, boolean active) {
        Connection conn = null;
        try {
            conn = BiBiTools.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE MODULES SET ACTIVE=? WHERE ID=?");
            stmt.setInt(1, active ? 1 : 0);
            stmt.setString(2, id);
            stmt.execute();
            stmt.close();
            
        } catch (Exception e) {
            LOG.error("module state of '" + id + "' could not be written to database",e);
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                LOG.fatal("Exception while closing statement/database connection:" + e,e);
            }
        }
        return true;
    }

    /**
     * Save current Module Descriptor object to db. (as the new info.xml)
     *
     * @param id moduleid
     * @return ModuleInfo
     */
    public ModuleInfo writeModuleInfoToDb(ModuleInfo info) {
        
        Connection conn = null;
        try {
            conn = BiBiTools.getDataSource().getConnection();
           
            PreparedStatement pst = conn.prepareStatement("UPDATE MODULES SET INFO=? WHERE ID=?");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshalModuleInfo(info, out);
            pst.setAsciiStream(1, new ByteArrayInputStream(out.toByteArray()));
            pst.setString(2, info.getId());
            
            pst.close();
        } catch (Exception ex) {
            LOG.fatal("The following exception occured while "
                    + "updating Info-Document of Module '" + info.getId() + "':" + ex,ex);
        } finally {
            try {
                
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                LOG.fatal("Exception while closing database connection:" + ex,ex);
            }
        }
        return null;
    }

    private ModuleInfo unmarshalModuleInfo(InputStream is) {
        try {
            JAXBContext c = JAXBContext.newInstance(ModuleInfo.class);
            ModuleInfo i = (ModuleInfo) c.createUnmarshaller().unmarshal(is);
            return i;
        } catch (JAXBException e) {
            LOG.fatal("Error while unmarshalling ModuleInfo!", e);
            return null;
        }
    }

    private void marshalModuleInfo(ModuleInfo info, OutputStream out) {
        try {
            JAXBContext c = JAXBContext.newInstance(ModuleInfo.class);
            c.createMarshaller().marshal(info, out);
        } catch (JAXBException e) {
            LOG.fatal("Error while marshalling ModuleInfo!", e);
        }
    }

    /**
     * get all currently installed modules.
     */
    public Map<String, Module> getAll() {
        Map<String, Module> modulesMap = new TreeMap<>();
        Connection conn = null;
        try {
            conn = BiBiTools.getDataSource().getConnection();
            Statement pst = conn.createStatement();
            ResultSet rset = pst.executeQuery("SELECT ID,ACTIVE, INFO FROM MODULES");
            modulesMap.clear();
            while (rset.next()) {
                String id = rset.getString(1);
                int active = rset.getInt(2);
                Module m = new Module();
                m.setId(id);
                m.setActive(active);
                Clob clob = rset.getClob(3);   
                m.setInfo(unmarshalModuleInfo(clob.getAsciiStream()));
                modulesMap.put(id, m);
            }
            rset.close();
            pst.close();
        } catch (DBConnectionException | SQLException e) {
            LOG.error("Error while generating list of currently installed modules: "
                    + e.toString(),e);
        } finally {
            try {
                if (conn != null){
                    conn.close();
                } 
            } catch (SQLException ex) {
                LOG.fatal("Exception while closing database connection:" + ex,ex);
            }
        }
        return modulesMap;
    }
}
