package forecast_classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Temperature {
    @JsonProperty("Value")
    private float value;
    @JsonProperty("Unit")
    private String unit;
    @JsonProperty("UnitType")
    private int unitType;

    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

}