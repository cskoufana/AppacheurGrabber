/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class KerawaGrapper extends AbstractGrapper {

    private final KerawaMapping mappping = new KerawaMapping();

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
        loadFile(item, page);
        return item;
    }

    private void loadFile(List<Item> items, String page) {
//        System.out.print(page);
        Document doc = Jsoup.parse(page);
        Iterator<Element> elts = doc.select("#main .ad_list tr td.text").iterator();
        for (Iterator<Element> iterator = elts; iterator.hasNext();) {
            try {
                Element nxt = iterator.next();
                System.out.print(nxt);
                Element next = nxt.select("a").first();
                Item item = new Item();
                if (next != null) {
                    if (next.text() != null) {
                        item.setTitle(next.text().trim());
                    }
                    item.setSrcLink(next.attr("href"));
//                item.setCatId(Integer.valueOf(mappping.getCategorie(nxt.select(".ctg a .categor u font").
//                        first().text())));
                    items.add(item);
                }
            } catch (Exception ex) {
                Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);

            }
        }
    }

    public void loadFile(Item item, String page) {
        try {
            String page2 = page.replaceAll("[\\s]+", "");
            String ville = find("Lieu:<strong>(\\w+),Cameroun</strong>", page2);
            item.setLocalisation(mappping.getLocalisation(ville));
            Document doc = Jsoup.parse(page);
            Element desc = doc.select("div#description").first();
            if (doc.select("#description #user_menu .button.primary strong").first() != null) {
                String prix = doc.select("#description #user_menu .button.primary strong").first().text().
                        replace("CFA", "").replace("Prix", "").replace(":", "").trim();
                item.setPrix(prix.replace(".", ""));
            }
            if (desc.select("p").first() != null) {
                item.setDescription(desc.select("p").first().html());
            }
            desc = doc.select("div#item_head h1 center strong").first();
            if (desc != null) {
                item.setTitle(desc.text());
            }
            if (doc.select(".content.item .block.bread a").last() != null) {
                item.setCatId(Integer.valueOf(mappping.getCategorie(doc.select(".content.item .block.bread a").last().text())));
            }
            Iterator<Element> phoneIterator = doc.select("div#phone #custom_fields .meta").iterator();
            for (Iterator<Element> iterator = phoneIterator; iterator.hasNext();) {
                Element next = iterator.next();
                if (item.getTelephone1() == null) {
                    item.setTelephone1(getPhoneNumber(next));
                } else {
                    item.setTelephone2(getPhoneNumber(next));
                }
            }
            if (doc.select("#contact .name a").first() != null) {
                item.setName(getText(doc.select("#contact .name a").first()));
            }
            try {
                item.setDate(new SimpleDateFormat("yyyy/MM/dd").parse(getText(doc.select("#type_dates .publish").first()).split(":")[1].trim()));
            } catch (Exception ex) {
                Logger.getLogger(KerawaGrapper.class.getName()).log(Level.WARNING, ex.getMessage());
            }
            ListIterator<Element> it = doc.select("#sidebar #item_location li").listIterator();
            for (ListIterator<Element> iterator = it; iterator.hasNext();) {
                Element next = iterator.next();
                if (next.text().contains("Adresse")) {
                    String adresse = next.select("strong").text();
                    item.setAdresse(adresse);
                }
            }
            it = doc.select("#description #photos div img").listIterator();
            List<ItemImage> images = new ArrayList<ItemImage>();
            for (ListIterator<Element> iterator = it; iterator.hasNext();) {
                Element next = iterator.next();
                String img = next.attr("src");
                ItemImage image = new ItemImage();
                image.setSrc(img.replace("_thumbnail", ""));
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
        } catch (Exception ex) {
            Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

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
