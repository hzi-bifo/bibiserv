/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

import de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.JsonFileReader;
import de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs.Instancetype;
import de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs.Regions;
import de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater.mappingPOJOs.Size;
import de.unibi.cebitec.bibiserv.web.beans.session.AwsBean;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class Ec2InstanceWizard implements InitializingBean {

    /**
     * Instanz des AWSBeans.
     */
    private AwsBean awsbean;

    /**
     * The selected EC2-Maschine-Name (MASTER).
     */
    private Ec2Instance selectedEc2MasterInstance;
    /**
     * The selected EC2-Maschine-Name (MASTER).
     */
    private Ec2Instance selectedEc2SlaveInstance;
    /**
     * Number of slaves.
     */
    private int numberOfSlaves = 1;
    /**
     * The selected Region.
     */
    private String selectedRegion;
    /**
     * All possible regions.
     */
    private ArrayList<String> availableRegions;
    /**
     * Estimated Price.
     */
    private double estimatedPrice = 0.0;

    private boolean disclaimerAccepted = false;

    /**
     * All possible EC2-Maschine-Names.
     */
    private Map<String, List<Ec2Instance>> possibleEc2Instances_HVM_MASTER = new HashMap<>();
    private Map<String, List<Ec2Instance>> possibleEc2Instances_HVM_SLAVE = new HashMap<>();

    private Map<String, List<String>> ILIST_HVM_MASTER = new HashMap<>();
    private Map<String, List<String>> ILIST_HVM_SLAVE = new HashMap<>();

    private Map<String, List<String>> XLIST_HVM_MASTER = new HashMap<>();
    private Map<String, List<String>> XLIST_HVM_SLAVE = new HashMap<>();

    private Map<String, String> IMAGE_ID_HVM_MASTER = new HashMap<>();
    private Map<String, String> IMAGE_ID_HVM_SLAVE = new HashMap<>();

    /**
     * Ugly Pipe to ec2-list -.- .
     */
    private List<Regions> EC2REGIONS = new ArrayList<>();

    /**
     * checkRegion is an dynamic region validator which checks if the selected
     * region has a valid imageID which can be used to start an eC2 instance.
     */
    public void checkRegion() {
        if (IMAGE_ID_HVM_MASTER.get(selectedRegion).equals("")
                || IMAGE_ID_HVM_SLAVE.get(selectedRegion).equals("")) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Failure", "The selected region " + selectedRegion + " is temporary not available!"));
            for (String reg : availableRegions) {
                if (!reg.equals(selectedRegion)
                        && !IMAGE_ID_HVM_MASTER.get(reg).equals("")
                        && !IMAGE_ID_HVM_SLAVE.get(reg).equals("")) {
                    selectedRegion = reg;
                    break;
                }
            }
        }
        for (Ec2Instance ec2 : possibleEc2Instances_HVM_MASTER.get(selectedRegion)) {
            if (ec2.getInstanceName().equals(selectedEc2MasterInstance.getInstanceName())) {
                selectedEc2MasterInstance = ec2;
            }
        }
        for (Ec2Instance ec2 : possibleEc2Instances_HVM_SLAVE.get(selectedRegion)) {
            if (ec2.getInstanceName().equals(selectedEc2SlaveInstance.getInstanceName())) {
                selectedEc2SlaveInstance = ec2;
            }
        }
        calculatePrice();
    }

    public Ec2Instance getSelectedEc2MasterInstance() {
        return selectedEc2MasterInstance;
    }

    public void setSelectedEc2MasterInstance(Ec2Instance selectedEc2MasterInstance) {
        if (selectedEc2MasterInstance instanceof Ec2Instance) {
            this.selectedEc2MasterInstance = selectedEc2MasterInstance;
        }
    }

    public AwsBean getAwsbean() {
        return awsbean;
    }

    public void setAwsbean(AwsBean awsbean) {
        this.awsbean = awsbean;
    }

    public Ec2Instance getSelectedEc2SlaveInstance() {
        return selectedEc2SlaveInstance;
    }

    public void setSelectedEc2SlaveInstance(Ec2Instance selectedEc2SlaveInstance) {
        if (selectedEc2SlaveInstance instanceof Ec2Instance) {
            this.selectedEc2SlaveInstance = selectedEc2SlaveInstance;
        }
    }

    public int getNumberOfSlaves() {
        return numberOfSlaves;
    }

    public void setNumberOfSlaves(int numberOfSlaves) {
        this.numberOfSlaves = numberOfSlaves;
    }

    public String getSelectedRegion() {
        return selectedRegion;
    }

    public void setSelectedRegion(String selectedRegion) {
        if (selectedRegion instanceof String) {
            this.selectedRegion = selectedRegion;
        }
    }

    public List getPossibleHVMEc2MasterInstances() {
        return this.possibleEc2Instances_HVM_MASTER.get(this.selectedRegion);
    }

    public List getPossibleHVMEc2SlaveInstances() {
        return this.possibleEc2Instances_HVM_SLAVE.get(this.selectedRegion);
    }

    public Map<String, List<Ec2Instance>> getPossibleEc2Instances_HVM_MASTER() {
        return possibleEc2Instances_HVM_MASTER;
    }

    public void setPossibleEc2Instances_HVM_MASTER(Map<String, List<Ec2Instance>> possibleEc2Instances_HVM_MASTER) {
        this.possibleEc2Instances_HVM_MASTER = possibleEc2Instances_HVM_MASTER;
    }

    public Map<String, List<Ec2Instance>> getPossibleEc2Instances_HVM_SLAVE() {
        return possibleEc2Instances_HVM_SLAVE;
    }

    public void setPossibleEc2Instances_HVM_SLAVE(Map<String, List<Ec2Instance>> possibleEc2Instances_HVM_SLAVE) {
        this.possibleEc2Instances_HVM_SLAVE = possibleEc2Instances_HVM_SLAVE;
    }

    public List<String> getILIST_HVM_MASTER() {
        return ILIST_HVM_MASTER.get(selectedRegion);
    }

    public List<String> getILIST_HVM_SLAVE() {
        return ILIST_HVM_SLAVE.get(selectedRegion);
    }

    public List<String> getXLIST_HVM_MASTER() {
        return XLIST_HVM_MASTER.get(selectedRegion);
    }

    public List<String> getXLIST_HVM_SLAVE() {
        return XLIST_HVM_SLAVE.get(selectedRegion);
    }

    public String getIMAGE_ID_HVM_MASTER() {
        return IMAGE_ID_HVM_MASTER.get(selectedRegion);
    }

    public String getIMAGE_ID_HVM_SLAVE() {
        return IMAGE_ID_HVM_SLAVE.get(selectedRegion);
    }

    public List<String> getAvailableRegions() {
        return availableRegions;
    }

    public void setAvailableRegions(ArrayList<String> availableRegions) {
        this.availableRegions = availableRegions;
    }

    public boolean isDisclaimerAccepted() {
        return disclaimerAccepted;
    }

    public void setDisclaimerAccepted(boolean disclaimerAccepted) {
        this.disclaimerAccepted = disclaimerAccepted;
    }

    /**
     * stringToList converts a comma-seperated list of n elements to an
     * arraylist with n elements.
     *
     * @param s - Comma separated list
     * @return ArrayList
     */
    private ArrayList stringToList(String s) {
        ArrayList<String> a = new ArrayList<>();
        String[] elements = s.trim().split(",");
        a.addAll(Arrays.asList(elements));
        return a;
    }
    
    /**
     * calculates on demand the actual estimated price for the
     * selected grid-setting.
     *
     * @return
     */
    public void calculatePrice() {
        estimatedPrice = Double.parseDouble(selectedEc2MasterInstance.getPrice())
                + (numberOfSlaves * Double.parseDouble(selectedEc2SlaveInstance.getPrice()));
        DecimalFormat f = new DecimalFormat("#.###");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        f.setDecimalFormatSymbols(dfs);
        estimatedPrice = Double.parseDouble(f.format(estimatedPrice));
    }
    
    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    /**
     * checkInstances() takes care of the stored include and exclude lists at
     * the ritc.properties. The filled List output contains all accepted
     * instances which can be used to start an EC2-instance.
     *
     * @param include - IncludeList
     * @param exclude - ExcludeList
     * @param instance - InstanceType to look at
     * @param region - The specified region
     * @param output - The accepted-instances-list
     */
    private void checkInstances(List<String> include,
            List<String> exclude,
            Size instance,
            String region,
            Map<String, List<Ec2Instance>> output) {

        if (!output.containsKey(region)) {
            output.put(region, new ArrayList<Ec2Instance>());
        }
        List<Ec2Instance> temp = output.get(region);

        include_list_pv_master:
        // Check accepted instance names
        for (String includeCheck : include) {
            if (instance.getSize().startsWith(includeCheck) || includeCheck.isEmpty()) {

                /**
                 * Bedingungen aus der XLIST abwarten.
                 */
                for (String excludeCheck : exclude) {
                    if (instance.getSize().startsWith(excludeCheck)) {
                        continue include_list_pv_master;
                    }
                }
                temp.add(new Ec2Instance(
                        instance.getSize(),
                        instance.getVcpu(),
                        instance.getMemorygib(),
                        instance.getStoragegb(),
                        instance.getEcu(),
                        instance.getValuecolumns().get(0).getPrices().getUsd(),
                        region
                ));

            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * JsonFileReader reads in actual pricing-table and writes reslts to
         * this.EC2REGIONS if finished.
         */
        EC2REGIONS = JsonFileReader.parse().getConfig().getRegions();

        /**
         * Loading maschine-properties from resources.
         */
        final URL propFileDest = new URL("http://bibiserv.cebitec.uni-bielefeld.de/resources/ritc.properties");
        Properties prop_file = new Properties();

        prop_file.load(propFileDest.openStream());

        /**
         * Get all working regions.
         */
        availableRegions = stringToList(prop_file.getProperty("regions"));

        /**
         * For each regions get its include and exclude instance-lists.
         */
        for (String availableRegion : availableRegions) {
            this.IMAGE_ID_HVM_MASTER.put(availableRegion, prop_file.getProperty(availableRegion + ".hvm.master", ""));
            this.IMAGE_ID_HVM_SLAVE.put(availableRegion, prop_file.getProperty(availableRegion + ".hvm.slave", ""));

            /**
             * HVM Lists.
             */
            ILIST_HVM_MASTER.put(availableRegion, stringToList(prop_file.getProperty(availableRegion + ".hvm.master.ILIST", "")));
            ILIST_HVM_SLAVE.put(availableRegion, stringToList(prop_file.getProperty(availableRegion + ".hvm.slave.ILIST", "")));
            XLIST_HVM_MASTER.put(availableRegion, stringToList(prop_file.getProperty(availableRegion + ".hvm.master.XLIST", "")));
            XLIST_HVM_SLAVE.put(availableRegion, stringToList(prop_file.getProperty(availableRegion + ".hvm.slave.XLIST", "")));
        }

        String reg = null;
        /**
         * EC2REGIONS enthält ALLE Instanzen. Hierachie:
         * Regions->InstanceTypes->Sizes->ValueColums->Prices.
         */
        for (Regions r : EC2REGIONS) {
            if (this.availableRegions.contains(r.getRegion())) {
                /**
                 * Nur wenn die Region in den momentan verfügbaren Regionen
                 * liegt akzeptiere sie und stelle sie zur auswahl.
                 */
                reg = r.getRegion();

                /**
                 * Instancetypes = 'highmemcurrentgen'.
                 */
                for (Instancetype it : r.getInstancetypes()) {

                    /**
                     * it.getSizes() = alle Instancen der instanztypen.
                     */
                    for (Size instanceName : it.getSizes()) {
                        checkInstances(ILIST_HVM_MASTER.get(reg), XLIST_HVM_MASTER.get(reg), instanceName, reg, possibleEc2Instances_HVM_MASTER);
                        checkInstances(ILIST_HVM_SLAVE.get(reg), XLIST_HVM_SLAVE.get(reg), instanceName, reg, possibleEc2Instances_HVM_SLAVE);
                    }
                }

            }
        }
        this.selectedRegion = availableRegions.get(0);
        /**
         * Don't leave the selectedInstances to null. The estimated price
         * weather stops with an nullpointer exception.
         */
        for (Ec2Instance e : possibleEc2Instances_HVM_MASTER.get(selectedRegion)) {
            if (prop_file.getProperty(selectedRegion + ".hvm.master.DEFAULT").equals(e.getInstanceName())) {
                this.selectedEc2MasterInstance = e;
                break;
            }
        }
        for (Ec2Instance e : possibleEc2Instances_HVM_SLAVE.get(selectedRegion)) {
            if (prop_file.getProperty(selectedRegion + ".hvm.slave.DEFAULT").equals(e.getInstanceName())) {
                this.selectedEc2SlaveInstance = e;
                break;
            }
        }
        calculatePrice();
    }
}
