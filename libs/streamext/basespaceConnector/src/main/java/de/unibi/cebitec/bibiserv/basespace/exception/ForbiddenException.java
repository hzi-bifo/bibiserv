/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.basespace.exception;

/**
 *
 * @author gatter
 */
public class ForbiddenException extends BaseSpaceException {
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String msg) {
        super(msg);
    }

    public ForbiddenException(String msg, Throwable t) {
        super(msg, t);
    }

    public ForbiddenException(Throwable t) {
        super(t);
    }

}
