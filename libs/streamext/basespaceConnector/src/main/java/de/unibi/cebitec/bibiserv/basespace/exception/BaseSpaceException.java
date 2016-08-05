

package de.unibi.cebitec.bibiserv.basespace.exception;


public class BaseSpaceException extends RuntimeException
{
    public BaseSpaceException() {
        super();
    }

    public BaseSpaceException(String msg) {
        super(msg);
    }

    public BaseSpaceException(String msg, Throwable t) {
        super(msg, t);
    }

    public BaseSpaceException(Throwable t) {
        super(t);
    }

}
