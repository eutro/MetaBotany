/*
 * This was taken in part from its counterpart in the Botania mod,
 * found at https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/core/handler/ClientTickHandler.java
 */

package eutros.botaniapp.client.core.handler;

import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = Reference.MOD_ID)
public class ClientTickHandler {

    public static int ticksInGame = 0;

    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Screen gui = Minecraft.getInstance().currentScreen;
            if (gui == null || !gui.isPauseScreen()) {
                ++ticksInGame;
            }
        }
    }

    private ClientTickHandler() {}
}
