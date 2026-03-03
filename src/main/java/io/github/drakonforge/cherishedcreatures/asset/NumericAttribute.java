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
            .append(new KeyedCodec<>("Average", Codec.FLOAT), (obj, value) -> obj.average = value, NumericAttribute::getAverage)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("StandardDeviation", Codec.FLOAT), (obj, value) -> obj.standardDeviation = value, NumericAttribute::getStandardDeviation)
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
    private float average = 0.5f;
    private float standardDeviation = 0.25f;
    private float roundToNearest = -1.0f;

    private NumericAttribute() {}

    public NumericAttribute(float min, float max, float average, float standardDeviation, float roundToNearest) {
        this.min = min;
        this.max = max;
        this.average = average;
        this.standardDeviation = standardDeviation;
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

    public float getAverage() {
        return average;
    }

    public float getStandardDeviation() {
        return standardDeviation;
    }

    public float getRoundToNearest() {
        return roundToNearest;
    }
}
