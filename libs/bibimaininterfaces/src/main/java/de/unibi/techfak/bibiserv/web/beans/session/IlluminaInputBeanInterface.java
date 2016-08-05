package de.unibi.techfak.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.basespace.Scope;

/**
 * Interface defining the basic functionality needed in all Inputs using
 * Illumina BaseSpace.
 *
 * @author Thomas Gatter
 */
public interface IlluminaInputBeanInterface {

    /**
     * Adds the access needs of the object implementing this interface to the
     * Scope object.
     *
     * @param scope the object to add access request to
     * @return True: adding successful; false: could not add (for example
     * invalid object)
     */
    public boolean addToScope(Scope scope);

    /**
     * The Id of the input to identify the input object.
     *
     * @return
     */
    public String getId();

}
