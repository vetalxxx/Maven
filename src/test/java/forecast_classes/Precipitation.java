package forecast_classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Precipitation {
    @JsonProperty("Icon")
    private int icon;
    @JsonProperty("IconPhrase")
    private String iconPhrase;
    @JsonProperty("HasPrecipitation")
    private boolean hasPrecipitation;



    public String getIconPhrase() {
        return iconPhrase;
    }

}