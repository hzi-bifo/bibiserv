/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class Ec2InstanceMasterConverter implements Converter {

    private Ec2InstanceWizard ec2InstanceWizard;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if ( ec2InstanceWizard != null && !ec2InstanceWizard.getPossibleHVMEc2MasterInstances().isEmpty()) {
            for (Object ec2 : ec2InstanceWizard.getPossibleHVMEc2MasterInstances()) {
                if (((Ec2Instance) ec2).getInstanceName().equals(value)) {
                    return ec2;
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Ec2Instance) value).getInstanceName();
    }

    public Ec2InstanceWizard getEc2InstanceWizard() {
        return ec2InstanceWizard;
    }

    public void setEc2InstanceWizard(Ec2InstanceWizard ec2InstanceWizard) {
        this.ec2InstanceWizard = ec2InstanceWizard;
    }

}
