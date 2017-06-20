/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.mapping;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author probook
 */
public abstract class AbstractMapping implements MappingLocal{

    @Override
    public int getLocalisation(String localisation) {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("localisation.properties"));
            return Integer.parseInt(properties.getProperty(localisation.trim().toLowerCase()));
        } catch (Exception ex) {
            Logger.getLogger(AbstractMapping.class.getName()).log(Level.WARNING, ex.getMessage(),ex);
        }
        return 12;
    }

}
