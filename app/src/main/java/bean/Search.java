package bean;

import java.util.ArrayList;

public class Search
{
    private String title;
    private String id;
    private ArrayList<CompanyListData> companyListData;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<CompanyListData> getCompanyListData() {
        return companyListData;
    }

    public void setCompanyListData(ArrayList<CompanyListData> companyListData) {
        this.companyListData = companyListData;
    }


}
