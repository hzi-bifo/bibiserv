package de.unibi.cebitec.bibiserv.client.manager;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class ManagerClientTask extends Task {

    private String action;
    private String server;
    private String port;
    private String file;
    private String name;
    private Boolean ssl;
    private String usage = "";
    private ManagerClient managerclient;

    @Override
    public void execute() throws BuildException {

        try {
            // initialize ManagerApplication
            if (server != null) {
                managerclient = new ManagerClient("server", (port == null) ? "80" : port, false);
            } else {
                managerclient = new ManagerClient();
            }


            if (action != null) {
                if (action.equals("deploy")) {
                    if (file != null && (new File(file).exists())) {
                        managerclient.deploy(new File(file));
                    } else {
                        new BuildException(usage);
                    }
                } else if (action.equals("undeploy")) {
                    if (name != null) {
                        managerclient.undeploy(name);
                    } else {
                        new BuildException(usage);
                    }
                } else {
                    new BuildException("no Action given :" +usage);
                }
            } else {
                new BuildException(usage);
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }



    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setSsl(Boolean ssl){
        this.ssl = ssl;
    }
}
