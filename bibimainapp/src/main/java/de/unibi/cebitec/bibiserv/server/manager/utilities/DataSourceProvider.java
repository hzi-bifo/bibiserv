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
 *  by your own identifying information:
 * 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 *	Christian Henke <chenke@cebitec.uni-bielefeld.de>
 * 
 */
package de.unibi.cebitec.bibiserv.server.manager.utilities;

import de.unibi.techfak.bibiserv.exception.DBConnectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class DataSourceProvider {

  private static Logger LOG = Logger.getLogger(DataSourceProvider.class);
  private static DataSource datasource = null;
  // @TODO: Hardcoded DataSource -> move to property!!!
  public static final String LOOKUP_STRING = "jdbc/bibiserv2";

  /** helper function - used to initialize
   *  the datasource.
   * @throws de.unibi.techfak.bibiserv.exception.DBConnectionException
   */
  public static DataSource getDataSource(String lookupString) throws DBConnectionException {
	if (datasource == null) {
	  try {
		final Context dbctx = new InitialContext();
		if (dbctx == null) {
		  throw new DBConnectionException();
		}
		datasource = (DataSource) dbctx.lookup(lookupString);
	  } catch (NamingException ex) {
		LOG.fatal("A NamingException occurred : " + ex.getMessage());
		throw new DBConnectionException();
	  }

	  LOG.info("DataSource initalized");
	}
	return datasource;
  }

  public static DataSource getDataSource() throws DBConnectionException {
	return getDataSource(LOOKUP_STRING);
  }
}
