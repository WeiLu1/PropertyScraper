import java.io.Serializable;

public class House implements Serializable {

    private String house;
    private int price;
    private String url;
    private int numBeds;
    private String borough;

    public House(){}

    public String getHouse(){
        return house;
    }

    public int getPrice(){
        return price;
    }

    public String getUrl(){
        return url;
    }

    public int getNumBeds(){
        return numBeds;
    }

    public String getBorough(){
        return borough;
    }

    public void setHouse(String house){
        this.house = house;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setNumBeds(int numBeds){
        this.numBeds = numBeds;
    }

    public void setBorough(String borough){
        this.borough = borough;
    }

}
