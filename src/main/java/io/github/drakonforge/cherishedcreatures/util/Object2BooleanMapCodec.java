package io.github.drakonforge.cherishedcreatures.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.WrappedCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.BooleanSchema;
import com.hypixel.hytale.codec.schema.config.IntegerSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

public class Object2BooleanMapCodec<T> implements Codec<Object2BooleanMap<T>>, WrappedCodec<T> {

    private final Codec<T> keyCodec;
    private final Supplier<Object2BooleanMap<T>> supplier;
    private final boolean unmodifiable;

    public Object2BooleanMapCodec(Codec<T> keyCodec, Supplier<Object2BooleanMap<T>> supplier,
            boolean unmodifiable) {
        this.keyCodec = keyCodec;
        this.supplier = supplier;
        this.unmodifiable = unmodifiable;
    }

    public Object2BooleanMapCodec(Codec<T> keyCodec, Supplier<Object2BooleanMap<T>> supplier) {
        this(keyCodec, supplier, true);
    }

    public Codec<T> getChildCodec() {
        return this.keyCodec;
    }

    public Object2BooleanMap<T> decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        BsonDocument bsonDocument = bsonValue.asDocument();
        Object2BooleanMap<T> map = this.supplier.get();

        for (Map.Entry<String, BsonValue> entry : bsonDocument.entrySet()) {
            T decodedKey = this.keyCodec.decode(new BsonString(entry.getKey()),
                    extraInfo);
            map.put(decodedKey, entry.getValue().asBoolean().getValue());
        }

        if (this.unmodifiable) {
            map = Object2BooleanMaps.unmodifiable(map);
        }

        return map;
    }

    @Nonnull
    public BsonValue encode(@Nonnull Object2BooleanMap<T> map, ExtraInfo extraInfo) {
        BsonDocument bsonDocument = new BsonDocument();
        ObjectIterator<T> iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            T key = iterator.next();
            String encodedKey = this.keyCodec.encode(key, extraInfo).asString().getValue();
            bsonDocument.put(encodedKey, new BsonBoolean(map.getBoolean(key)));
        }

        return bsonDocument;
    }

    public Object2BooleanMap<T> decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo)
            throws IOException {
        reader.expect('{');
        reader.consumeWhiteSpace();
        Object2BooleanMap<T> map = this.supplier.get();
        if (reader.tryConsume('}')) {
            if (this.unmodifiable) {
                map = Object2BooleanMaps.unmodifiable(map);
            }

            return map;
        } else {
            while (true) {
                T key = this.keyCodec.decodeJson(reader, extraInfo);
                reader.consumeWhiteSpace();
                reader.expect(':');
                reader.consumeWhiteSpace();
                map.put(key, reader.readBooleanValue());
                reader.consumeWhiteSpace();
                if (reader.tryConsumeOrExpect('}', ',')) {
                    if (this.unmodifiable) {
                        map = Object2BooleanMaps.unmodifiable(map);
                    }

                    return map;
                }

                reader.consumeWhiteSpace();
            }
        }
    }

    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        ObjectSchema s = new ObjectSchema();
        StringSchema key = (StringSchema) this.keyCodec.toSchema(context);
        String title = key.getTitle();
        if (title == null) {
            title = key.getHytale().getType();
        }

        s.setTitle("Map of " + title + " to boolean");
        s.setPropertyNames(key);
        s.setAdditionalProperties(new BooleanSchema());
        return s;
    }
}
