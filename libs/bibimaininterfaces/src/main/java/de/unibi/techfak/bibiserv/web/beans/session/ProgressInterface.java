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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.web.beans.session;

/**
 * ProgressInterface describes an interface for all information needed for a progress bar or user
 * information system for long running calculation.
 *
 * @author Jan Krueger - jkrueger[aet]cebitec.uni-bielefeld.de
 */
public interface ProgressInterface {

    public boolean isIndeterminate();

    public void setIndeterminate(boolean indeterminate);

    public String getInfotext();

    public void setInfotext(String infotext);

    public boolean isInfotextavailable();

    public MessagesInterface getMessages();

    public void setMessages(MessagesInterface messages);

    public int getPercent();

    public void setPercent(int percent);

    public int getStatuscode();

    public void setStatuscode(int statuscode);

    public String getStatusdescription();

    public void setStatusdescription(String statusdescription);

    public void setStatusavailable(boolean statusavailable);

    public boolean isStatusavailable();

    public boolean isVisible();

    public void setVisible(boolean visible);
}
