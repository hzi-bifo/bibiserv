/*
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
All rights reserved.

The contents of this file are subject to the terms of the Common
Development and Distribution License("CDDL") (the "License"). You
may not use this file except in compliance with the License. You can
obtain a copy of the License at http://www.sun.com/cddl/cddl.html

See the License for the specific language governing permissions and
limitations under the License.  When distributing the software, include
this License Header Notice in each file.  If applicable, add the following
below the License Header, with the fields enclosed by brackets [] replaced
 by your own identifying information:

"Portions Copyrighted 2011 BiBiServ"

Contributor(s):
*/
package de.unibi.techfak.bibiserv.util.dependencyparser;

/**
 *
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
public class DependencyException extends Exception{

    /**
     * What is the exception that occured.
     */
    private DependencyExceptionEnum exceptionType;
    /**
     * Extra information of the exception, p.e. the function id.
     */
    private String value;

    public DependencyException(){
        super();
    }

    public DependencyException(String message, DependencyExceptionEnum type, String value){
        super(message);
        this.exceptionType = type;
        this.value = value;
    }

    public DependencyException( Throwable t, DependencyExceptionEnum type, String value){
        super(t);
        this.exceptionType = type;
        this.value = value;
    }

    public DependencyException(String message, Throwable t, DependencyExceptionEnum type, String value){
        super(message,t);
        this.exceptionType = type;
        this.value = value;
    }

    public DependencyExceptionEnum getExceptionType() {
        return exceptionType;
    }

    public String getValue() {
        return value;
    }

}
