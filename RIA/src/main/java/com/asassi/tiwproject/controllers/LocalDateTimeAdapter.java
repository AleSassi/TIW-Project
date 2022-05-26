package com.asassi.tiwproject.controllers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return null; // The server should only send, not receive data
    }

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(localDateTime));
    }
}
