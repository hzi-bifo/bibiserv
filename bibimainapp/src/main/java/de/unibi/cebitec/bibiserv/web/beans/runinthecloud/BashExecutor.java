/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.cebitec.bibiserv.web.beans.session.UserBean;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class BashExecutor implements InitializingBean {

    /**
     * Static Variables.
     */
    private static final Logger log = Logger.getLogger(BashExecutor.class);
    private static final String bibigridPropertiesFile = "bibigrid.properties";
    private static final String gridPropertiesFile = "grid.properties";

    /**
     * Injected Beans.
     */
    private UserBean user;
    private DatabaseConnect dc;
    private Ec2InstanceWizard ec2InstanceWizard;

    /**
     * Private Variables.
     */
    private boolean keypairFound = false;
    private boolean keypairForSelectedRegion = false;
    private String keypairName;
    private Path tempDirectoryPath;
    private final StringBuffer result = new StringBuffer();
    private final StringBuffer redirectAddress = new StringBuffer();
    private Process process;
    private final String toolID = getToolID();
    private final String rootPath = BiBiTools.getProperties().getProperty("tmpdir.base") + "/";
    private int progressBarValue = 0;
    /**
     * Streaming indicator variables.
     */
    private boolean readCompletely;
    private boolean active;

    /**
     * Receive and create the uniqueFolderDirectory in bibiserv/spool/tmp/.
     *
     * @return Path to UniqueFolder
     */
    private Path getUniqueFolderDirectory() {
        try {
            return Files.createTempDirectory(Paths.get(rootPath), "ritc_");
        } catch (IOException iex) {
            log.error(iex.getMessage(), iex);
            return null;
        }
    }

    /**
     * Parse toolID from RequestHeader.
     *
     * @return toolID
     */
    private String getToolID() {
        /**
         * Get Tool-ID.
         */
        URI requestURL = null;
        FacesContext ctx;
        HttpServletRequest req;
        try {
            ctx = FacesContext.getCurrentInstance();
            req = (HttpServletRequest) ctx.getExternalContext().getRequest();
            requestURL = new URI(req.getHeader("referer"));
            /**
             * referer e.g. http://localhost:9080/dialign?id=ritc
             */
            return requestURL.getPath().substring(1); // getPath()=='/dialign'
        } catch (URISyntaxException ue) {
            log.error(ue.getMessage(), ue);
            return "error";
        }
    }

    /**
     * Executed from PF-remoteCommand on confirmation.xhtml.
     */
    public void onload() {
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.execute("PF('progressBar').start()");
    }

    public BashExecutor() {
    }

    /**
     * Manual scrolling of primefaces-scrollpanel. The -1000 is an optimistic
     * value. It describes how 'deep' the scroll should be. While each poll
     * takes 2 sec a value of -50 should reach, but cause i'm using a listener
     * on poll, the function scroll() needs to be executed and takes ~ 1 more
     * sec. So -1000 should reach to scroll each time to the bottom. (If the
     * value should not reach...tune it up to 10000?)
     */
    public void scroll() {
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.execute("PF('scroller').scrollY(1000)");
    }

    /**
     * Creates the needed bibigrid.properties file which will configure the
     * bibigrid binaries to start correctly.
     *
     * @param tempDirectoryPath - The resolved unique folder id
     * @param isr - inputstream reader
     * @param out - outputStream
     * @param execScript - filediscriptor of execScript
     * @param sourceProperties - filediscriptor to the now building
     * bibigrid.properties
     */
    private void createBiBiGridPropertiesFile(
            final Path tempDirectoryPath,
            final InputStreamReader isr,
            final OutputStream out,
            final File execScript,
            final Properties sourceProperties) {
        /**
         * Get selected values from wizard.
         */
        final Integer numberOfSlaves = ec2InstanceWizard.getNumberOfSlaves();
        final String masterInstanceType = ec2InstanceWizard.getSelectedEc2MasterInstance().getInstanceName();
        final String slaveInstanceType = ec2InstanceWizard.getSelectedEc2SlaveInstance().getInstanceName();
        final String region = ec2InstanceWizard.getSelectedRegion();

        /**
         * Get identitiyfile from SSH-Keychain Module.
         */
        final File identityfile = new File(tempDirectoryPath.toFile(), user.getId() + "_identityfile.pem");
        final StringBuffer keypairname = new StringBuffer();
        try (PrintWriter id_pw = new PrintWriter(identityfile)) {
            ArrayList<SshKeyPair> foundKeyPairs = new ArrayList(dc.retrieveSshKeyFile(user));
            for (SshKeyPair kp : foundKeyPairs) {
                /**
                 * If there is one active keypair we can quit searching and take
                 * this keypair. The active keypair can be set in the
                 * KeyChainModule.
                 */
                if (kp.isActive()) {
                    id_pw.println(kp.getIdentityFile());
                    keypairname.append(kp.getKeypairName());
                    break;
                }
            }
            id_pw.close();

        } catch (FileNotFoundException fnfe) {
            log.error(fnfe.getMessage(), fnfe);
        }
        /**
         * Start new Thread for creating and writing new bibigrid.properties.
         */
        Thread bibiGridPropertiesCreatorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sourceProperties.load(isr);
                    sourceProperties.setProperty("accessKey", ec2InstanceWizard.getAwsbean().getAccessKey());
                    sourceProperties.setProperty("secretKey", ec2InstanceWizard.getAwsbean().getSecretKey());
                    sourceProperties.setProperty("region", region);
                    sourceProperties.setProperty("keypair", keypairname.toString());
                    /**
                     * The availability zones are HARD CODED! I've at this point
                     * no idea how to parse the available
                     * endpoints/availability-zones.
                     */
                    sourceProperties.setProperty("availability-zone", region + "a");
                    sourceProperties.setProperty("master-instance-type", masterInstanceType);
                    sourceProperties.setProperty("master-image", ec2InstanceWizard.getIMAGE_ID_HVM_MASTER());
                    sourceProperties.setProperty("slave-instance-type", slaveInstanceType);
                    sourceProperties.setProperty("slave-instance-min", "1");
                    sourceProperties.setProperty("slave-instance-start", String.valueOf(numberOfSlaves));
                    sourceProperties.setProperty("slave-instance-max", String.valueOf(numberOfSlaves));
                    sourceProperties.setProperty("slave-image", ec2InstanceWizard.getIMAGE_ID_HVM_SLAVE());
                    sourceProperties.setProperty("ports", "8080,8081");
                    sourceProperties.setProperty("execute-script", execScript.getName());
                    sourceProperties.setProperty("use-master-as-compute", "no");
                    sourceProperties.setProperty("grid-properties-file", gridPropertiesFile);
                    sourceProperties.setProperty("identity-file", identityfile.getName());
                    sourceProperties.store(out, null);
                } catch (IOException | NullPointerException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    try {
                        isr.close();
                        out.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });
        bibiGridPropertiesCreatorThread.start();
    }

    /**
     * Creates execScript which will be only executed on the EC2 instance to
     * install the base environment to use and install the bibiserv-clone. It
     * also installs the parsed tool on the BiBiServ.
     *
     * @param execScript
     */
    private void createExecScript(final File execScript) {
        // e.g. toolID = 'dialign'
        Thread execScriptCreatorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try (BufferedWriter output = new BufferedWriter(new FileWriter(execScript))) {
                        output.write("#!/bin/bash \n");

                        output.write("#if run as early shell script we have to set some things manually \n");
                        output.write("export SGE_ROOT=/var/lib/gridengine \n");

                        output.write("# change into instantbibi base dir \n");
                        output.write("cd /home/ubuntu/instantbibi \n");

                        output.write("# init .antlibs \n");
                        output.write("/usr/bin/ant install.antlib \n");

                        output.write("# check for updates \n");
                        output.write("/usr/bin/ant .get \n");

                        output.write("# setup and configure domain, deploy bibimainapp \n");
                        output.write("mkdir -p /vol/spool/bibiserv \n");
                        output.write("/usr/bin/ant -Dbase.dir=/vol/spool/bibiserv -Dglassfish4=true -Duse.min.module.set=true -Denable.docker=true -Dconfig.64=true -Dconfig.oge=true -Ddrmaa.jar=/usr/share/java/drmaa.jar -Ddrmaa.library.path=/usr/lib/ instant \n");

                        output.write("# deploy application \n");
                        // parsd toolID
                        output.write("/usr/bin/ant deploy.app -Dapp=" + toolID + "\n");

                        output.write("# finished");
                        output.write("/usr/bin/date");
                        output.close();
                    }

                    Runtime r = Runtime.getRuntime();
                    /**
                     * Modify Script to be executeable.
                     */
                    r.exec("chmod u+x " + execScript.getAbsolutePath());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        execScriptCreatorThread.start();
    }

    /**
     * Creates the bibigrid-starter-script which is needed to execute the
     * bibigrid binaries correctly.
     *
     * @param tempDirectoryPath
     * @param bashFile
     */
    private void createGridStartScript(
            final Path tempDirectoryPath,
            final File bashFile) {
        final String username = user.getId();
        Thread gridCreatorThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try (BufferedWriter output = new BufferedWriter(new FileWriter(bashFile))) {
                        output.write("#!" + BiBiTools.getProperties().getProperty("batchfile.shell") + " \n");
                        // set classpath
                        output.write("CLASSPATH=" + BiBiTools.getProperties().getProperty("bibigrid.bin") + " ");
                        // added -gpf to obtain the grid.properties file at the end
                        output.write(BiBiTools.getProperties().getProperty("bibigrid.bin") + "bibigrid -o " + bibigridPropertiesFile + " -gpf " + gridPropertiesFile + " -c ");
                        output.close();
                    }
                    Runtime rr = Runtime.getRuntime();
                    // setting access
                    rr.exec("chmod u+x " + bashFile.getAbsolutePath());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }

        });
        gridCreatorThread.start();
    }

    /**
     * executeGridStarterScript starts the whole grid creation-procedure. One
     * the one hand it executes the just created startGrid.sh, scans in its
     * output and brings it to the JSF result-panel on confirmation.xhtml. On
     * the other side, at the end, it reads in the grid.properties file which
     * contains e.g. the public-dns of the EC2 instance which is needed to
     * redirect to the instance. All important informations from the
     * grid.properties will get stored to the db and can be easily looked up at
     * the BiBiServ-KeyChainModule.
     */
    private void executeGridStarterScript() {
        final File bashFile = new File(tempDirectoryPath.toFile(), "");
        final String username = user.getId();

        Thread executeAndReadThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Runtime r = Runtime.getRuntime();
                    /**
                     * Start script as process.
                     */
                    process = r.exec("/bin/bash startGrid.sh", null, bashFile.getAbsoluteFile());

                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    /**
                     * Errorstream scanning.
                     */
//                    BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        result.append(inputLine);
                        result.append("\n");
                    }

                    in.close();
                    /**
                     * Read grid.properties to redirect to Ec2-instance.
                     */
                    File gridInformation = new File(tempDirectoryPath.toFile(), gridPropertiesFile);
                    InputStreamReader isr = new InputStreamReader(gridInformation.toURI().toURL().openStream());
                    Properties gridJobInfo = new Properties();
                    gridJobInfo.load(isr);
                    String masterNodeDNS = gridJobInfo.getProperty("BIBIGRID_MASTER");
                    String clusterId = gridJobInfo.getProperty("clusterId");
                    redirectAddress.delete(0, redirectAddress.length());
                    redirectAddress.append("http://");
                    redirectAddress.append(masterNodeDNS);
                    redirectAddress.append(":8080/");
                    redirectAddress.append(toolID);
                    
                    dc.insertUniqueFolderID(username, tempDirectoryPath.getFileName().toString(), true);
                    dc.insertJobIdAndRedirectToUniqueFolder(clusterId, redirectAddress.toString(), tempDirectoryPath);

                    readCompletely = true;
                    active = false;
                } catch (IOException iex) {
                    log.error(iex.getMessage(), iex);
                }
            }
        }
        );
        executeAndReadThread.start();
    }

    /**
     * Indicator if instance-access is ready.
     */
    private boolean accessReady = false;

    /**
     * CheckInstanceAccess tests wheather the started EC2 is already up and in
     * which state of starting the instance is. Furthermore it controls the
     * progressBar on the confirmation.xhtml. Each exception increments the
     * value 'progressBarValue'. Because I tested the instanceAccess so often I
     * noticed that there are ca. 15 ClientHandlerExecptions and ca. 4
     * UniformInterfaceExecptions before the 'alive' command comes from the
     * server REST-service. So each CHE increments the progressBarValue with 2
     * points and each UIE increments the value with 17. So the value will stop
     * at round about 97% and the successfull 'alive' increments it finally to
     * 99% and the redirection starts.
     */
    public void checkInstanceAccess() {
        String url = redirectAddress.substring(0, redirectAddress.lastIndexOf("/")) + "/rest";
        Client client = Client.create(new DefaultClientConfig());
        WebResource res = client.resource(url);
        String answer;
        while (true) {
            try {
                answer = res.path("manager").type(MediaType.TEXT_PLAIN).get(String.class);
                if (answer.equals("alive")) {
                    progressBarValue = 99;
                    accessReady = true;
                    break;
                }
            } catch (ClientHandlerException e) {
                /**
                 * Hier reicht ein log.info und kein break, da die einzige
                 * mögliche exception erwartet wird: java.net.ConnectException:
                 * Verbindungsaufbau abgelehnt = akzeptiert;
                 * java.net.ConnectException: Die Wartezeit für die Verbindung
                 * ist abgelaufen = falsche dns.
                 */
                // es fallen ca 15 CLEs ...
                log.error(e.getMessage(), e);
                if (progressBarValue + 2 >= 98) {

                } else {
                    progressBarValue += 2;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    log.info(ie.getMessage(), ie);
                    break;
                }
            } catch (UniformInterfaceException e) {
                // ... und ca 4 UFEs
                log.error(e.getMessage(), e);
                if (progressBarValue + 17 >= 98) {

                } else {
                    progressBarValue += 17;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    log.info(ie);
                    break;
                }
            }
        }
        connectToInstance();
    }

    /**
     * PrepareGrid gets executed on the switch from grid_settings.xhtml to
     * confirmation.xhtml to ensure a fully generated base where from the grid
     * can be started. The function creates 4 file:
     * <ul>
     * <li>
     * bibigrid.properties (on demand from user configuration)
     * </li>
     * <li>
     * identityfile (from db)
     * </li>
     * <li>
     * startGrid.sh
     * </li>
     * <li>
     * execScript
     * </li>
     * </ul>
     */
    public void prepareGrid() {
        progressBarValue = 5;
        tempDirectoryPath = getUniqueFolderDirectory();
        try {
            final URL srcPropFileName = new URL("http://bibiserv.cebitec.uni-bielefeld.de/resources/bibigrid.skel.properties");
            final File destFile = new File(tempDirectoryPath.toFile(), bibigridPropertiesFile);
            /**
             * Create new Exec-Script file.
             */
            final File execScript = new File(tempDirectoryPath.toFile(), "execScript");
            createExecScript(execScript);
            /**
             * Create new BibiGrid-properties file.
             */
            final InputStreamReader isr = new InputStreamReader(srcPropFileName.openStream());
            final OutputStream out = new FileOutputStream(destFile);
            createBiBiGridPropertiesFile(tempDirectoryPath, isr, out, execScript, new Properties());
            /**
             * Create new GridStarter-Script.
             */
            final File bashFile = new File(tempDirectoryPath.toFile(), "startGrid.sh");
            createGridStartScript(tempDirectoryPath, bashFile);
        } catch (IOException iex) {
            log.error(iex.getMessage(), iex);
        }
    }

    /**
     * Pass-through function. Can be removed in future. active needs to be set
     * somewhere outside any thread.
     */
    public void createGrid() {
        active = true;
        /**
         * Execute GridStarterScript.
         */
        executeGridStarterScript();
    }

    /**
     * Redirect to started Ec2Instance. There must be an 10sec delay to wait
     * until the tool got depoyed.
     *
     * @return
     */
    public void connectToInstance() {
        try {
            Thread.sleep(10000);
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirectAddress.toString());
        } catch (IOException | InterruptedException iox) {
            log.error(iox.getMessage(), iox);
        }
    }

    public String getResult() {
        return result.toString();
    }

    public boolean isReadCompletely() {
        return readCompletely;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAccessReady() {
        return accessReady;
    }

    public void setAccessReady(boolean accessReady) {
        this.accessReady = accessReady;
    }

    public Ec2InstanceWizard getEc2InstanceWizard() {
        return ec2InstanceWizard;
    }

    public void setEc2InstanceWizard(Ec2InstanceWizard ec2InstanceWizard) {
        this.ec2InstanceWizard = ec2InstanceWizard;
    }
    
    private String keypairRegion;
    
    public String getKeypairRegion() {
        return keypairRegion;
    }

    /**
     * Function needed for restting the wizard. Executed on the first
     * 'welcome'-tag If this function would not be called, the confirmation-tab
     * would stay at the 'finished'-state. It also checks if the keypairs got
     * set already.
     */
    public void reset() {
        active = false;
        readCompletely = false;
        result.delete(0, result.length());
        
        ec2InstanceWizard.getAwsbean().reloadCredentials();

        List<SshKeyPair> keypairs = dc.retrieveSshKeyFile(user);
        if (!keypairs.isEmpty()) {
            for (SshKeyPair kp : keypairs) {
                if (kp.isActive()) {
                    keypairFound = true;
                    keypairName = kp.getKeypairName();
                    keypairRegion = kp.getRegion();
                    break;
                }
            }
            if (keypairRegion.equals(ec2InstanceWizard.getSelectedRegion())) {
                keypairForSelectedRegion = true;
            } else {
                keypairForSelectedRegion = false;
            }
        } else {
            keypairFound = false;
        }
    }

    /**
     * For an non-executed grid creation the temporary created unique folder
     * will not be used any further. So we can delete it safely.
     */
    public void deleteUniqueFolder() {
        try {
            FileUtils.deleteDirectory(tempDirectoryPath.toFile());
            dc.deleteUniqueFolderID(new UniqueFolderID(user, tempDirectoryPath.getFileName().toString(), null, null, false, null));
        } catch (IOException io) {
            log.error(io.getMessage(), io);
        }
    }

    /**
     * Wizard onFlowProcess.
     *
     * @param event
     * @return
     */
    public String onFlowProcess(FlowEvent event) {
        // Disclaimer accepted
        if (("welcome").equals(event.getOldStep())
                && ec2InstanceWizard.isDisclaimerAccepted() == false) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Disclaimer not accepted!", "To continue you have to accept the disclaimer!"));
            RequestContext rc = RequestContext.getCurrentInstance();
            rc.execute("PF('spot').show()");
            return event.getOldStep();
        }
        // Delete UFI-Folder on 'back' from confirmation.
        if ("confirmation".equals(event.getOldStep())) {
            deleteUniqueFolder();
        }
        // prepare unique folder if 'next' is confirmation.
        if ("confirmation".equals(event.getNewStep())) {
            prepareGrid();
        }
        // No 'next' if credentials not ready
        if ("credentials".equals(event.getNewStep())) {
            reset();
        }
        return event.getNewStep();
    }

    public void hideNavigation() {
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.execute("PF('wiz').hideNextNav()");
        rc.execute("PF('wiz').showBackNav()");
    }

    public DatabaseConnect getDc() {
        return dc;
    }

    public void setDc(DatabaseConnect dc) {
        this.dc = dc;
    }

    public UserBean getUser() {
        return user;
    }

    public String getKeypairName() {
        return keypairName;
    }

    public void setKeypairName(String keypairName) {
        this.keypairName = keypairName;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getProgressBarValue() {
        return progressBarValue;
    }

    public void setProgressBarValue(int progressBarValue) {
        this.progressBarValue = progressBarValue;
    }

    public boolean isKeypairFound() {
        return keypairFound;
    }

    public void setKeypairFound(boolean keypairFound) {
        this.keypairFound = keypairFound;
    }

    public boolean isKeypairForSelectedRegion() {
        return keypairForSelectedRegion;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        active = false;
        readCompletely = false;
        result.delete(0, result.length());
    }
}
