package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterAdapter extends TypeAdapter<DateTimeFormatter> {

    @Override
    public void write(final JsonWriter jsonWriter, final DateTimeFormatter dateTimeFormatter) throws IOException {
        jsonWriter.value("yyyy-MM-dd HH:mm");
    }

    @Override
    public DateTimeFormatter read(final JsonReader jsonReader) throws IOException {
        return DateTimeFormatter.ofPattern(jsonReader.nextString());
    }
}
