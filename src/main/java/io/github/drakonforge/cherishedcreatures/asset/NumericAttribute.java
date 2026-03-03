package io.github.drakonforge.cherishedcreatures.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;

public class NumericAttribute {
    public static final BuilderCodec<NumericAttribute> CODEC = BuilderCodec.builder(
                    NumericAttribute.class, NumericAttribute::new)
            .append(new KeyedCodec<>("Mode", new EnumCodec<>(Mode.class)), (obj, value) -> obj.mode = value, NumericAttribute::getMode)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Min", Codec.FLOAT), (obj, value) -> obj.min = value, NumericAttribute::getMin)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Max", Codec.FLOAT), (obj, value) -> obj.max = value, NumericAttribute::getMax)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("DefaultAvgValue", Codec.FLOAT), (obj, value) -> obj.defaultAvgValue = value, NumericAttribute::getDefaultAvgValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("DefaultStandardDeviation", Codec.FLOAT), (obj, value) -> obj.defaultStandardDeviation = value, NumericAttribute::getDefaultStandardDeviation)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("RoundToNearest", Codec.FLOAT), (obj, value) -> obj.roundToNearest = value, NumericAttribute::getRoundToNearest)
            .documentation("TODO")
            .add()
            .documentation("TODO")
            .build();

    public enum Mode {
        Disabled, Internal, Display
    }

    private Mode mode = Mode.Disabled;
    private float min = 0.0f;
    private float max = 1.0f;
    private float defaultAvgValue = 0.5f;
    private float defaultStandardDeviation = 0.25f;
    private float roundToNearest = -1.0f;

    private NumericAttribute() {}

    public NumericAttribute(float min, float max, float defaultAvgValue, float defaultStandardDeviation, float roundToNearest) {
        this.min = min;
        this.max = max;
        this.defaultAvgValue = defaultAvgValue;
        this.defaultStandardDeviation = defaultStandardDeviation;
        this.roundToNearest = roundToNearest;
    }

    public Mode getMode() {
        return mode;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float clamp(float value) {
        if (roundToNearest > 0) {
            value = roundToNearest * Math.round(value / roundToNearest);
        }
        return Math.clamp(value, min, max);
    }

    public float getDefaultAvgValue() {
        return defaultAvgValue;
    }

    public float getDefaultStandardDeviation() {
        return defaultStandardDeviation;
    }

    public float getRoundToNearest() {
        return roundToNearest;
    }
}
