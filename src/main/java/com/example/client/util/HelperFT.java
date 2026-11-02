package com.example.client.util;

import com.example.client.ClientMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Система подсказок.
 */
public class HelperFT {

    private static final HelperFT INSTANCE = new HelperFT();
    public static HelperFT getInstance() { return INSTANCE; }

    private final List<Tip> tips = new ArrayList<>();
    private Tip currentTip = null;
    private int displayTicks = 0;
    private static final int MAX_TICKS = 20 * 15; // 15 секунд

    public void loadTips() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("helper_tips.json");
        try {
            if (Files.exists(path)) {
                String json = Files.readString(path);
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement el : array) {
                    tips.add(new Tip(
                            el.getAsJsonObject().get("text").getAsString(),
                            el.getAsJsonObject().get("category").getAsString()
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[HelperFT] Ошибка загрузки: " + e.getMessage());
        }
    }

    public void addTip(String text, String category) {
        tips.add(new Tip(text, category));
    }

    public void showNextTip() {
        if (tips.isEmpty()) return;
        currentTip = tips.get((int) (Math.random() * tips.size()));
        displayTicks = 0;
    }

    public void onTick() {
        if (currentTip != null) {
            displayTicks++;
            if (displayTicks > MAX_TICKS) {
                currentTip = null;
            }
        }
    }

    public void render(DrawContext context) {
        if (currentTip == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        int w = ClientMod.VERSION_SUPPORT.getScaledWidth(mc);
        int x = w / 2 - 100;
        int y = 10;

        context.fill(x, y, x + 200, y + 40, 0xAA000000);
        ClientMod.VERSION_SUPPORT.renderText(context, "Совет (" + currentTip.category + "):", x + 5, y + 5, 0xFF8A2BE2);
        ClientMod.VERSION_SUPPORT.renderText(context, currentTip.text, x + 5, y + 20, 0xFFFFFF);
    }

    private static class Tip {
        String text;
        String category;

        Tip(String text, String category) {
            this.text = text;
            this.category = category;
        }
    }
}
