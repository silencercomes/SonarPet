package net.techcable.sonarpet.utils;

import lombok.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import com.dsh105.commodus.StringUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//ToDo: Version Fix Here
@Getter
public enum NmsVersion {
    v1_6_R3,
    v1_7_R1,
    v1_7_R2,
    v1_7_R3,
    v1_7_R4,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1;

    public static final NmsVersion LATEST, EARLIEST;
    private ImmutableMap<String, Integer> metadata;
    private ImmutableMap<String, String> obfuscatedMethods;
    public int getMetadataId(String name) {
        if (metadata == null) loadData();
        Integer id = metadata.get(name);
        if (id == null) {
            throw new IllegalArgumentException("Metadata " + name + " is unknown for version " + this);
        }
        return id;
    }
    public String getObfuscatedMethod(String id) {
        if (obfuscatedMethods == null) loadData();
        String name = obfuscatedMethods.get(id);
        if (name == null) {
            throw new IllegalArgumentException("Obfuscated method " + id + " is unknown for " + this);
        }
        return name;
    }
    @SneakyThrows(IOException.class)
    private void loadData() {
        URL url = getClass().getResource("/versions/" + this + ".json");
        if (url == null) {
            throw new IllegalArgumentException("No version data for " + this);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charsets.UTF_8))) {
            JsonObject base = new JsonParser().parse(reader).getAsJsonObject();
            JsonObject metadataObject = base.getAsJsonObject("metadata");
            ImmutableMap.Builder<String, Integer> metadata = ImmutableMap.builder();
            for (Map.Entry<String, JsonElement> entry : metadataObject.entrySet()) {
                metadata.put(entry.getKey(), entry.getValue().getAsInt());
            }
            this.metadata = metadata.build();
            JsonObject obfuscatedMethodsObject = base.getAsJsonObject("obfuscated_methods");
            ImmutableMap.Builder<String, String> obfuscatedMethods = ImmutableMap.builder();
            for (Map.Entry<String, JsonElement> entry : obfuscatedMethodsObject.entrySet()) {
                obfuscatedMethods.put(entry.getKey(), entry.getValue().getAsString());
            }
            this.obfuscatedMethods = obfuscatedMethods.build();
        }
    }

    static {
        NmsVersion[] sorted = values();
        Arrays.sort(sorted, Comparator.comparing(Enum::name));
        LATEST = sorted[sorted.length - 1];
        EARLIEST = sorted[0];
    }

    public static NmsVersion getVersion(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private final int majorVersion = parseMajorVersion(name());
    private static int parseMajorVersion(String s) {
        return Integer.parseInt(s.split("_")[1]);
    }
}
