/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * by your own identifying information:
 *
 * "Portions Copyrighted 2011 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Jan Krueger, jkrueger(at)cebitec.uni-bielefeld.de
 *
 */


/* Provide some function used by tools submission page to show/hide, highlight components */
             
function setCSSclass(id,cssclass){
    document.getElementById(id).className = cssclass;
}

function show(id,idparent){
    var element = document.getElementById(id);
    element.style.visibility="visible";    
}
function hide(id){
    var element = document.getElementById(id);
    element.style.visibility="hidden";
}

