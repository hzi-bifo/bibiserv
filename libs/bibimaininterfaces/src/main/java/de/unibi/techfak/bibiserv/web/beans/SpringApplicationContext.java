/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2013 BiBiServ Support Team"
 *
 * Contributor(s): Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
package de.unibi.techfak.bibiserv.web.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Wrapper to always return a reference to the Spring Application Context from
 * within non-Spring enabled beans. Unlike Spring MVC's WebApplicationContextUtils
 * we do not need a reference to the Servlet context for this. All we need is
 * for this bean to be initialized during application startup
 * 
 * Thanks to Sujit Pal for this really elegant and simple solution 
 * (http://sujitpal.blogspot.de/2007/03/accessing-spring-beans-from-legacy-code.html)
 * 
 * 
 * .
 */
public class SpringApplicationContext implements ApplicationContextAware{
    
    private static ApplicationContext CONTEXT;
    
  /**
   * This method is called from within the ApplicationContext once it is 
   * done starting up, it will stick a reference to itself into this bean.
   * @param context a reference to the ApplicationContext.
   */
    @Override
  public void setApplicationContext(ApplicationContext context)  {
    System.err.println("SpringApplicationContext initialized");
    if (context == null){
        System.err.println("init :: Context is null");
    }
    CONTEXT = context;
  }

  /**
   * This is about the same as context.getBean("beanName"), except it has its
   * own static handle to the Spring context, so calling this method statically
   * will give access to the beans by name in the Spring application context.
   * As in the context.getBean("beanName") call, the caller must cast to the
   * appropriate target class. If the bean does not exist, then a Runtime error
   * will be thrown.
   * @param beanName the name of the bean to get.
   * @return an Object reference to the named bean.
   */
  public static Object getBean(String beanName) {
      if (CONTEXT != null) {
        return CONTEXT.getBean(beanName);
      }
      System.err.println("CONTEXT is null");
      return null;
  }
    
}
