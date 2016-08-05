/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.statistics.logging.database;

/**
 * Custom Exception to throw from DBGetter.
 * Provides the possibility to add further information and
 * so on to the thrown error if needed.
 * @author jschmolke
 */
public class MySQLException extends Exception{
    
    /**
     * Standardconstructor.
     * Calls the Exceptionconstructor.
     * @param exMsg The errormessage.
     */
    public MySQLException(String exMsg){
        super(exMsg);
    }    
    
    public MySQLException(Throwable t){
        super(t);
    }
    
    public MySQLException(String exMsg, Throwable t) {
        super(exMsg, t);
    }
    
    
    
}
