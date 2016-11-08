/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2010-2012 BiBiServ Curator Team"
 *
 * Contributor(s): Jan Krueger
 *
 */
package de.unibi.techfak.bibiserv.deploy.tools;

import java.io.File;

/**
 *
 * Deprecated class. Only for compatibility reason for generated tools before than 03/07/2012.
 * 
 * Class is replaced by VerifyLinks.
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 */
@Deprecated
public class VerifyImage extends VerifyLinks {

    @Deprecated
    public void setImagedir(String imagedir) {
        super.setResourcedir(imagedir);
    }

    @Deprecated
    public void setImageDir(String imagedir) {
        super.setResourcedir(imagedir);
    }

    @Deprecated
    public void setImagedir(File imagedir) {
        super.setResourcedir(imagedir);
    }

    @Deprecated
    public void setImageDir(File imagedir) {
        super.setResourcedir(imagedir);
    }
    
    @Deprecated
    public void setIgnorePattern(String pattern) {
        ignore_list.add(new Ignore(pattern));
    }
}
