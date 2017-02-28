package bean;

import java.util.ArrayList;

public class CompanyList {

    private String page;
    private ArrayList<CompanyListData> companyListData;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public ArrayList<CompanyListData> getCompanyListData() {
        return companyListData;
    }

    public void setCompanyListData(ArrayList<CompanyListData> companyListData) {
        this.companyListData = companyListData;
    }

}
