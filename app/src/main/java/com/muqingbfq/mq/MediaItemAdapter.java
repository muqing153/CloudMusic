package com.muqingbfq.mq;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MediaItemAdapter implements JsonSerializer<MediaItem>, JsonDeserializer<MediaItem> {

    public static final Type type = new TypeToken<List<MediaItem>>() {
    }.getType();
    @Override
    public JsonElement serialize(MediaItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.mediaId);
        jsonObject.addProperty("artist", src.mediaMetadata.artist != null ? src.mediaMetadata.artist.toString() : "");
        jsonObject.addProperty("title", src.mediaMetadata.title != null ? src.mediaMetadata.title.toString() : "");
        jsonObject.addProperty("artworkUri", src.mediaMetadata.artworkUri != null ? src.mediaMetadata.artworkUri.toString() : "");
//        jsonObject.addProperty("like",);
        return jsonObject;
    }

    @Override
    public MediaItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : "";
        String artist = jsonObject.has("artist") ? jsonObject.get("artist").getAsString() : "";
        String title = jsonObject.has("title") ? jsonObject.get("title").getAsString() : "";
        String artworkUri = jsonObject.has("artworkUri") ? jsonObject.get("artworkUri").getAsString() : "";
        String uri = "";

        MediaMetadata metadata = new MediaMetadata.Builder()
                .setArtist(artist)
                .setTitle(title)
                .setArtworkUri(artworkUri.isEmpty() ? null : android.net.Uri.parse(artworkUri))
                .build();

        return new MediaItem.Builder()
                .setMediaId(id)
                .setUri(uri)
                .setMediaMetadata(metadata)
                .build();
    }
}
