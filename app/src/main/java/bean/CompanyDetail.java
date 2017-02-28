package bean;

import java.util.ArrayList;

public class CompanyDetail {

    private String title;
    private String company;
    private String stage_simplify;
    private String url;
    private String product_list;
    private String area;
    private String category;
    private String logo;
    private String pic;
    private String des;
    private String found_date;
    private ArrayList<Financing> financing;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStage_simplify() {
        return stage_simplify;
    }

    public void setStage_simplify(String stage_simplify) {
        this.stage_simplify = stage_simplify;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProduct_list() {
        return product_list;
    }

    public void setProduct_list(String product_list) {
        this.product_list = product_list;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFound_date() {
        return found_date;
    }

    public void setFound_date(String found_date) {
        this.found_date = found_date;
    }

    public ArrayList<Financing> getFinancing() {
        return financing;
    }

    public void setFinancing(ArrayList<Financing> financing) {
        this.financing = financing;
    }
}
