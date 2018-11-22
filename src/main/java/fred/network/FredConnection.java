package fred.network;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import fred.data.Header;
import fred.data.Observation;
import fred.data.RecessionData;
import fred.data.Series;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class that downloads data from Fed website
 * and allows to get it as a Series object.
 */
public class FredConnection {
    /**
     * Gson object used to deserialize data from Fed website.
     */
    private static final Gson G = createGson();

    /**
     * Api key required to download data using Fred API.
     */
    private static final String API_KEY = getApiKey();

    /**
     * RecessionData object used to get data about all American recessions.
     */
    public static final RecessionData RECESSION_DATA = getRecessionData();

    /**
     * Returns Series object that contains all data downloaded from Fed.
     * @param id id of the series to be downloaded (i.e. GPD)
     * @return new Series
     * @throws IOException if operation fails
     */
    public static Series getSeries(String id) throws IOException {
        Header header = getHeader(id);
        List<Observation> observationList = getObservationList(id);

        return new Series(header, observationList);
    }

    /**
     * Returns Header object that was created by deserialization
     * of data from Fed website.
     * @param id id of the series to be downloaded (i.e. GPD)
     * @return new Header
     * @throws IOException if operation fails
     */
    private static Header getHeader(String id) throws IOException {
        String spec = "https://api.stlouisfed.org/fred/series?series_id=" +
                id + "&api_key=" + FredConnection.API_KEY + "&file_type=json";

        JsonObject jsonHeader = downloadData(spec, "seriess")
                .get(0).getAsJsonObject();
        return G.fromJson(jsonHeader, Header.class);
    }

    /**
     * Returns list of Observation objects that were created by deserialization
     * of data from Fed website.
     * @param id id of the series to be downloaded (i.e. GPD)
     * @return list of observations
     * @throws IOException if operation fails
     */
    private static List<Observation> getObservationList(String id) throws IOException {
        String spec = "https://api.stlouisfed.org/fred/series/" +
                "observations?series_id=" + id +
                "&api_key=" + FredConnection.API_KEY + "&file_type=json";

        List<Observation> observationList = new ArrayList<>();
        JsonArray jsonObs = downloadData(spec, "observations");

        for (JsonElement element : jsonObs)
            addDataToObservationList(observationList, element);

        return observationList;
    }

    private static void addDataToObservationList(List<Observation> observationList,
                                          JsonElement dataSourceElement) {
        if (dataSourceElement.isJsonObject()) {
            JsonObject object = dataSourceElement.getAsJsonObject();

            LocalDate date = G.fromJson(object.get("date"), LocalDate.class);
            BigDecimal value = G.fromJson(object.get("value"), BigDecimal.class);

            if (value != null) observationList.add(new Observation(date, value));
        }
    }

    /**
     * Downloads data from the specified location and returns it as JsonArray.
     * @param spec string representation of the URL data is downloaded from
     * @param memberName name of the member that is being requested
     * @return JsonArray
     * @throws IOException if downloading fails
     */
    private static JsonArray downloadData(String spec, String memberName) throws IOException {
        URLConnection connection = new URL(spec).openConnection();

        try (Scanner in = new Scanner(connection.getInputStream())) {
            return streamToJsonArray(in, memberName);
        }
    }

    private static JsonArray streamToJsonArray(Scanner input, String memberName) {
        StringBuilder builder = new StringBuilder();
        while (input.hasNextLine())
            builder.append(input.nextLine());

        String stringToParse = builder.toString();
        return new JsonParser().parse(stringToParse).getAsJsonObject()
                .get(memberName).getAsJsonArray();
    }

    /**
     * Creates new Gson object.
     * @return Gson object
     */
    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
                .serializeSpecialFloatingPointValues().create();
    }

    /**
     * Returns api key used in connection.
     * @return api key
     */
    private static String getApiKey() {
        String apiKey;
        InputStream stream = FredConnection.class.getResourceAsStream("/Key.txt");
        try (Scanner fileScanner = new Scanner(stream, "UTF-8")) {
            apiKey = fileScanner.nextLine();
        }

        return apiKey;
    }

    /**
     * Returns RecessionData object that contains data about all US recessions.
     * @return RecessionData object
     */
    private static RecessionData getRecessionData() {
        try {
            Header recessionHeader = getHeader("USREC");
            List<Observation> recessionObservations =
                    getObservationList("USREC");

            List<Series> recessionList = new ArrayList<>();
            List<Observation> observationList = new ArrayList<>();

            boolean isPreviousDateARecession = false;
            for (int i = 1; i < recessionObservations.size(); i++) {
                Observation obs = recessionObservations.get(i);
                BigDecimal value = obs.getValue();

                if (value.compareTo(BigDecimal.ONE) == 0) {
                    if (!isPreviousDateARecession) {
                        observationList = new ArrayList<>();
                        observationList.add(recessionObservations.get(i - 1));

                        isPreviousDateARecession = true;
                    }
                    observationList.add(obs);
                }
                else if (value.compareTo(BigDecimal.ZERO) == 0 && isPreviousDateARecession) {
                    recessionList.add(new Series(recessionHeader, observationList));
                    isPreviousDateARecession = false;
                }
            }

            return new RecessionData(recessionList);
        } catch (IOException e) {
            e.printStackTrace();

            int answer = JOptionPane.showConfirmDialog(null,
                     "Recession data downloading failed. Do you want to try to download it again?",
                     "Network error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            if (answer == 0) return getRecessionData();
        }

        return null;
    }
}

/**
 * Adapter that parses and formats LocalDate objects.
 */
class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        String formattedLocalDate = DateTimeFormatter.ISO_LOCAL_DATE.format(value);
        out.value(formattedLocalDate);
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return LocalDate.parse(in.nextString());
    }
}

/**
 * Adapter that parses and formats BigDecimals. It additionally deals with
 * Fed data problems.
 */
class BigDecimalAdapter extends TypeAdapter<BigDecimal> {
    @Override
    public void write(JsonWriter out, BigDecimal value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(value);
    }

    @Override
    public BigDecimal read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String number = in.nextString();
        if (number.equals(".")) return null;

        return new BigDecimal(number);
    }
}
