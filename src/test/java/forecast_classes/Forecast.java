package forecast_classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Forecast {
    @JsonProperty("Date")
    private String date;
    @JsonProperty("EpochDate")
    private String epochDate;
    @JsonProperty("Temperature")
    private DayTemperature temperature;
    @JsonProperty("Day")
    private Precipitation day;
    @JsonProperty("Night")
    private Precipitation night;

    public String getDate() {
        return date;
    }


    public DayTemperature getTemperature() {
        return temperature;
    }

    public Precipitation getDay() {
        return day;
    }

}
