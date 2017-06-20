/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.network;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.appacheur.annonces.grabber.entites.Item;
import org.appacheur.annonces.grabber.services.GrapperLocal;

/**
 *
 * @author Koufana Crepin Sosthene
 */
public class AppacheurItemTask extends Thread {

    private Item item;
    private GrapperLocal grapper;
    private GrapperClient client;
    private int id;

    public AppacheurItemTask() {
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    private void initItem(Item item) {
        this.item = item;
    }

    public void init(Item item) {
        initItem(item);
    }

    public AppacheurItemTask(GrapperLocal grapper) {
        this.grapper = grapper;
    }

    public AppacheurItemTask(GrapperLocal grapper, GrapperClient client) {
        this.grapper = grapper;
        this.client = client;
    }

    public AppacheurItemTask(int id) {
        this.id = id;
    }

    public AppacheurItemTask(Item item, GrapperLocal grapper) {
        this.item = item;
        this.grapper = grapper;
    }

    public AppacheurItemTask(Item item) {
        super();
        initItem(item);
    }

    public GrapperLocal getGrapper() {
        return grapper;
    }

    public void setGrapper(GrapperLocal grapper) {
        this.grapper = grapper;
    }

    private void populateItem() throws IOException {
        grapper.getItem(client.getHttpPage(item.getSrcLink()), item);
        if ((item.getImages() != null && item.getImages().size() > 0) || item.getCatId() == 19) {
            client.populateImages(item);
        }
    }

    private boolean addAnonce() {
        if ((item.getImages() != null && item.getImages().size() > 0) || item.getCatId() == 19) {
            client.addAnnonce(item);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        LOG.log(Level.INFO, "Debut thread {0}", id);
        try {
            populateItem();
            addAnonce();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        LOG.log(Level.INFO, "Fin thread {0}", id);
    }
    private static final Logger LOG = Logger.getLogger(AppacheurItemTask.class.getName());

}
