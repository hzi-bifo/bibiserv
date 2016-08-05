/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class DatabaseConnect implements InitializingBean {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private DataSource datasource;
    private Context context;
    /**
     * Table names.
     */
    private final static String SSH_TABLE_NAME = "RITC_SSH_KEYCHAIN";
    private final static String UFI_TABLE_NAME = "RITC_UNIQUEFOLDERID";

    // Logger
    private final static Logger log = Logger.getLogger(DatabaseConnect.class.getName());

    /**
     * Static exception messages.
     */
    private final static String COULD_NOT_CLOSE_STMT = "Could not close statement.";
    private final static String COULD_NOT_CLOSE_CON = "Could not close connection.";

    /**
     * Standard c'tor.
     */
    public DatabaseConnect() {
    }

    /**
     * inputStreamToString takes an clob-inputstream and converts it to a String
     * containing the clob data. This can be displayed and used at other places.
     *
     * @param in
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String inputStreamToString(InputStream in) throws FileNotFoundException,
            IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String nextLine = "";
        StringBuilder sb = new StringBuilder();
        while ((nextLine = br.readLine()) != null) {
            sb.append(nextLine).append("\n");
        }
        // Convert the content into to a string
        String clobData = sb.toString();

        // Return the data.
        return clobData;
    }

    /**
     * Create the two used tables and checks wheather they are already existing.
     */
    public final void createTableIfNotExist() {
        final String queryS = "CREATE TABLE " + SSH_TABLE_NAME + " ("
                + "username varchar(255) not null, "
                + "keypairname varchar(255), "
                + "region varchar(255), "
                + "identityfile clob default null, "
                + "setdate timestamp,"
                + "active smallint default 0"
                + ")";
        final String querySs = "CREATE TABLE " + UFI_TABLE_NAME + " ("
                + "username varchar(255) not null, "
                + "uniquefolderid varchar(255) default null, "
                + "jobid varchar(255),"
                + "redirect varchar(255),"
                + "setdate timestamp,"
                + "active smallint default 0"
                + ")";
        try {
            connection = datasource.getConnection();
            preparedStatement = connection.prepareStatement(queryS);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            /**
             * XOY32 is the error which describes an already existing table.
             */
            if (e.getSQLState().equals("X0Y32")) {
                log.info("SQL-Table [" + SSH_TABLE_NAME + "] already exists! Continue.");
            } else {
                log.error(e.getMessage(), e);
            }
        }
        try {
            preparedStatement = connection.prepareStatement(querySs);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                log.info("SQL-Table [" + UFI_TABLE_NAME + "] already exists! Continue.");
            } else {
                log.error(e.getMessage(), e);
            }
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Insert new ssh key pair to the db.
     *
     * @param username
     * @param keypairName
     * @param region
     * @param file
     * @param active
     * @return 0 for a successfull insert.
     */
    public final int insertSshKey(String username,
            String keypairName,
            String region,
            UploadedFile file,
            boolean active) {
        final String sshInsertQuery = "INSERT INTO "
                + SSH_TABLE_NAME
                + " (username, "
                + "keypairname, region, identityfile, active, setdate) VALUES (?,?,?,?,?,?)";
        try {
            connection = datasource.getConnection();
            Clob myClob = connection.createClob();
            myClob.setString(1, inputStreamToString(file.getInputstream()));

            preparedStatement = connection.prepareStatement(sshInsertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, keypairName);
            preparedStatement.setString(3, region);
            preparedStatement.setClob(4, myClob);
            preparedStatement.setInt(5, active == true?1:0);
            preparedStatement.setTimestamp(6, new java.sql.Timestamp(new java.util.Date().getTime()));
            preparedStatement.executeUpdate();
            return 0;

        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            return se.getErrorCode();
        } catch (FileNotFoundException fe) {
            log.error(fe.getMessage(), fe);
            return -1;
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            return -2;
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Delete an existing ssh key pair.
     *
     * @param sshKP
     * @return 0 for a successfull delete.
     */
    public final int deleteSshKey(SshKeyPair sshKP) {
        final String deleteQuery = "DELETE FROM "
                + SSH_TABLE_NAME
                + " WHERE setdate=? AND username=?";
        try {
            connection = datasource.getConnection();

            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setTimestamp(1, sshKP.getSetDate());
            preparedStatement.setString(2, sshKP.getUsername());
            preparedStatement.executeUpdate();
            return 0;
        } catch (SQLException sq) {
            return sq.getErrorCode();
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Usually called to change keypair to active or vice versa.
     *
     * @param sshKP
     * @return - 0 if success, else if error
     */
    public final int updateSshKey(SshKeyPair sshKP) {
        final String deleteQuery = "UPDATE "
                + SSH_TABLE_NAME
                + " SET active=? WHERE setdate=? AND username=?";
        try {
            connection = datasource.getConnection();

            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, sshKP.isActive()?1:0);
            preparedStatement.setTimestamp(2, sshKP.getSetDate());
            preparedStatement.setString(3, sshKP.getUsername());
            preparedStatement.executeUpdate();
            return 0;
        } catch (SQLException sq) {
            return sq.getErrorCode();
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Insert the uniqueFolderId to the db. This is necessary for an alter
     * access from the KeyChainModule.
     *
     * @param username
     * @param uniquefolderid
     * @param active
     * @return 0 for a successfull insert.
     */
    public final int insertUniqueFolderID(String username, String uniquefolderid, boolean active) {
        final String sshInsertQuery = "INSERT INTO "
                + UFI_TABLE_NAME
                + " (username, "
                + "uniquefolderid, active, setdate) VALUES (?,?,?,?)";
        try {
            connection = datasource.getConnection();

            preparedStatement = connection.prepareStatement(sshInsertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, uniquefolderid);
            preparedStatement.setInt(3, active?1:0);
            /**
             * actual timestamp.
             */
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
            preparedStatement.executeUpdate();
            return 0;

        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            return se.getErrorCode();
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Delete any existing UniqueFolder.
     *
     * @param ufi
     * @return 0 for a successfull deleting.
     */
    public final int deleteUniqueFolderID(UniqueFolderID ufi) {
        final String deleteQuery = "DELETE FROM "
                + UFI_TABLE_NAME
                + " WHERE uniquefolderid=? AND username=? AND jobid = 'NULL'";
        try {
            connection = datasource.getConnection();
            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1, ufi.getUniquefolderid());
            preparedStatement.setString(2, ufi.getUser().getId());
            preparedStatement.executeUpdate();
            return 0;
        } catch (SQLException sq) {
            return sq.getErrorCode();
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Update an UniqueFolder with active or inactive.
     *
     * @param username
     * @param uniquefolderid
     * @param active
     * @return 0 for an successfull update.
     */
    public final int updateUniqueFolderID(String username, String uniquefolderid, boolean active) {
        final String deleteQuery = "UPDATE "
                + UFI_TABLE_NAME
                + " SET active=? WHERE uniquefolderid=? AND username=?";
        try {
            connection = datasource.getConnection();

            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, active?1:0);
            preparedStatement.setString(2, uniquefolderid);
            preparedStatement.setString(3, username);
            preparedStatement.executeUpdate();
            return 0;
        } catch (SQLException sq) {
            return sq.getErrorCode();
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Updating any existing UniqueFolder with the current cluster-jobID and the
     * corresponding redirect-address of the EC2-instance.
     *
     * @param clusterId
     * @param redirect
     * @param tempDirectoryPath
     */
    public void insertJobIdAndRedirectToUniqueFolder(
            String clusterId,
            String redirect,
            Path tempDirectoryPath) {
        final String jobIdQuery = "UPDATE "
                + UFI_TABLE_NAME
                + " SET jobid=?, redirect=? WHERE uniquefolderid=? ";
        try {
            connection = datasource.getConnection();

            preparedStatement = connection.prepareStatement(jobIdQuery);
            preparedStatement.setString(1, clusterId);
            preparedStatement.setString(2, redirect);
            preparedStatement.setString(3, tempDirectoryPath.getFileName().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException sq) {
            log.error(sq.getSQLState(), sq);
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Retrieve all stored key pairs.
     *
     * @param user
     * @return List of SshKeyPairs
     */
    public List<SshKeyPair> retrieveSshKeyFile(UserBean user) {
        final String retrSshKeyQuery = "SELECT keypairname, region, identityfile, active, setdate FROM "
                + SSH_TABLE_NAME
                + " WHERE username='"
                + user.getId() + "'";

        try {
            connection = datasource.getConnection();
            preparedStatement = connection.prepareStatement(retrSshKeyQuery);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<SshKeyPair> result = new ArrayList<>();

            /**
             * For each found keypair...
             */
            while (rs.next()) {
                Clob c = rs.getClob("identityfile");
                int filesize = (int) (c.length());
                // ... create new Keypair ...
                SshKeyPair tmp = new SshKeyPair(
                        user,
                        rs.getString("keypairname"),
                        rs.getString("region"),
                        c.getSubString(1, filesize),
                        rs.getInt("active")>0,
                        rs.getTimestamp("setdate"),
                        filesize);
                // ... and add it to the result-list.
                result.add(tmp);
            }

            return result;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            return null;
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    /**
     * Retrieve all stored UniqueFolderIDs which holds a valid jobID and a
     * redirectAddress.
     *
     * @param user
     * @return List of all UniqueFolderIDs containing a jobID and
     * redirect-address.
     */
    public List<UniqueFolderID> retrieveUniqueFolderID(UserBean user) {
        final String retrSshKeyQuery = "SELECT uniquefolderid, jobid, redirect, active, setdate FROM "
                + UFI_TABLE_NAME
                + " WHERE username='"
                + user.getId()
                + "' AND jobid != 'null' order by setdate desc";

        try {
            connection = datasource.getConnection();
            preparedStatement = connection.prepareStatement(retrSshKeyQuery);
            /**
             * Set output-limit to 5.
             */
            preparedStatement.setMaxRows(5);
            ResultSet rs = preparedStatement.executeQuery();

            ArrayList<UniqueFolderID> result = new ArrayList<>();

            /**
             * For each found UFI...
             */
            while (rs.next()) {
                // ... create new UFI object ...
                UniqueFolderID tmp = new UniqueFolderID(user,
                        rs.getString("uniquefolderid"),
                        rs.getString("jobid"),
                        rs.getString("redirect"),
                        rs.getInt("active")>0,
                        rs.getTimestamp("setdate"));
                // ... and add it to the result list.
                result.add(tmp);
            }

            return result;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            return null;
        } finally {
            // close statement
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_STMT, ex);
            }
            // close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                log.fatal(COULD_NOT_CLOSE_CON, ex);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            /**
             * Look and set for DataBase connector.
             */
            datasource = BiBiTools.getDataSource();
            /**
             * create tables if they don't exist already.
             */
            createTableIfNotExist();
        } catch (DBConnectionException ex) {
            log.fatal("An exception occured while getting a DataSource object from BiBiTools:", ex);
        }
    }

}
