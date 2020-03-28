package eutros.botaniapp.client.core.proxy;

import eutros.botaniapp.client.core.handler.ClientTickHandler;
import eutros.botaniapp.client.core.handler.ColorHandler;
import eutros.botaniapp.client.integration.patchouli.RegexDissection;
import eutros.botaniapp.common.block.BlockSparkPainter;
import eutros.botaniapp.common.block.flower.BlockFloatingSpecialFlower;
import eutros.botaniapp.common.core.proxy.IProxy;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ClientProxy implements IProxy {

    @Override
    public void registerHandlers() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    }

    @Override
    public long getWorldElapsedTicks() {
        return ClientTickHandler.ticksInGame;
    }

    private void clientSetup(FMLClientSetupEvent event) {
        registerRenderTypes();
    }

    private void registerRenderTypes() {
        BlockSparkPainter.dyeMap.values()
                .forEach(p -> RenderTypeLookup.setRenderLayer(p, RenderType.getTranslucent()));

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> Objects.requireNonNull(b.getRegistryName()).getNamespace().equals(Reference.MOD_ID))
                .forEach(b -> {
                    if(b instanceof BlockFloatingSpecialFlower || b instanceof FlowerBlock) {
                        RenderTypeLookup.setRenderLayer(b, RenderType.getCutout());
                    }
                });
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        ColorHandler.init();
        RegexDissection.init();
    }

}
