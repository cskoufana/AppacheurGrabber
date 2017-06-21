/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.network;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.appacheur.annonces.grabber.Grabber;
import org.appacheur.annonces.grabber.entites.FieldValue;
import org.appacheur.annonces.grabber.entites.Item;
import org.appacheur.annonces.grabber.entites.ItemImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author probook
 */
public class GrapperClient {

    private CloseableHttpClient httpclient;
    public static final String ADD_ANNONCE_URL = "http://www.appacheur.org/index.php";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    public String getHttpsPage(String url) throws Exception {
        String page = null;
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());

        if (httpclient == null) {
            httpclient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
        }

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        } finally {
            response.close();
        }
        return page;
    }

    public byte[] getHttpsData(String url) throws Exception {
        byte[] page = null;
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());

        if (httpclient == null) {
            httpclient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
        }

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toByteArray(entity);
        } finally {
            response.close();
        }
        return page;
    }

    public String getHttpPage(String url) throws IOException {
        String page = null;
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
        }
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toString(entity, Charset.forName("UTF-8"));
        } finally {
            response.close();
        }
        return page;
    }

    public byte[] getHttpData(String url) throws IOException {
        byte[] page = null;
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
        }
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toByteArray(entity);
        } finally {
            response.close();
        }
        return page;
    }

    public String postHttpData(String url, String dataUrl, byte[] data) throws IOException {
        String page = null;
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
        }
        HttpPost httpPost = new HttpPost(url);
//        ByteArrayEntity en = new ByteArrayEntity(data, ContentType.APPLICATION_OCTET_STREAM);
//        httpPost.addHeader("name", dataUrl);
//        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
//        postParameters.add(new BasicNameValuePair("name", dataUrl));

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file", data, ContentType.DEFAULT_BINARY, dataUrl);
        builder.addTextBody("name", dataUrl);
        HttpEntity en = builder.build();
        httpPost.setEntity(en);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toString(entity, Charset.forName("UTF-8"));
            System.out.println(page);
        } finally {
            response.close();
        }
        return page;
    }

    public String postHttpData(String url, Map<String, String> map) throws IOException {
        String page = null;
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
        }
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entrySet : map.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            postParameters.add(new BasicNameValuePair(key.trim(), value));

        }
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters, Charset.forName("UTF-8")));

        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toString(entity, Charset.forName("UTF-8"));
            System.out.println(page);
        } finally {
            response.close();
        }
        return page;
    }

    public String addAnnonce(Item item) {
        String page = null;
        try {
            uploadImages(item);
            System.out.println("image uploaded");
            HttpPost post = createPost(item);
            if (httpclient == null) {
                httpclient = HttpClients.createDefault();
            }
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage(),ex);
        } catch (IOException ex) {
            Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage(),ex);
        }
        return page;
    }

    public void uploadImages(Item item) {
        List<ItemImage> images = item.getImages();
        for (ItemImage image : images) {
            uploadImage(image);
        }
    }

    public  void populateImages(Item item) {
        List<ItemImage> images = item.getImages();
        List<ItemImage> imagesToremowe = new ArrayList<ItemImage>();
        for (ItemImage image : images) {
            if (!populateImage(image)) {
                imagesToremowe.add(image);
            }
        }
        for (ItemImage imagesToremowe1 : imagesToremowe) {
            images.remove(imagesToremowe1);
        }
    }

    private boolean populateImage(ItemImage image) {
        try {
            if (image.getSrc().startsWith("https")) {
                image.setData(getHttpsData(image.getSrc()));
            } else {
                image.setData(getHttpsData(image.getSrc()));
            }
            return fixImage(image);
        } catch (Exception ex) {
            Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return false;
    }

    private boolean fixImage(ItemImage image) throws IOException {
        BufferedImage monImage = ImageIO.read(new ByteArrayInputStream(image.getData()));
        if (monImage.getWidth() < 300 || monImage.getHeight() < 300) {
            return false;
        }
        BufferedImage monImage2 = ImageIO.read(Grabber.class.getResourceAsStream("logo3.jpg"));
        int db = monImage.getWidth() - 100;
        int end = 71;
        int t = 0;
        int p = 0;
        for (int i = db; i < db + 90; i++) {
            p = 0;
            for (int j = 15; j < 75; j++) {
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
        if (image.getSrc().toLowerCase().endsWith("png")) {
            format = "PNG";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(monImage, format, out);
        image.setData(out.toByteArray());
        return true;
    }

    private void uploadImage(ItemImage image) {
        try {
            String ext = "jpg";
            if (image.getSrc().endsWith("png")) {
                ext = "jpg";
            } else if (image.getSrc().endsWith("gif")) {
                ext = "gif";
            }
            String name = UUID.randomUUID().toString().replace("-", "_") + "." + ext;
            image.setName(name);
            postHttpData("http://www.appacheur.org/index.php?option=com_djclassifieds&task=upload&tmpl=component", name, image.getData());

        } catch (Exception ex) {
            Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

    }

    public HttpPost createPost(Item item) throws UnsupportedEncodingException {
        HttpPost httppost = new HttpPost(ADD_ANNONCE_URL);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("name", item.getTitle()));
        postParameters.add(new BasicNameValuePair("contact_name", item.getName()));
        postParameters.add(new BasicNameValuePair("cats[]", String.valueOf(item.getCatId())));
        postParameters.add(new BasicNameValuePair("type_id", "1"));
        postParameters.add(new BasicNameValuePair("regions[]", String.valueOf(item.getLocalisation())));
        postParameters.add(new BasicNameValuePair("address", item.getAdresse()));
        postParameters.add(new BasicNameValuePair("description", item.getDescription()));
        postParameters.add(new BasicNameValuePair("tlphone_1", item.getTelephone1()));
        postParameters.add(new BasicNameValuePair("tlphone_2", item.getTelephone2()));
        postParameters.add(new BasicNameValuePair("email", "auto@appaheur.com"));
        postParameters.add(new BasicNameValuePair("price", item.getPrix()));
        postParameters.add(new BasicNameValuePair("currency", "FCFA"));
        postParameters.add(new BasicNameValuePair("hashcode", hashcode(item.getTitle()+format.format(item.getDate().getTime()))));
        List<ItemImage> images = item.getImages();
        if (images != null) {
            for (ItemImage image : images) {
                System.out.println(image.getName().replace(".jpg", "").replace(".jpeg", "").replace(".png", "").replace(".gif", ""));
                postParameters.add(new BasicNameValuePair("img_id[]", "0"));
                postParameters.add(new BasicNameValuePair("img_image[]", image.getName() + ";" + image.getName()));
                postParameters.add(new BasicNameValuePair("img_caption[]", image.getName().replace(".jpg", "")
                        .replace(".jpeg", "")
                        .replace(".png", "")
                        .replace(".gif", "")));
            }
            postParameters.add(new BasicNameValuePair("uploader_count", "0"));
        }
        List<FieldValue> extras = item.getExtraValue();
        if (extras != null) {
            for (FieldValue extra : extras) {
                if (extra.getField() != null && !extra.getField().trim().isEmpty()) {
                    postParameters.add(new BasicNameValuePair(extra.getField(), extra.getValue()));
                }
            }

        }

        postParameters.add(new BasicNameValuePair("option", "com_djclassifieds"));
        postParameters.add(new BasicNameValuePair("id", "0"));
        postParameters.add(new BasicNameValuePair("token", null));
        postParameters.add(new BasicNameValuePair("view", "additem"));
        postParameters.add(new BasicNameValuePair("task", "save"));
        postParameters.add(new BasicNameValuePair("boxchecked", "0"));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters, Charset.forName("UTF-8")));
        return httppost;
    }

    public void connexion() throws Exception {
        String page = getHttpPage("http://www.appacheur.org/connexion.html");
//        System.out.println(page);
        Document doc = Jsoup.parse(page);
        Elements element = doc.select("form.form-validate.form-horizontal input");
        ListIterator<Element> it = element.listIterator();
        it.next();
        it.next();
        it.next();
        Element next = it.next();
        String returns = next.attr("value");
        next = it.next();
        String name = next.attr("name");
        System.out.println(name + " = " + returns);
        HttpPost httppost = new HttpPost("http://www.appacheur.org/connexion.html?task=user.login");
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("username", "admin"));
        postParameters.add(new BasicNameValuePair("password", "Crepin2010@"));
        postParameters.add(new BasicNameValuePair("return", returns));
        postParameters.add(new BasicNameValuePair(name, "1"));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        if (httpclient == null) {
            httpclient = HttpClients.createDefault();
        }
        CloseableHttpResponse response = httpclient.execute(httppost);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    public static String postHttp(String url, Map<String, String> map) throws IOException {
        String page = null;
        CloseableHttpClient httpclients = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entrySet : map.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            postParameters.add(new BasicNameValuePair(key.trim(), value));

        }
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters, Charset.forName("UTF-8")));

        CloseableHttpResponse response = httpclients.execute(httpPost);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            page = EntityUtils.toString(entity, Charset.forName("UTF-8"));
            System.out.println(page);
        } finally {
            response.close();
        }
        return page;
    }

    public static String hashcode(String str) {
        return str.replaceAll("[\\s_\\-.',;]+", "");
    }

}
