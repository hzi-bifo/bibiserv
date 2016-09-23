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
 * Type of exceptions for DependencyException for better machine interpreting.
 * @author Thomas Gatter <tgatter@cebitec.uni-bielefeld.de>
 */
public enum DependencyExceptionEnum {

        /**
        * The function to test was not specified.
        */
       noFunctionId,
       /**
        * The tooldescription was not set.
        */
       noRunnableItem,
       /**
        * The dependency could not be extracted.
        */
       dependencyExtractionError,
       /**
        * Parameter wrapper was not set.
        */
       noParameterWrapper,
       /**
        * Error in setTooldescription in dependencyparser.java
        */
       setTooldescriptionException,
       /**
        * An operation is not supported.
        */
       unsupportedOperation,
       /**
        * The type is not implemented or supported.
        */
       unsupportedType,
       /**
        * An operation combination can't be solved.
        */
       unsolveableDependency,
       /**
        * Comparing of types that can't be compared.
        */
       unsupportedCompare,
       /*
        * A string input could not be casted to the correct type.
        */
       stringToTypeCastFailed,
       /**
        * Error in function setparameter in dependencyparser.java
        */
       setParameter;

}
