package forecast_classes;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AccuWeatherForecast {
    @JsonProperty("Headline")
    private HeadLine headLine;
    @JsonProperty("DailyForecasts")
    private List<Forecast> dailyForecasts;

    public HeadLine getHeadLine() {
        return headLine;
    }

    public List<Forecast> getDailyForecasts() {
        return dailyForecasts;
    }

}