import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class FixerCurrencyLoader implements CurrencyLoader {
    @Override
    public List<Currency> load() {
        try {
            return toList(loadJson());
        } catch (IOException e) {
            return emptyList();
        }
    }

    private List<Currency> toList(String json) {
        List<Currency> list = new ArrayList<>();
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        // Check if "currencies" object is present in the JSON
        if (jsonObject.has("currencies")) {
            JsonObject currenciesObject = jsonObject.getAsJsonObject("currencies");
            Map<String, JsonElement> currencies = new Gson().fromJson(currenciesObject, new TypeToken<Map<String, JsonElement>>(){}.getType());

            // Check if the "currencies" map is not null before accessing its keys
            if (currencies != null) {
                for (String symbol : currencies.keySet()) {
                    list.add(new Currency(symbol, currencies.get(symbol).getAsString()));
                }
            }
        }

        return list;
    }

    private String loadJson() throws IOException {
        URL url = new URL("http://data.fixer.io/api/symbols?access_key=" + FixerAPI.key);
        try (InputStream is = url.openStream()) {
            return new String(is.readAllBytes());
        }
    }
}
