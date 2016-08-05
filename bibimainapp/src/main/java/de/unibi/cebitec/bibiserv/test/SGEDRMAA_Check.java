/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.test;

import de.unibi.techfak.bibiserv.BiBiTools;
import de.unibi.techfak.bibiserv.impl.DRMAACall;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author jkrueger
 */
public class SGEDRMAA_Check {

    public String getSgeroot() {
        String v = System.getenv("SGE_ROOT");
        if (v == null || v.isEmpty()) {
            return "not set";
        }
        return v;

    }

    public String getSgeexecdport() {
        String v = System.getenv("SGE_EXECD_PORT");
        if (v == null || v.isEmpty()) {
            return "not set";
        }
        return v;
    }

    public String getSgeqmasterport() {
        String v = System.getenv("SGE_QMASTER_PORT");
        if (v == null || v.isEmpty()) {
            return "not set";
        }
        return v;
    }

    public String getSgecell() {
        String v = System.getenv("SGE_CELL");
        if (v == null || v.isEmpty()) {
            return "not set (use default)";
        }
        return v;
    }

    public String getLdlibrarypath() {
        String v = System.getenv("LD_LIBRARY_PATH");
        if (v == null || v.isEmpty()) {
            return "not set";
        }
        return v;
    }
    BiBiTools wsstools;
    String result = "";
    String status = "";

    public void rundemo() {

        try {
           result = "";
           status = "";

            wsstools = new BiBiTools();
            String exec = "/bin/echo Hello World";
            DRMAACall instance = new DRMAACall(wsstools);

            if (instance.call(exec)) {


                result = BiBiTools.i2s(new InputStreamReader(instance.getStdOutStream()));

                status = "ok";

            } else {

                status = "exec of '" + exec + "' fails ...";
            }
        } catch (Exception e) {
            status = e.getMessage();
        }
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }
}
