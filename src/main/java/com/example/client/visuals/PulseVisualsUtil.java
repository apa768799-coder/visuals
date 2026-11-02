package com.example.client.visuals;

import com.example.client.ClientMod;
import com.example.client.config.ClientConfig;
import com.example.client.util.FpsHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита визуальных эффектов.
 */
public class PulseVisualsUtil {

    private static final List<Particle> particles = new ArrayList<>();
    private static int comboCount = 0;
    private static long lastComboTime = 0;

    public static void onTick() {
        particles.removeIf(p -> p.life <= 0);
        for (Particle p : particles) {
            p.update();
            p.life--;
        }

        if (System.currentTimeMillis() - lastComboTime > 3000) {
            comboCount = 0;
        }
    }

    public static void addCombo() {
        comboCount++;
        lastComboTime = System.currentTimeMillis();
    }

    // Частицы удара
    public static void drawHitParticles(double x, double y, boolean isCritical) {
        if (!ClientConfig.enableVisuals || !ClientConfig.enableHitParticles || FpsHelper.isLowFps()) return;

        int count = isCritical ? 15 : 8;
        int color = isCritical ? 0xFFFF0000 : 0xFFFFD700;

        for (int i = 0; i < count; i++) {
            if (particles.size() < 30) {
                particles.add(new Particle(x, y, (Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2, color));
            }
        }
    }

    // HUD цели
    public static void drawTargetHud(DrawContext context, PlayerEntity target, int x, int y) {
        if (!ClientConfig.enableTargetHud || target == null || FpsHelper.isLowFps()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player.distanceTo(target) > 64) return;

        context.fill(x, y, x + 150, y + 40, 0xAA000000);
        ClientMod.VERSION_SUPPORT.renderText(context, target.getName().getString(), x + 5, y + 5, 0xFFFF8C1A);

        float hp = ClientMod.VERSION_SUPPORT.getPlayerHealth(target);
        float maxHp = ClientMod.VERSION_SUPPORT.getPlayerMaxHealth(target);
        int barWidth = (int) (140 * Math.max(0, hp / maxHp));

        context.fill(x + 5, y + 20, x + 5 + barWidth, y + 25, 0xFFFF0000);
        ClientMod.VERSION_SUPPORT.renderText(context, String.format("%.1f / %.1f", hp, maxHp), x + 5, y + 28, 0xFFFFFF);
    }

    // Линия траектории
    public static void drawTrajectoryLine(DrawContext context, Vec3d start, Vec3d end) {
        if (!ClientConfig.enableTrajectory || FpsHelper.isLowFps()) return;

        for (double i = 0; i < 1.0; i += 0.1) {
            int px = (int) (start.x + (end.x - start.x) * i);
            int py = (int) (start.y + (end.y - start.y) * i + Math.sin(i * Math.PI) * 20);
            context.fill(px, py, px + 2, py + 2, 0xAAFFFFFF);
        }
    }

    // Пульсация при низком HP
    public static void drawLowHpPulse(DrawContext context, float hpPercent) {
        if (!ClientConfig.enableLowHpPulse || hpPercent > 0.2f || FpsHelper.isLowFps()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        int w = ClientMod.VERSION_SUPPORT.getScaledWidth(mc);
        int h = ClientMod.VERSION_SUPPORT.getScaledHeight(mc);

        double pulse = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 200.0);
        int alpha = (int) (100 * (1.0f - (hpPercent / 0.2f)) * pulse);
        int color = (Math.max(0, Math.min(255, alpha)) << 24) | 0xFF0000;

        context.fill(0, 0, w, 20, color);
        context.fill(0, h - 20, w, h, color);
    }

    // Комбо-счётчик
    public static void drawComboCounter(DrawContext context) {
        if (!ClientConfig.enableCombo || comboCount == 0) return;

        int color = comboCount >= 7 ? 0xFFFF0000 : (comboCount >= 4 ? 0xFFFF8C1A : 0xFFFFD700);
        float scale = 1.0f + (comboCount * 0.1f);

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        ClientMod.VERSION_SUPPORT.renderText(context, "COMBO x" + comboCount, 10, 10, color);
        context.getMatrices().pop();
    }

    // FPS индикатор
    public static void drawFpsIndicator(DrawContext context, int x, int y) {
        if (!ClientConfig.enableFpsIndicator) return;

        int fps = FpsHelper.getFps();
        int color = fps >= 60 ? 0xFF00FF00 : (fps >= 30 ? 0xFFFFFF00 : 0xFFFF0000);
        String text = fps + " FPS | " + FpsHelper.getUsedMemoryMb() + " MB";
        ClientMod.VERSION_SUPPORT.renderText(context, text, x, y, color);
    }

    // Параллакс фон
    public static void drawParallaxBackground(DrawContext context, int mouseX, int mouseY, int w, int h) {
        context.fill(0, 0, w, h, 0xFF100515);

        if (!ClientConfig.enableVisuals || !ClientConfig.enableParallax || FpsHelper.isLowFps()) return;

        int offsetX = (mouseX - w / 2) / 20;
        int offsetY = (mouseY - h / 2) / 20;
        context.fill(offsetX, offsetY, w + offsetX, h + offsetY, 0x208A2BE2);
    }

    // Шлейф частиц
    public static void renderParticleTrail(double x, double y, int count) {
        if (!ClientConfig.enableParticleTrail || FpsHelper.isLowFps()) return;

        for (int i = 0; i < count && particles.size() < 30; i++) {
            particles.add(new Particle(x, y, (Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5, 0xFFFF8C1A));
        }
    }

    private static class Particle {
        double x, y, vx, vy;
        int color;
        int life = 20;

        Particle(double x, double y, double vx, double vy, int color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.1;
        }
    }
}
