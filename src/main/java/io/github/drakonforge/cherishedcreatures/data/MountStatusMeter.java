package io.github.drakonforge.cherishedcreatures.data;

import au.ellie.hyui.builders.HudBuilder;
import au.ellie.hyui.builders.HyUIHud;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nullable;

public class MountStatusMeter {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String KEY_VALUE = "value";
    private static final String KEY_MAX_VALUE = "maxValue";

    private final TemplateProcessor data;
    private final HudBuilder hudBuilder;
    @Nullable
    private HyUIHud hudElement = null;

    public MountStatusMeter(float initialMaxValue, String html) {
        data = new TemplateProcessor().setVariable(KEY_VALUE, initialMaxValue).setVariable(KEY_MAX_VALUE, initialMaxValue);
        hudBuilder = HudBuilder.detachedHud().fromTemplate(html, data);
    }

    public void setValue(float value) {
        data.setVariable(KEY_VALUE, value);
    }

    public void setMaxValue(float maxValue) {
        data.setVariable(KEY_MAX_VALUE, maxValue);
    }

    public void addHud(PlayerRef ref) {
        LOGGER.atInfo().log("Adding HUD for " + ref.getUsername());
        hudElement = hudBuilder.show(ref);
    }

    @Nullable
    public HyUIHud getHudElement() {
        return hudElement;
    }
}
