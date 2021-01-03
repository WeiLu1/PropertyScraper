import java.io.Serializable;

public class House implements Serializable {

    private String house;
    private String price;
    private String url;
    private String numBeds;

    public House(){}

    public String getHouse(){
        return house;
    }

    public String getPrice(){
        return price;
    }

    public String getUrl(){
        return url;
    }

    public String getNumBeds(){
        return numBeds;
    }

    public void setHouse(String house){
        this.house = house;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setNumBeds(String numBeds){
        this.numBeds = numBeds;
    }

}
