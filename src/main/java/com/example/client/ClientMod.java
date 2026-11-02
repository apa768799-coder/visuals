package com.example.client;

import com.example.client.config.ClientConfig;
import com.example.client.menu.CustomMainMenuScreen;
import com.example.client.model.CustomModelManager;
import com.example.client.util.HelperFT;
import com.example.client.util.MusicPlayerUtil;
import com.example.client.version.IVersionSupport;
import com.example.client.version.VersionSupport_1_20;
import com.example.client.version.VersionSupport_1_21;
import com.example.client.visuals.PulseVisualsUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;

/**
 * Главный класс клиента.
 */
public class ClientMod implements ClientModInitializer {

    public static IVersionSupport VERSION_SUPPORT;

    @Override
    public void onInitializeClient() {
        // Определяем версию игры и выбираем адаптер
        String mcVersion = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .get()
                .getMetadata()
                .getVersion()
                .getFriendlyString();

        if (mcVersion.startsWith("1.21")) {
            VERSION_SUPPORT = new VersionSupport_1_21();
        } else {
            VERSION_SUPPORT = new VersionSupport_1_20();
        }

        // Инициализация систем
        CustomModelManager.getInstance().reload();
        HelperFT.getInstance().loadTips();
        MusicPlayerUtil.getInstance().loadMusic("assets/music/lobby_theme.ogg");

        // Замена стандартного меню
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen && ClientConfig.replaceMainMenu) {
                client.execute(() -> client.setScreen(new CustomMainMenuScreen()));
            }
        });

        // HUD рендер
        HudRenderCallback.EVENT.register((context, tickDelta) -> HudOverlay.render(context, tickDelta));

        // Тик для обновления визуалов
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PulseVisualsUtil.onTick();
            HelperFT.getInstance().onTick();
        });
    }
}
