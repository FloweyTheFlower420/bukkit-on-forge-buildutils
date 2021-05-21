package com.floweytf.mappinggen;

import com.floweytf.utils.mappings.MappingsFactory;
import com.floweytf.utils.mappings.Mappings;
import com.floweytf.utils.mappings.MappingType;
import com.floweytf.utils.streams.InputStreamUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.function.Function;

public class ParseJsonConfig {
    public static Mappings utils(JsonObject o, Function<String, InputStreamUtils> converter, MappingType type) {
        JsonElement e = o.get("sources");
        if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
            return MappingsFactory.readFromSources(type, converter.apply(e.getAsJsonPrimitive().getAsString()));

        JsonObject element = o;

        InputStreamUtils[] source = new InputStreamUtils[element.getAsJsonArray("sources").size()];
        int i = 0;
        for (JsonElement sources : element.getAsJsonArray("sources")) {
            source[i] = converter.apply(sources.getAsString());
            i++;
        }
        return MappingsFactory.readFromSources(type, source);
    }

    public static Mappings parse(String filename) throws IOException {
        JsonElement reader = InputStreamUtils.getStreamFile(filename).asJson(JsonElement.class);
        JsonArray processing = reader.getAsJsonArray();

        Mappings mappings = null;

        for (JsonElement e : processing) {
            JsonObject element = e.getAsJsonObject();
            MappingType type = null;
            switch (element.getAsJsonPrimitive("type").getAsString()) {
                case "csrg":
                    type = MappingType.CSRG;
                    break;
                case "srg":
                    type = MappingType.SRG;
                    break;
                default:
                    throw new IllegalArgumentException("Type can only be csrg, srg, tsrg or tsrg2");
            }

            Mappings m;

            switch (element.getAsJsonPrimitive("source-type").getAsString()) {
                case "file":
                     m = utils(element, ar -> { try { return InputStreamUtils.getStreamFile(ar);} catch (Exception a) { throw new IllegalArgumentException(); }}, type);
                    if(mappings == null)
                        mappings = m;
                    else
                        mappings = mappings.merge(m);
                    break;
                case "url":
                    m = utils(element, ar -> { try { return InputStreamUtils.getStreamURL(ar);} catch (Exception a) { throw new IllegalArgumentException(); }}, type);
                    if(mappings == null)
                        mappings = m;
                    else
                        mappings = mappings.merge(m);
                    break;
                case "jar":
                    m = utils(element, ar -> { try { return InputStreamUtils.getStreamClassPath(ar);} catch (Exception a) { throw new IllegalArgumentException(); }}, type);
                    if(mappings == null)
                        mappings = m;
                    else
                        mappings = mappings.merge(m);
                    break;
                default:
                    throw new IllegalArgumentException("source-type can only be url or file");
            }
        }

        return mappings;
    }
}
