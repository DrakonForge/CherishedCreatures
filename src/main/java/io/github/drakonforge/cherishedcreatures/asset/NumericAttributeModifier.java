package io.github.drakonforge.cherishedcreatures.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class NumericAttributeModifier {
    public static final BuilderCodec<NumericAttributeModifier> CODEC = BuilderCodec.builder(
                    NumericAttributeModifier.class, NumericAttributeModifier::new)
            .append(new KeyedCodec<>("Min", Codec.FLOAT), (obj, value) -> obj.min = value, NumericAttributeModifier::getMin)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Max", Codec.FLOAT), (obj, value) -> obj.max = value, NumericAttributeModifier::getMax)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("DefaultAvgValue", Codec.FLOAT), (obj, value) -> obj.defaultAvgValue = value, NumericAttributeModifier::getDefaultAvgValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("DefaultStandardDeviation", Codec.FLOAT), (obj, value) -> obj.defaultStandardDeviation = value, NumericAttributeModifier::getDefaultStandardDeviation)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("RoundToNearest", Codec.FLOAT), (obj, value) -> obj.roundToNearest = value, NumericAttributeModifier::getRoundToNearest)
            .documentation("TODO")
            .add()
            .documentation("TODO")
            .build();

    float min = 0.0f;
    float max = 1.0f;
    float defaultAvgValue = 0.5f;
    float defaultStandardDeviation = 0.25f;
    float roundToNearest = -1.0f;

    private NumericAttributeModifier() {}

    public NumericAttributeModifier(float min, float max, float defaultAvgValue, float defaultStandardDeviation, float roundToNearest) {
        this.min = min;
        this.max = max;
        this.defaultAvgValue = defaultAvgValue;
        this.defaultStandardDeviation = defaultStandardDeviation;
        this.roundToNearest = roundToNearest;
    }


    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
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
