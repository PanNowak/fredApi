package fred.enumeration;

public enum SeriesEnum {
    EMPTY("---Please choose data series---", ""),
    GDP("Real Gross Domestic Product", "GDPC1"),
    UN("Unemployment Level", "UNEMPLOY"),
    CPI("Consumer Price Index", "CPIAUCSL"),
    PCE("Real Personal Consumption Expenditures", "PCECC96"),
    INF("Inflation", "FPCPITOTLZGUSA"),
    USDEUR("US / EURO exchange rate", "DEXUSEU"),
    TRS10("10-Year Treasury Constant Maturity Rate", "WGS10YR"),
    TRHELD("U.S. Treasury securities held by the Federal Reserve", "TREAST"),
    UNR("Civilian Unemployment Rate", "UNRATE"),
    POUND("GDP at Market Prices in the UK", "RGDPMPUKA");

    private String label;
    private String id;

    SeriesEnum(String label, String id) {
        this.label = label;
        this.id = id;
    }


    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SeriesEnum{" +
                "label='" + label + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
