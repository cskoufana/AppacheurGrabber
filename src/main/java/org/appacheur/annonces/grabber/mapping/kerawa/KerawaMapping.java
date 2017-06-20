/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.mapping.kerawa;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.appacheur.annonces.grabber.mapping.AbstractMapping;

/**
 *
 * @author probook
 */
public class KerawaMapping extends AbstractMapping {

    @Override
    public String getExtraField(String extraField) {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("extraField.properties"));
            return properties.getProperty(extraField.toLowerCase());
        } catch (Exception ex) {
            Logger.getLogger(AbstractMapping.class.getName()).log(Level.WARNING, ex.getMessage(),ex);
        }
        return null;
    }

    @Override
    public String getCategorie(String category) {
        try {
            Properties properties = new Properties();
            String[] dd = category.split("-");
            category = dd[dd.length-1];
            properties.load(getClass().getResourceAsStream("category.properties"));
            System.out.println(category);
            category= properties.getProperty(category.trim().replaceAll("[\\s'_-]+", "").replace(",","").toLowerCase());
            System.out.println(category);
            return category != null ? category :"20";
        } catch (Exception ex) {
            Logger.getLogger(AbstractMapping.class.getName()).log(Level.WARNING, ex.getMessage(),ex);
        }
        return "20";
    }

}
