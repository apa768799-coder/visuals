package com.example.client;

import com.example.client.config.ClientConfig;
import com.example.client.util.HelperFT;
import com.example.client.visuals.PulseVisualsUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Рендеринг игрового HUD.
 */
public class HudOverlay {

    public static void render(DrawContext context, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (ClientConfig.enableVisuals) {
            // Target HUD при наведении на игрока
            if (mc.targetedEntity instanceof PlayerEntity target) {
                PulseVisualsUtil.drawTargetHud(context, target, 10, 10);
            }

            // Пульсация при низком HP
            float hpPercent = mc.player.getHealth() / mc.player.getMaxHealth();
            PulseVisualsUtil.drawLowHpPulse(context, hpPercent);

            // Комбо-счётчик
            PulseVisualsUtil.drawComboCounter(context);
        }

        // Подсказки
        HelperFT.getInstance().render(context);
    }
}
