import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JSoupScraper {

    private static final String urlBase = "www.zoopla.co.uk";
    private static final String urlPage = "https://www.zoopla.co.uk/for-sale/property/london/croydon/?beds_min=2&page_size=25&price_max=400000&q=croydon&radius=0&results_sort=newest_listings&pn=";
    private static final String houseClass = "e2uk8e4 css-16zqmgg-StyledLink-Link-FullCardLink e33dvwd0";
    private static final String houseLinkDiv = "div.css-g014tg-ImageCenterWrapper.e2uk8e23";
    private static final String priceClass = "css-18tfumg-Text eczcs4p0";


    public ArrayList<House> extractHouseInfo() throws IOException {

        ArrayList<House> houses = new ArrayList<>();
        int pageNum = 0;

        House houseStart = new House();
        houseStart.setHouse("description");
        houseStart.setPrice("price");
        houseStart.setUrl("url");
        houseStart.setNumBeds("number of beds");
        houses.add(houseStart);

        while(true) {
            pageNum += 1;

            Document doc = Jsoup.connect(urlPage + pageNum).get();

            Elements houseElements = doc.getElementsByClass(houseClass);
            Elements priceElements = doc.getElementsByClass(priceClass);
            Elements houseLinkElements = doc.select(houseLinkDiv + " > a");

            if (houseElements.text().equals("")){
                break;
            } else {
                int amountHouses = doc.getElementsByClass(houseClass).size();

                for (int i = 0; i < amountHouses; i++) {

                    House houseObj = new House();

                    Element house = houseElements.get(i);
                    String numBeds = houseElements.get(i).text().substring(0, 1);
                    Element price = priceElements.get(i);
                    String urlLink = houseLinkElements.get(i).attr("href");
                    if (house != null && price != null) {
                        houseObj.setHouse(house.text());
                        houseObj.setPrice(price.text());
                        houseObj.setUrl(urlBase + urlLink);
                        houseObj.setNumBeds(numBeds);
                    }
                    houses.add(houseObj);
                }
            }
        }

        return houses;

    }

    public void csvWriter(ArrayList<House> houseArray) {

        final String path = "Houses.csv";
        String[] columns = new String[] {"house", "price", "url", "numBeds"};

        try {
            FileWriter writer = new FileWriter(path);

            final ColumnPositionMappingStrategy<House> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(House.class);
            mappingStrategy.setColumnMapping(columns);

            final StatefulBeanToCsv<House> beanWriter = new StatefulBeanToCsvBuilder<House>(writer)
                    .withMappingStrategy(mappingStrategy)
                    .build();;
            beanWriter.write(houseArray);
            writer.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {

        JSoupScraper scraper = new JSoupScraper();
        ArrayList<House> housesScraped = scraper.extractHouseInfo();

        for (House house: housesScraped){
            System.out.println(house.getHouse() + " " + house.getPrice() + " " + house.getUrl() + " " + house.getNumBeds());
        }

        scraper.csvWriter(housesScraped);
    }
}
