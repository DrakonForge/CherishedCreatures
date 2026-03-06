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

    private final TemplateProcessor data;
    private final HudBuilder hudBuilder;
    @Nullable
    private HyUIHud hudElement = null;

    public MountStatusMeter(String htmlPath) {
        data = new TemplateProcessor().setVariable(KEY_VALUE, 1.0f);
        hudBuilder = HudBuilder.detachedHud().enableRuntimeTemplateUpdates(true).withRefreshRate(5).loadHtml(htmlPath, data);
    }

    public void setValue(float value) {
        data.setVariable(KEY_VALUE, value);
    }

    public void addHud(PlayerRef ref) {
        LOGGER.atInfo().log("Adding HUD for " + ref.getUsername());
        hudElement = hudBuilder.show(ref);
        hide();
    }

    public void show() {
        if (hudElement != null) {
            hudElement.addUnsafe();
        }
    }

    public void hide() {
        if (hudElement != null) {
            hudElement.removeUnsafe();
        }
    }
}
