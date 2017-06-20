/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.mapping.afribaba;

import org.appacheur.annonces.grabber.services.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.appacheur.annonces.grabber.entites.FieldValue;
import org.appacheur.annonces.grabber.entites.Item;
import org.appacheur.annonces.grabber.entites.ItemImage;
import org.appacheur.annonces.grabber.mapping.MappingLocal;
import org.appacheur.annonces.grabber.mapping.kerawa.KerawaMapping;
import org.appacheur.annonces.grabber.network.GrapperClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

/**
 *
 * @author probook
 */
public class AfribabaGrapper extends AbstractGrapper {

    private final MappingLocal mappping = new AfribabaMapping();

    @Override
    public List<Item> getItemsList(String page) {
        List<Item> items = new ArrayList<Item>();
        loadFile(items, page);
        return items;
    }

    @Override
    public Item getItem(String page, Item item) {
        if (item == null) {
            item = new Item();
        }
//        loadFile(item, page);
        return item;
    }

    private void loadFile(List<Item> items, String page) {
        Document doc = Jsoup.parse(page);
        Iterator<Element> elts = doc.select(".media_listing .media-body").iterator();
        for (Iterator<Element> iterator = elts; iterator.hasNext();) {
            Element nxt = iterator.next();
            Element next = nxt.select("h3 a").first();
            Item item = new Item();
            item.setTitle(next.text().trim());
            item.setSrcLink("http://" + next.attr("href"));
            item.setCatId(Integer.valueOf(mappping.getCategorie(getText(nxt.select(".ad_cat_sub a").
                    first()))));
            items.add(item);
        }
    }

    public void loadFile(Item item, String page) throws IOException {
        Document doc = Jsoup.parse(page);
        String ville = doc.select(".h1-title location strong").first().text();
        item.setLocalisation(mappping.getLocalisation(ville));
        Element desc = doc.select("div.content_display").first();
        if (desc.select(".top-buffer-5.bottom-buffer strong.white").first() != null) {
            String prix = desc.select(".top-buffer-5.bottom-buffer strong.white").first().text().
                    replace("FCFA", "").replace(" ", "").trim();
            item.setPrix(prix.replace(".", ""));
        }
        if (desc.select("div.overflow-hidden").first() != null) {
            item.setDescription(desc.select("div.overflow-hidden").first().html());
        }
        desc = doc.select(".h1-title h1 summary").first();
        if (desc != null) {
            item.setTitle(desc.text());
        }
        Pattern pattern = Pattern.compile("ajax.php?view=giii&adtype=(\\w+)&display_phones=1(\\S+)'function");
        Matcher matcher = pattern.matcher(page.replaceAll("[\\s]+", ""));
        String url = "";
        while (matcher.find()) {
            url = "ajax.php?view=giii&adtype="+matcher.group(1)+"&display_phones=1"+matcher.group(2);
        }
        String page2 = GrapperClient.postHttp(url, new HashMap<String, String>());
        System.out.println(page2);
        Document doc2 = Jsoup.parse(page2);
        Iterator<Element> phoneIterator = doc.select(".phone-ajax-modal .list-group li strong").iterator();
        for (Iterator<Element> iterator = phoneIterator; iterator.hasNext();) {
            Element next = iterator.next();
            if (item.getTelephone1() == null) {
                item.setTelephone1(getPhoneNumber(next));
            } else {
                item.setTelephone2(getPhoneNumber(next));
            }
        }
        if (doc.select(".h1-title .top-buffer-5  a strong.underline").first() != null) {
            item.setName(getText(doc.select(".h1-title .top-buffer-5  a strong.underline").first()));
        }
//        try {
//            item.setDate(new SimpleDateFormat("yyyy/MM/dd").parse(getText(doc.select("#type_dates .publish").first()).split(":")[1].trim()));
//        } catch (Exception ex) {
//            Logger.getLogger(AfribabaGrapper.class.getName()).log(Level.WARNING, ex.getMessage());
//        }
//        ListIterator<Element> it = doc.select("#sidebar #item_location li").listIterator();
//        for (ListIterator<Element> iterator = it; iterator.hasNext();) {
//            Element next = iterator.next();
//            if (next.text().contains("Adresse")) {
//                String adresse = next.select("strong").text();
//                item.setAdresse(adresse);
//            }
//        }
        ListIterator<Element> it = doc.select("#slider-thumbs .thumbs li a img.img-thumbnail").listIterator();
        List<ItemImage> images = new ArrayList<ItemImage>();
        for (ListIterator<Element> iterator = it; iterator.hasNext();) {
            Element next = iterator.next();
            String img = next.attr("src");
            ItemImage image = new ItemImage();
            image.setSrc("http:"+img.replace("thumbs/tn_", ""));
            images.add(image);

        }
        item.setImages(images);
        List<FieldValue> values = new ArrayList<FieldValue>();
        it = doc.select("#description #custom_fields .meta_list .meta").listIterator();
        for (ListIterator<Element> iterator = it; iterator.hasNext();) {
            FieldValue value = new FieldValue();
            Element next = iterator.next();
            String field = next.select("strong").text();
            String fieldvalue = getText(next);
            value.setField(mappping.getExtraField(field.replace(":", "").trim()));
            value.setValue(fieldvalue);
            values.add(value);
        }
        item.setExtraValue(values);
    }

    public static String getPhoneNumber(Element elt) {
        List<TextNode> nodes = elt.textNodes();
        for (TextNode node : nodes) {
            if (!node.text().trim().isEmpty()) {
                return node.text().trim();
            }
        }
        return null;
    }

    public static String getText(Element elt) {
        List<TextNode> nodes = elt.textNodes();
        for (TextNode node : nodes) {
            if (!node.text().trim().isEmpty()) {
                return node.text().trim();
            }
        }
        return null;
    }

    public static String find(String regex, String page) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(page);
        while (m.find()) {
            return m.group(1);
        }
        return null;

    }

}
