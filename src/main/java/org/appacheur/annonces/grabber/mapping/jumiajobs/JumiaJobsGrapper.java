/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appacheur.annonces.grabber.mapping.jumiajobs;

import org.appacheur.annonces.grabber.services.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.appacheur.annonces.grabber.entites.FieldValue;
import org.appacheur.annonces.grabber.entites.Item;
import org.appacheur.annonces.grabber.entites.ItemImage;
import org.appacheur.annonces.grabber.network.GrapperClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 *
 * @author Koufana Crepin Sosthene
 */
public class JumiaJobsGrapper extends AbstractGrapper {

    private final JumiaJobsMapping mappping = new JumiaJobsMapping();

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
        Elements titleList = doc.select("#job-search-content .content-wrapper .tab-content #professionals p.headline3 a");
        System.out.println(titleList.size() + " élements trouvés pour la page ");
        int i = 0;
        Iterator<Element> elts = titleList.iterator();
        for (Iterator<Element> iterator = elts; iterator.hasNext();) {
            try {
                Element nxt = iterator.next();
                Element next = nxt;
                Item item = new Item();
                if (next != null) {
                    if (next.text() != null) {
                        item.setTitle(next.text().trim());
                    }
                    item.setSrcLink(next.attr("href"));
//                item.setCatId(Integer.valueOf(mappping.getCategorie(nxt.select(".ctg a .categor u font").
//                        first().text())));
                    System.out.println(item.getTitle() + " - "+item.getSrcLink());
                    i++;
                    item.setId((long)i);
                    items.add(item);
                }
            } catch (Exception ex) {
                Logger.getLogger(GrapperClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);

            }
        }
    }

    public void loadFile(Item item, String page) {
        try {
            System.out.println("chargement item "+item.getId());
            String page2 = page.replaceAll("[\\s]+", "");
            Document doc = Jsoup.parse(page);
            Elements elts = doc.select(".row .jobs-details-section div dl dd");
            String ville = elts.get(1).text().trim();
            item.setLocalisation(mappping.getLocalisation(ville));
            Element desc = doc.select("#job-details-section").get(2);
            
            if (desc.select("p").first() != null) {
                item.setDescription(desc.select(".dl-horizontal").first().html());
            }
            item.setCatId(Integer.valueOf(mappping.getCategorie(elts.get(7).text().trim())));
            
            if (doc.select("#job-header .row h4 a").first() != null) {
                item.setName(getText(doc.select("#job-header .row h4 a").first()));
            }
            try {
                item.setDate(new SimpleDateFormat("dd MMMMM yyyy",Locale.ENGLISH).parse(getText(doc.select("#job-date strong").first())));
            } catch (Exception ex) {
                Logger.getLogger(JumiaJobsGrapper.class.getName()).log(Level.WARNING, ex.getMessage());
            }
            
            ListIterator<Element> it = doc.select("#job-header img.img").listIterator();
            List<ItemImage> images = new ArrayList<ItemImage>();
            for (ListIterator<Element> iterator = it; iterator.hasNext();) {
                Element next = iterator.next();
                String img = next.attr("src");
                ItemImage image = new ItemImage();
                image.setSrc(img.trim());
                images.add(image);

            }
            item.setImages(images);
            List<FieldValue> values = new ArrayList<FieldValue>();
            FieldValue value = new FieldValue();
            value.setField("contract_type");
            value.setValue(elts.get(5).text().trim());
            
            values.add(value);
                
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
