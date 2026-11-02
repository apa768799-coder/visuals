package com.example.client.menu;

import com.example.client.ClientMod;
import com.example.client.config.ClientConfig;
import com.example.client.util.HelperFT;
import com.example.client.util.MusicPlayerUtil;
import com.example.client.visuals.PulseVisualsUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Кастомное главное меню.
 */
public class CustomMainMenuScreen extends Screen {

    public CustomMainMenuScreen() {
        super(Text.literal("PylseVisual Client"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Кнопка "Играть"
        addDrawableChild(ButtonWidget.builder(Text.literal("Играть"), b -> {
            System.out.println("[Client] Подключение к серверу...");
        }).dimensions(centerX - 100, startY, 200, 20).build());

        // Кнопка "Магазин"
        addDrawableChild(ButtonWidget.builder(Text.literal("Магазин"), b -> {
            System.out.println("[Client] Открытие магазина...");
        }).dimensions(centerX - 100, startY + 25, 200, 20).build());

        // Кнопка "Советы"
        addDrawableChild(ButtonWidget.builder(Text.literal("Советы"), b -> {
            HelperFT.getInstance().showNextTip();
        }).dimensions(centerX - 100, startY + 50, 200, 20).build());

        // Кнопка "Музыка"
        addDrawableChild(ButtonWidget.builder(Text.literal("Музыка"), b -> {
            MusicPlayerUtil.getInstance().playMusic();
        }).dimensions(centerX - 100, startY + 75, 200, 20).build());

        // Кнопка "Выход"
        addDrawableChild(ButtonWidget.builder(Text.literal("Выход"), b -> {
            client.scheduleStop();
        }).dimensions(centerX - 100, startY + 100, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Фон с параллаксом
        PulseVisualsUtil.drawParallaxBackground(context, mouseX, mouseY, this.width, this.height);

        // Заголовок с тенью
        String title = "PYLSEVISUAL";
        int titleX = this.width / 2;
        int titleY = this.height / 2 - 100;
        context.drawText(textRenderer, title, titleX - textRenderer.getWidth(title) / 2 + 1, titleY + 1, 0xFF8A2BE2, false);
        context.drawText(textRenderer, title, titleX - textRenderer.getWidth(title) / 2, titleY, 0xFFFF8C1A, false);

        // Стандартный рендер кнопок
        super.render(context, mouseX, mouseY, delta);

        // Виджет музыки
        if (ClientConfig.isMusicEnabled) {
            context.fill(10, this.height - 30, 150, this.height - 10, 0xAA000000);
            context.drawText(textRenderer, "♪ " + MusicPlayerUtil.getInstance().getCurrentTrackName(), 15, this.height - 25, 0xFFFFFF, true);
        }

        // FPS индикатор
        PulseVisualsUtil.drawFpsIndicator(context, 5, 5);

        // Подсказки
        HelperFT.getInstance().render(context);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
