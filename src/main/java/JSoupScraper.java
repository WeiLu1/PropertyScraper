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

    private static final String urlBase = "https://www.zoopla.co.uk/for-sale/property/";
    private static final String urlPageMidStr = "/?is_shared_ownership=false&page_size=25&q=";
    private static final String urlPageEndStr = "&radius=0&results_sort=newest_listings&search_source=facets&pn=";

    private static final String houseClass = "e2uk8e4 css-16zqmgg-StyledLink-Link-FullCardLink e33dvwd0";
    private static final String houseLinkDiv = "div.css-g014tg-ImageCenterWrapper.e2uk8e23";
    private static final String priceClass = "css-18tfumg-Text eczcs4p0";


    private String urlBuilder(String[] city){
        String countyCity = city[0];
        String cityOnly = city[1];

        return urlBase + countyCity + urlPageMidStr + cityOnly + urlPageEndStr;
    }

    private ArrayList<House> extractHouseInfo(String urlPage) throws IOException {

        ArrayList<House> houses = new ArrayList<>();
        int pageNum = 0;

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
                        houseObj.setPrice(price.text().substring(1));
                        houseObj.setUrl(urlBase + urlLink);
                        houseObj.setNumBeds(numBeds);
                    }
                    houses.add(houseObj);
                }
            }
        }

        return houses;

    }

    private void csvWriter(ArrayList<House> houseArray, String city) {

        final String path = city.toUpperCase() + "-Properties-2021" + ".csv";
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

        final String[][] cities = {

                {"london/croydon", "croydon"},
                {"east-sussex/brighton", "brighton"},
                {"birmingham", "birmingham"},
                {"bristol", "bristol"},
                {"devon/exeter", "exeter"},
                {"norwich", "norwich"},
                {"newcastle-upon-tyne", "Newcastle"},
                {"hull", "hull"},
                {"west-yorkshire/leeds", "leeds"}

        };

        JSoupScraper scraper = new JSoupScraper();

        for (String[] city : cities) {

            String url = scraper.urlBuilder(city);
            ArrayList<House> housesScraped = scraper.extractHouseInfo(url);

            for (House house: housesScraped){
                System.out.println(house.getHouse() + " " + house.getPrice() + " " + house.getUrl() + " " + house.getNumBeds());
            }

            scraper.csvWriter(housesScraped, city[1]);

        }
    }
}
