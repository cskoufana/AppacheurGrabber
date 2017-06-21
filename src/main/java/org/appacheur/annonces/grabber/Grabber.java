/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.PNGDecodeParam;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JPEGDescriptor;
import javax.media.jai.operator.PNGDescriptor;
import javax.media.jai.operator.ScaleDescriptor;
import org.apache.commons.io.FileUtils;
import org.appacheur.annonces.grabber.entites.Item;
import org.appacheur.annonces.grabber.entites.ItemImage;
import org.appacheur.annonces.grabber.network.AppacheurListTask;
import org.appacheur.annonces.grabber.network.GrapperClient;
import org.appacheur.annonces.grabber.services.KerawaGrapper;

/**
 *
 * @author probook
 */
public class Grabber {

    public static int courant;

    public static String URL = "https://kerawa.com/cameroun-r40";

    public static void main(String[] args) throws Exception {
        loadFile3();
    }

    public static void loadFile2() throws Exception {
        GrapperClient client = new GrapperClient();
        client.connexion();
        KerawaGrapper grapper = new KerawaGrapper();
        Item item = grapper.getItem(client.getHttpsPage("https://kerawa.com/2744925_avensis-a-saisir-absolument"), null);
        item.setCatId(16);
        System.out.println(item.getLocalisation());
        client.populateImages(item);
        client.addAnnonce(item);

    }
    public static void loadFile3() throws Exception {
        GrapperClient client = new GrapperClient();
        client.connexion();
        KerawaGrapper grapper = new KerawaGrapper();
        for (int i = 10; i >= 1; i--) {
            List<Item> items = grapper.getItemsList(client.getHttpPage(URL + "/" + i));
            AppacheurListTask itemtask = new AppacheurListTask(grapper,client,i);
            itemtask.init(items);
            itemtask.start();
        }
    }
    public static void loadFile() throws Exception {
        GrapperClient client = new GrapperClient();
        client.connexion();
        KerawaGrapper grapper = new KerawaGrapper();
        for (int i = 1; i >= 1; i--) {
            List<Item> items = grapper.getItemsList(client.getHttpPage(URL + "/" + i));
            for (Item item : items) {
                grapper.getItem(client.getHttpPage(item.getSrcLink()), item);

                if ((item.getImages() != null && item.getImages().size() > 0) || item.getCatId() == 19) {
                    client.populateImages(item);
                }
            }
//        System.out.println(items);
            for (Item item : items) {
                if ((item.getImages() != null && item.getImages().size() > 0) || item.getCatId() == 19) {
                    client.addAnnonce(item);
//                break;
                }
            }
        }

    }

    private static void loadImageDate(GrapperClient client, Item item) throws Exception {
        List<ItemImage> images = item.getImages();
        for (ItemImage image : images) {
            image.setData(client.getHttpsData(image.getSrc()));
        }
    }

    private static void testPut() throws Exception {
        GrapperClient client = new GrapperClient();
        KerawaGrapper grapper = new KerawaGrapper();
        byte[] data = client.getHttpData("http://annonces.appacheur.com//components/com_djclassifieds/images/item/51_img_0017_thb.jpg");
        String name = UUID.randomUUID().toString() + ".jpg";
        System.out.println(name);
        client.postHttpData("http://annonces.appacheur.com/index.php?option=com_djclassifieds&task=upload&tmpl=component", name, data);
    }

    public static void compress() throws IOException {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
        String imageName = "test3.jpg";
        File outfile = new File("test3_c.jpg");
        String type = "JPG";
        if (imageName.endsWith(".png")) {
            type = "PNG";
        }

        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(outfile));

        PlanarImage image = readPNGImage(imageName);

        RenderingHints qualityHints = new RenderingHints(new HashMap<RenderingHints.Key, Object>());

        float scaleX = 200f / image.getWidth(), scaleY = 150f / image.getHeight();
        float translationX = 0f, translationY = 0f;
        System.out.println(scaleX + "," + scaleY);
        RenderedOp scaleOp
                = ScaleDescriptor.create(image,
                        scaleX, scaleY,
                        translationX, translationY, Interpolation.getInstance(
                                Interpolation.INTERP_BILINEAR),
                        qualityHints);

        PlanarImage resizedImage = scaleOp.getRendering();
        ImageIO.write(resizedImage, type, bos);

    }

    public static PlanarImage readPNGImage(String imageName) throws IOException {
        File f = new File(imageName);
        RenderedOp op;
        if (imageName.endsWith(".png")) {
            op = PNGDescriptor.create(new FileSeekableStream(f), new PNGDecodeParam(), null);
        } else {
            op = JPEGDescriptor.create(new FileSeekableStream(f), null);
        }
        return op.getRendering();
    }

    public static void check(String src,String dst) throws IOException {
        ArrayList<Col> ls = new ArrayList<Col>();
        BufferedImage image = readPNGImage(src).getAsBufferedImage();
        BufferedImage image2 = readPNGImage(dst).getAsBufferedImage();
        String max = null;
        String min = null;
        double ma = 0;
        double mi = 100000;
        double avg = 0;
        int zero = 0;
        int sup = 0;
        double dt;
        Col dts;
        Col c1 = new Col();
        Col c2 = new Col();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                dts = c1.init(image.getRGB(i, j),i,j).diff(c2.init(image2.getRGB(i, j),i,j));
                dt =dts.diffValue();
                avg = dt + avg;
                if (dt > ma) {
                    ma = dt;
                    max = i + "," + j;
                    addMax(ls,dts);
                    ma=ls.get(0).diffValue();
                }
                if (dt < mi) {
                    mi = dt;
                    min = i + "," + j;
                }
                if (dt == 0) {
                    zero++;
                }
                if (dt > 20) {
                    sup++;
                }
            }
        }
        avg = avg / (image.getWidth() * image.getHeight());
        HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 0);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 0);
        map.put(6, 0);
        for (Col l : ls) {
            int key = l.proche();
            map.put(key, map.get(key)+1);
        }
        System.out.println(map);
    }

    private static void addMax(ArrayList<Col> ls, Col col) {
        if (ls.size() > 200) {
            ls.remove(0);
        }
        ls.add(col);
        Collections.sort(ls);
    }
    
    public static void fixImage(String image,int x,int y) throws IOException {
        BufferedImage monImage = ImageIO.read(FileUtils.getFile(image));
        BufferedImage monImage2 = ImageIO.read(Grabber.class.getResourceAsStream("logo4.jpg"));
        int db = monImage.getWidth() - 100;
        int end = 71;
        int dw = 10;
        int dh=20;
        int wt=0;
        int ht=0;
        if(x>=monImage.getWidth())
            wt=monImage.getWidth()-monImage2.getWidth()-dw;
        else if(x==0)
            wt=dw;
        else{
            wt = monImage.getWidth()/2 - (monImage2.getWidth()/2);
        }
        if(y==0)
            ht=dh;
        else{
            ht=monImage.getHeight()-monImage2.getHeight()-dh+15;
        }
        int t = 0;
        int p = 0;
        for (int i = wt; i < wt + monImage2.getWidth(); i++) {
            p = 0;
            for (int j = ht; j < ht+monImage2.getHeight(); j++) {
//                System.out.println(t+","+p);
                if ((t <= monImage2.getWidth() - 1) && (p <= monImage2.getHeight() - 1)) {
                    monImage.setRGB(i, j, monImage2.getRGB(t, p));
                } else {
                    monImage.setRGB(i, j, monImage2.getRGB(monImage2.getWidth() - 1, monImage2.getHeight() - 1));
                }
                p++;
            }
            t++;
        }

        String format = "JPG";
//        if (image.getSrc().toLowerCase().endsWith("png")) {
//            format = "PNG";
//        }
        FileOutputStream out = new FileOutputStream(image+"_fix.jpg");
        ImageIO.write(monImage, format, out);out.close();
    }

    public static  class Col implements Comparable<Col>{

        int r;
        int v;
        int b;
        int a;
        
        int x;
        int y;
        
        
        public Col(int rvba,int x,int y) {
            super();
            initCol(rvba,x,y);
        }

        private void initCol(int rvba,int x,int y) {
             r = (rvba) & 0xFF;
             v = (rvba >> 8) & 0xFF;
             b = (rvba >> 16) & 0xFF;
             a = (rvba >> 24) & 0xFF;
             this.x=x;
             this.y=y;
        }
        
        public Col init(int rvba,int x,int y){
            initCol(rvba,x,y);
            return this;
        }

        public Col(int r, int v, int b, int a) {
            this.r = r;
            this.v = v;
            this.b = b;
            this.a = a;
        }

        public Col() {
        }
        
        
        public Col diff(Col l){
            Col lt = new Col();
            lt.setR(Math.abs(getR()-l.getR()));
            lt.setV(Math.abs(getV()-l.getV()));
            lt.setB(Math.abs(getB()-l.getB()));
            lt.setA(Math.abs(getA()-l.getA()));
            lt.setX(x);
            lt.setY(y);
            return lt;
        }
        
        public double diffValue(){
            return (int)Math.sqrt((r*r+b*b+v*v+a*a));
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
        

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
        
        public double dist(int x1, int y1){
            return (int)Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
        }
        
        public int proche(){
            double d = dist(0, 0);
            int i=1;
            if(d>dist(99,0)){
                d=dist(99,0);
                i=2;
            }
            if(d>dist(199,0)){
                d=dist(199,0);
                i=3;
            } 
            if(d>dist(0,149)){
                d=dist(0,149);
                i=4;
            } 
            if(d>dist(99,149)){
                d=dist(99,149);
                i=5;
            } 
            if(d>dist(199,149)){
                i=6;
            }     
            return i;
        }

        @Override
        public int compareTo(Col o) {
            return (diffValue()>=o.diffValue())? 1:-1;
        }

        @Override
        public String toString() {
            return x+","+y+" - "+diffValue()+"  /"+proche();
        }
        
        

    }

}
