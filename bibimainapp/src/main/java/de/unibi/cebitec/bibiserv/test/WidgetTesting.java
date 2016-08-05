/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.faces.context.FacesContext;
import org.primefaces.component.scrollpanel.ScrollPanel;
import org.primefaces.context.RequestContext;

/**
 *
 * @author jsteiner
 */
public class WidgetTesting {

    /**
     * Scroll Panel testing.
     */
    private final StringBuffer result = new StringBuffer();
    private boolean readCompletely;
    private boolean active;

    /**
     * Blockui testing with progressBar.
     */
    private boolean ready = false;
    private int progress = 0;

    public void scroll() {
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.execute("PF('scroller').scrollY(1000)");
    }

    public void reset() {
        ready = false;
        progress = 0;
    }

    public void countSlow() {
        while (progress <= 100) {
            progress += 10;
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {

            }
        }
    }

    public void waitAndSet() {
        try {
            Thread.currentThread().sleep(5000);
            ready = true;
        } catch (InterruptedException e) {

        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void startMe() {

        Thread executeAndReadThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Runtime r = Runtime.getRuntime();
                    /**
                     * Start script as process.
                     */
                    Process process = r.exec("/bin/bash /home/jsteiner/test.sh");

                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    /**
                     * Errorstream scanning.
                     */
//                    BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        result.append(inputLine);
                        result.append("<br />");
                    }

                    readCompletely = true;
                    active = false;
                    in.close();
                    /**
                     * Read grid.properties to redirect to Ec2-instance.
                     */
//                    File gridInformation = new File(tempDirectoryPath.toFile(), gridPropertiesFile);
//                    InputStreamReader isr = new InputStreamReader(gridInformation.toURI().toURL().openStream());
//                    Properties gridJobInfo = new Properties();
//                    gridJobInfo.load(isr);
//                    String masterNodeDNS = gridJobInfo.getProperty("BIBIGRID_MASTER");
//                    String clusterId = gridJobInfo.getProperty("clusterId");
//                    redirectAddress.delete(0, redirectAddress.length());
//                    redirectAddress.append("http://");
//                    redirectAddress.append(masterNodeDNS);
//                    redirectAddress.append(":8080/");
//                    redirectAddress.append(toolID);
//                    dc.insertJobIdAndRedirectToUniqueFolder(clusterId, redirectAddress.toString(), tempDirectoryPath);
                } catch (IOException iex) {
//                    log.error(iex.getMessage(), iex);
                }
            }
        }
        );
        executeAndReadThread.start();
    }

    public boolean isReadCompletely() {
        return readCompletely;
    }

    public void setReadCompletely(boolean readCompletely) {
        this.readCompletely = readCompletely;
    }

    public String getResult() {
        return result.toString();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
