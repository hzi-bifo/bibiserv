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

import de.unibi.techfak.bibiserv.util.dependencyparser.javacc.ParseException;

/**
 * @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public enum ParseExceptionMessageEnum {

    noParameterWidthId("No Parameter with ID: "),
    noTypeChildParameter("No Type in Parameter: "),
    notSupportedOrImplemented("Not supported or implemented: "),
    unknownConstantValue("Unkown Constant Value: "), // Expected Values are String ('string'), Boolean (true,false), Integer ([+|-]\\d+) and Float ([+|-]\\d+\\.\\d+)!"
    empty(""),
    onToken("");

    private String message;

    private ParseExceptionMessageEnum(String message){
        this.message = message;
    }

    /**
     * Find out what type of exception occured.
     * @param e the caught exception
     * @return ParseExceptionMessageEnum corresponding to exception
     */
    public static ParseExceptionMessageEnum getType(ParseException e){
        String message = e.getMessage();

       if(message.startsWith(noParameterWidthId.message)){
           return noParameterWidthId;
       } else if(message.startsWith(noTypeChildParameter.message)){
           return noTypeChildParameter;
       } else if(message.startsWith(notSupportedOrImplemented.message)){
           return notSupportedOrImplemented;
       } else if(message.startsWith(unknownConstantValue.message)){
           return unknownConstantValue;
       } else {
           if(e.currentToken!=null){
               return onToken;
           } else {
               return empty;
           }
       }
    }

    /**
     * Return the value passed in the message of a ParseException.
     * Return Empty String if no Value or no correct message.
     * @param message Message to get Value out of.
     * @return the passed Value
     */
    public static String getValue(String message){
        String[] splitted = message.split(": ", 2);
        if(splitted.length>1){
            return splitted[1];
        }
        return "";
    }

    public String getMessage() {
        return message;
    }

}
