package eutros.botaniapp.client.core.proxy;

import eutros.botaniapp.client.core.handler.ClientTickHandler;
import eutros.botaniapp.client.core.handler.ColorHandler;
import eutros.botaniapp.common.core.proxy.IProxy;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

    @Override
    public void registerHandlers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    }

    @Override
    public long getWorldElapsedTicks() {
        return ClientTickHandler.ticksInGame;
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        ColorHandler.init();
    }
}
