/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.network;

import java.util.List;
import org.appacheur.annonces.grabber.entites.Item;
import org.appacheur.annonces.grabber.services.GrapperLocal;

/**
 *
 * @author probook
 */
public class AppacheurListTask extends Thread{
    
    public static final int ITEM_THREAD_NUMBER=20;
    
    private List<Item> items;
    private GrapperLocal grapper;
    private GrapperClient client;
    private int id;

    public AppacheurListTask() {
    }
    
    

    public AppacheurListTask(GrapperLocal grapper, GrapperClient client, int id) {
        this.grapper = grapper;
        this.client = client;
        this.id = id;
    }
    
    private void initItems(List<Item> items){
        this.items = items;
    }

    public AppacheurListTask(List<Item> items) {
        super();
        initItems(items);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public GrapperLocal getGrapper() {
        return grapper;
    }

    public void setGrapper(GrapperLocal grapper) {
        this.grapper = grapper;
    }

    public GrapperClient getClient() {
        return client;
    }

    public void setClient(GrapperClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        for (Item item : items) {
            AppacheurItemTask itemtask = new AppacheurItemTask(grapper, client);
            itemtask.init(item);
            itemtask.start();
        }
    }

    public void init(List<Item> items) {
        initItems(items);
    }
    
    
    
    
}
