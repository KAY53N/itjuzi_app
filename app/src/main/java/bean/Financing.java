package bean;

public class Financing
{
    public String getFinancing_date() {
        return financing_date;
    }

    public void setFinancing_date(String financing_date) {
        this.financing_date = financing_date;
    }

    public String getFinancing_rank() {
        return financing_rank;
    }

    public void setFinancing_rank(String financing_rank) {
        this.financing_rank = financing_rank;
    }

    public String getFinancing_money() {
        return financing_money;
    }

    public void setFinancing_money(String financing_money) {
        this.financing_money = financing_money;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    private String financing_date;
    private String financing_rank;
    private String financing_money;
    private String organization;
}
