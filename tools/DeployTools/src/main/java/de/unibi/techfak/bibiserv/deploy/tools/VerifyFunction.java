/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.deploy.tools;

import de.unibi.techfak.bibiserv.cms.TenumParam;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.Texample.Prop;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.Tfunction.Inputref;
import de.unibi.techfak.bibiserv.cms.TinputOutput;
import de.unibi.techfak.bibiserv.cms.Tparam;
import de.unibi.techfak.bibiserv.cms.TparamGroup;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.tools.ant.BuildException;

/**
 * VerifyFucntion is small tool class that check if every param, input and ouput
 * is referenced by the inputAndOutputOrder.
 * 
 * This class extends the ant Task class and overwrites its execute class.
 *
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public class VerifyFunction extends AbstractVerifyTask {

    @Override
    public void execute() throws BuildException {
        super.execute();
        if (getRunnableitem().isSetExecutable()) {
        
        // for all functions ...
        for (Tfunction tf : getRunnableitem().getExecutable().getFunction()) {
            System.out.println("Found function :" + tf.getId());
            List<JAXBElement<?>> paramAndInputOutputOrderList = tf.getParamAndInputOutputOrder().getReferenceOrAdditionalString();
            List<String> paramAndInputOutputOrderidList = new ArrayList<String>();
            for (JAXBElement<?> jaxbe : paramAndInputOutputOrderList) {
                if (jaxbe.getValue() instanceof TinputOutput) {
                    paramAndInputOutputOrderidList.add(((TinputOutput) jaxbe.getValue()).getId());
                } else if (jaxbe.getValue() instanceof Tparam) {
                    paramAndInputOutputOrderidList.add(((Tparam) jaxbe.getValue()).getId());
                } else if (jaxbe.getValue() instanceof TenumParam) {
                    paramAndInputOutputOrderidList.add(((TenumParam) jaxbe.getValue()).getId());
                } else {
                    System.out.println("ignore unsupported type :" + jaxbe.getValue().getClass().getName());
                }
            }           
            List<String> paramAndInputOutputReferenceIdList = getInputOutputIdList(tf.getId());
            try {
                paramAndInputOutputReferenceIdList.addAll(getParamIdList(tf.getId()));
            } catch (Exception e) {
                throw new BuildException(e);
            }

             // check if example(s) exists and all refid are valid and all values are valid
            for (Texample te : tf.getExample()) {
                for (Prop p : te.getProp()) {
                    if (!paramAndInputOutputOrderidList.contains(p.getIdref())) {
                        throw new BuildException("The example tag named '"+te.getName()+"' of function '"+tf.getId()+"' has an unknown reference '"+p.getIdref()+"'!");
                    }
                }
            }
            
            if(!VerifyDownload.compareEntries(paramAndInputOutputOrderidList, paramAndInputOutputReferenceIdList, new ArrayList<String>())){
                if (!paramAndInputOutputOrderidList.isEmpty()) {
                    throw new BuildException("The ParamAndInputOutputOrder tag of function '"+tf.getId()+" contains refId(s) ["+VerifyDownload.printList(paramAndInputOutputOrderidList)+"] which are not part of this function!");
                } else {
                    throw new BuildException("The ParamAndInputOutputOrder tag of function '"+tf.getId()+" misses refId(s) ["+VerifyDownload.printList(paramAndInputOutputReferenceIdList)+"] !");
                }
            }
            System.out.println(" -> References seems to be ok!");
            
           
        }
        } else {
            System.out.println("No Executable found ...");
        }
    }

    public List<String> getInputIdList(String function_id) {
        List<String> list = new ArrayList<String>();
        for (TinputOutput input : getInputList(function_id)) {
            list.add(input.getId());
        }
        return list;
    }

    /**
     * Return a list of input(s) used/referenced by  given function id.
     *
     * @param function_id
     * @return Return a list of inputs
     */
    public List<TinputOutput> getInputList(String function_id) {
        List<TinputOutput> list = new ArrayList<TinputOutput>();
        for (Inputref ref : getFunction(function_id).getInputref()) {
            list.add((TinputOutput) ref.getRef());
        }
        return list;
    }

    public String getOutputId(String function_id) {
        return getOutput(function_id).getId();
    }

    /** Return an output used/referenced by given function id/
     *
     * @param function_id
     * @return return output
     */
    public TinputOutput getOutput(String function_id) {
        return (TinputOutput) (getFunction(function_id).getOutputref().getRef());
    }

    public List<String> getInputOutputIdList(String function_id) {
        List<String> list = getInputIdList(function_id);
        list.add(getOutputId(function_id));
        return list;
    }

    /**
     * Return a list of input(s) and ouput used/referenced by  given function id.
     *
     * @param function_id
     * @return
     */
    public List<TinputOutput> getInputOutputList(String function_id) {
        List<TinputOutput> list = getInputList(function_id);
        list.add(getOutput(function_id));
        return list;
    }

    /**
     * Return a list of parameterIds used/referenced by given function id.
     *
     * @param function_id
     * @return Return a list of parameterIds.
     */
    public List getParamList(String function_id) throws Exception {
        List list = new ArrayList();
        if (getFunction(function_id).isSetParamGroup()) {
            buildParamList(getFunction(function_id).getParamGroup(), list);
        }

        return list;
    }

    /**
     * Return a list of parameterIds used/referenced by given function id.
     *
     * @param function_id
     * @return Return a list of parameterIds.
     */
    public List<String> getParamIdList(String function_id) throws Exception {
        List<String> list = new ArrayList<String>();
        for (Object o : getParamList(function_id)) {
            if (o instanceof Tparam) {
                list.add(((Tparam)o).getId());
            } else {
                list.add(((TenumParam)o).getId());
            }
        }
        return list;
    }

    /**
     * Private helper method, search recursvily for parameter
     *
     * @param c an object of type Tparam or TparamGroup
     * @param l
     * @throws Exception if an unknown object
     */
    private void buildParamList(Object c, List l) throws Exception {
        if (c instanceof Tparam || c instanceof TenumParam ) {
            l.add( c);
        } else if (c instanceof TparamGroup) {
            TparamGroup tpg = (TparamGroup) c;
            for (Object o : tpg.getParamrefOrParamGroupref()) {
                if (o instanceof TparamGroup.Paramref) {
                    buildParamList(((TparamGroup.Paramref) o).getRef(), l);
                } else {
                    buildParamList(((TparamGroup.ParamGroupref) o).getRef(), l);
                }
            }
        } else {
            throw new Exception("Unkown object of type '" + c.getClass().getName() + "'. Aborting!");
        }
    }

    /**
     * Return the function with given id (if exists)
     *
     * @param function_id
     * @return
     */
    public Tfunction getFunction(String function_id) {
        for (Tfunction tf : getRunnableitem().getExecutable().getFunction()) {
            if (tf.isSetId() && tf.getId().equals(function_id)) {
                return tf;
            }
        }
        return null;
    }
}
