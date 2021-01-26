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
    private static final String urlPageMidStr = "/?beds_max=9&is_shared_ownership=false&page_size=25&q=";
    private static final String urlPageEndStr = "&radius=0&results_sort=newest_listings&search_source=facets&pn=";

    private static final String houseClass = "e2uk8e4 css-16zqmgg-StyledLink-Link-FullCardLink e33dvwd0";
    private static final String houseLinkDiv = "div.css-g014tg-ImageCenterWrapper.e2uk8e23";
    private static final String priceClass = "css-18tfumg-Text eczcs4p0";


    private String urlBuilder(String[] city){
        String countyCity = city[0];
        String cityOnly = city[1];

        return urlBase + countyCity + urlPageMidStr + cityOnly + urlPageEndStr;
    }

    private ArrayList<House> extractHouseInfo(String urlPage, String borough) throws IOException {

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
                    int numBeds;
                    Element house = houseElements.get(i);

                    if (houseElements.get(i).text().startsWith("Studio")){
                        numBeds = 1;
                    } else {
                        try {
                            numBeds = Integer.parseInt(houseElements.get(i).text().substring(0, 1));
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                    Element priceRaw = priceElements.get(i);
                    String urlLink = houseLinkElements.get(i).attr("href");
                    if (house != null && priceRaw != null && !priceRaw.text().equals("POA") && !priceRaw.text().equals("Sale by tender")) {
//                        houseObj.setHouse(house.text());
                        houseObj.setPrice(Integer.parseInt(priceRaw.text().replace(",", "").substring(1)));
                        houseObj.setUrl("https://www.zoopla.co.uk" + urlLink);
                        houseObj.setNumBeds(numBeds);
                        houseObj.setBorough(borough);
                        houses.add(houseObj);
                    }
                }
            }
        }

        return houses;

    }

    private void csvWriter(ArrayList<House> houseArray, String borough) {

        final String path = borough.toUpperCase() + "-Properties-2021" + ".csv";
        String[] columns = new String[] {"borough", "numBeds", "price", "url"};

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

        final String[][] boroughs = {

//                {"newham-london-borough", "newham-london-borough", "Newham"},
//                {"harrow-london-borough", "harrow-london-borough", "Harrow"},
//                {"sutton-london-borough", "sutton-london-borough", "Sutton"},
//                {"croydon-london-borough", "croydon-london-borough", "Croydon"},
//                {"southwark-london-borough", "southwark-london-borough", "Southwark"},
//                {"greenwich-royal-borough", "greenwich-royal-borough", "Greenwich"},
//                {"kingston-upon-thames-royal-borough", "kingston-upon-thames-royal-borough", "Kingston upon Thames"},
//                {"waltham-forest-london-borough", "waltham-forest-london-borough", "Waltham Forest"},
                {"kensington-and-chelsea-royal-borough", "kensington-and-chelsea-royal-borough", "Kensington and Chelsea"},
                {"ealing-london-borough", "ealing-london-borough", "Ealing"},
                {"bromley-london-borough", "bromley-london-borough", "Bromley"},
                {"hounslow-london-borough", "hounslow-london-borough", "Hounslow"},
                {"camden-london-borough", "camden-london-borough", "Camden"},
                {"merton-london-borough", "merton-london-borough", "Merton"},
                {"lambeth-london-borough", "lambeth-london-borough", "Lambeth"},
                {"hillingdon-london-borough", "hillingdon-london-borough", "Hillingdon"},
                {"richmond-upon-thames-london-borough", "richmond-upon-thames-london-borough", "Richmond upon Thames"},
                {"tower-hamlets-london-borough", "tower-hamlets-london-borough", "Tower Hamlets"},
                {"hammersmith-and-fulham-london-borough", "hammersmith-and-fulham-london-borough", "Hammersmith and Fulham"},
                {"lewisham-london-borough", "lewisham-london-borough", "Lewisham"},
                {"bexley-london-borough", "bexley-london-borough", "Bexley"},
                {"westminster-london-borough", "westminster-london-borough", "Westminster"},
                {"havering-london-borough", "havering-london-borough", "Havering"},
                {"redbridge-london-borough", "redbridge-london-borough", "Redbridge"},
                {"barnet-london-borough", "barnet-london-borough", "Barnet"},
                {"hackney-london-borough", "hackney-london-borough", "Hackney"},
                {"islington-london-borough", "islington-london-borough", "Islington"},
                {"city-of-london-london-borough", "city-of-london-london-borough", "City of London"},
                {"barking-and-dagenham-london-borough", "barking-and-dagenham-london-borough", "Barking and Dagenham"},
                {"brent-london-borough", "brent-london-borough", "Brent"},
                {"enfield-london-borough", "enfield-london-borough", "Enfield"},
                {"haringey-london-borough", "haringey-london-borough", "Haringey"},
                {"wandsworth-london-borough", "wandsworth-london-borough", "Wandsworth"},

        };

        JSoupScraper scraper = new JSoupScraper();

        for (String[] borough : boroughs) {

            System.out.println(borough[1]);

            String url = scraper.urlBuilder(borough);
            ArrayList<House> housesScraped = scraper.extractHouseInfo(url, borough[2]);

            for (House house: housesScraped){
                System.out.println(house.getPrice() + ", " + house.getUrl() + ", " + house.getNumBeds() + ", " + house.getBorough());
            }

            scraper.csvWriter(housesScraped, borough[2]);

        }
    }
}
