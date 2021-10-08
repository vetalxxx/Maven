import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Weather;
import forecast_classes.AccuWeatherForecast;
import forecast_classes.Forecast;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccuweatherModel implements WeatherModel {

    private static final String PROTOCOL = "https";
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECASTS = "forecasts";
    private static final String VERSION = "v1";
    private static final String DAILY = "daily";
    private static final String ONE_DAY = "1day";
    private static final String FIVE_DAYS = "5day";
    private static final String API_KEY = "JSZGVAXASu746vM1K2AAcEaeDtAzD6IK";
    private static final String API_KEY_QUERY_PROPERTY = "apikey";
    private static final String LANGUAGE = "ru-ru";
    private static final String LANGUAGE_QUERY_PROPERTY = "language";
    private static final String METRIC = "true";
    private static final String METRIC_QUERY_PROPERTY = "metric";
    private static final String LOCATIONS = "locations";
    private static final String CITIES = "cities";
    private static final String AUTOCOMPLETE = "autocomplete";

    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final DataBaseRepository dataBaseRepository = new DataBaseRepository();

    @Override
    public void getWeather(String city, Period period) throws IOException {
        Request request = null;
        switch (period) {
            case NOW:
                HttpUrl oneDayHttpUrl = new HttpUrl.Builder()
                        .scheme(PROTOCOL)
                        .host(BASE_HOST)
                        .addPathSegment(FORECASTS)
                        .addPathSegment(VERSION)
                        .addPathSegment(DAILY)
                        .addPathSegment(ONE_DAY)
                        .addPathSegment(detectCityKey(city))
                        .addQueryParameter(API_KEY_QUERY_PROPERTY, API_KEY)
                        .addQueryParameter(LANGUAGE_QUERY_PROPERTY, LANGUAGE)
                        .addQueryParameter(METRIC_QUERY_PROPERTY, METRIC)
                        .build();

                request = new Request.Builder()
                        .url(oneDayHttpUrl)
                        .build();
                break;
            case FIVE_DAYS:
                HttpUrl fiveDaysHttpUrl = new HttpUrl.Builder()
                        .scheme(PROTOCOL)
                        .host(BASE_HOST)
                        .addPathSegment(FORECASTS)
                        .addPathSegment(VERSION)
                        .addPathSegment(DAILY)
                        .addPathSegment(FIVE_DAYS)
                        .addPathSegment(detectCityKey(city))
                        .addQueryParameter(API_KEY_QUERY_PROPERTY, API_KEY)
                        .addQueryParameter(LANGUAGE_QUERY_PROPERTY, LANGUAGE)
                        .addQueryParameter(METRIC_QUERY_PROPERTY, METRIC)
                        .build();

                request = new Request.Builder()
                        .url(fiveDaysHttpUrl)
                        .build();
                break;
            case DB:
                List<Weather> weathers = getWeatherFromDBList(city);
                for (Weather weather : weathers) {
                    System.out.println(weather);
                }
                break;
        }

        if (request != null) {
            Response response = okHttpClient.newCall(request).execute();
            List<Weather> weatherList = new ArrayList<>();
            if (response.isSuccessful()) {
                String responseAsString = Objects.requireNonNull(response.body()).string();

                ObjectMapper mapper = new ObjectMapper();
                AccuWeatherForecast accuWeatherForecast = mapper.readValue(responseAsString, AccuWeatherForecast.class);

                String string = "Погода в " + city + ": ";
                string += accuWeatherForecast.getHeadLine().getText();
                System.out.println(string);

                System.out.println("О погоде: ");
                System.out.println("Дата\tМах t\tМин t\tОблачность");
                for (Forecast forecast : accuWeatherForecast.getDailyForecasts()) {
                    System.out.println(forecast.getDate().substring(0, 10) + "\t"
                            + forecast.getTemperature().getMaximum().getValue() + ""
                            + forecast.getTemperature().getMaximum().getUnit() + "\t"
                            + forecast.getTemperature().getMinimum().getValue() + ""
                            + forecast.getTemperature().getMinimum().getUnit() + "\t"
                            + forecast.getDay().getIconPhrase());
                }
                saveWeatherToDB(weatherList);
            }
        }
    }
    @Override
    public boolean saveWeatherToDB(Weather weather) throws SQLException {
        return dataBaseRepository.saveWeatherToDB(weather);
    }

    @Override
    public Weather getWeatherFromDB(String city) {
        return dataBaseRepository.getWeatherFromDB(city);
    }

    public void saveWeatherToDB(List<Weather> weatherList) {
        dataBaseRepository.saveWeatherToDB(weatherList);
    }

    public List<Weather> getWeatherFromDBList(String city) {
        return dataBaseRepository.getWeatherFromDBList(city);
    }

    private String detectCityKey(String city) throws IOException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS)
                .addPathSegment(VERSION)
                .addPathSegment(CITIES)
                .addPathSegment(AUTOCOMPLETE)
                .addQueryParameter(API_KEY_QUERY_PROPERTY, API_KEY)
                .addQueryParameter("q", city)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .addHeader("accept", "application/json")
                .build();

        Response locationResponse = okHttpClient.newCall(request).execute();
        String locationResponseString = Objects.requireNonNull(locationResponse.body()).string();

        return objectMapper.readTree(locationResponseString).get(0).at("/Key").asText();
    }
}