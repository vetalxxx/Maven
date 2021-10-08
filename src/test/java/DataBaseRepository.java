import entity.Weather;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseRepository {
    private final String insertWeatherQuery = "insert into weather (city, local_date, temperature) values (?, ?, ?)";
    private final String getAllWeatherQuery = "select * from weather where city=?";
    private final String getLastWeatherQuery = "select * from (select * from weather where city=? order by local_date desc) top 1";
    private static final String DB_PATH = "jdbc:sqlite:weather.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean saveWeatherToDB(Weather weather) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement saveWeather = connection.prepareStatement(insertWeatherQuery);
            saveWeather.setString(1, weather.getCity());
            saveWeather.setString(2, weather.getLocalDate());
            saveWeather.setDouble(3, weather.getTemperature());
            return saveWeather.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new SQLException("Сохранение погоды в базу данных не выполнено!");
    }

    public void saveWeatherToDB(List<Weather> weatherList) {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement saveWeather = connection.prepareStatement(insertWeatherQuery);
            for (Weather weather : weatherList) {
                saveWeather.setString(1, weather.getCity());
                saveWeather.setString(2, weather.getLocalDate());
                saveWeather.setDouble(3, weather.getTemperature());
                saveWeather.addBatch();
            }
            saveWeather.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Weather getWeatherFromDB(String city) {
        Weather weather = null;
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement getWeather = connection.prepareStatement(getLastWeatherQuery);
            getWeather.setString(1, city);
            ResultSet resultSet = getWeather.executeQuery();
            if (resultSet.first()) {
                weather = new Weather(resultSet.getString("city"),
                        resultSet.getString("local_date"),
                        resultSet.getDouble("temperature"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weather;
    }

    public List<Weather> getWeatherFromDBList(String city) {
        List<Weather> weatherList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            PreparedStatement getWeather = connection.prepareStatement(getAllWeatherQuery);
            getWeather.setString(1, city);
            ResultSet resultSet = getWeather.executeQuery();
            while (resultSet.next()) {
                weatherList.add(new Weather(resultSet.getString("city"),
                        resultSet.getString("local_date"),
                        resultSet.getDouble("temperature")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weatherList;
    }
}

