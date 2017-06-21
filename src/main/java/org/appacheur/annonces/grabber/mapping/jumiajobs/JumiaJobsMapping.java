/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.mapping.jumiajobs;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.appacheur.annonces.grabber.mapping.AbstractMapping;

/**
 *
 * @author probook
 */
public class JumiaJobsMapping extends AbstractMapping {

    @Override
    public String getExtraField(String extraField) {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("extraField.properties"));
            return properties.getProperty(extraField.toLowerCase());
        } catch (Exception ex) {
            Logger.getLogger(AbstractMapping.class.getName()).log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    @Override
    public String getCategorie(String category) {
        return "19";
    }

}
