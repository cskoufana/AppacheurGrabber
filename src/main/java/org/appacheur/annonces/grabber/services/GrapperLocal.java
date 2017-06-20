/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.services;

import java.util.List;
import org.appacheur.annonces.grabber.entites.Item;

/**
 *
 * @author probook
 */
public interface GrapperLocal {
    
    
    List<Item> getItemsList(String page);
    
    Item getItem(String page,Item item);
}
