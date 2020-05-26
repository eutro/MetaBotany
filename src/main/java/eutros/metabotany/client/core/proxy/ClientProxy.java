package eutros.metabotany.client.core.proxy;

import eutros.metabotany.client.core.handler.ClientTickHandler;
import eutros.metabotany.client.core.handler.ColorHandler;
import eutros.metabotany.client.integration.patchouli.RegexDissection;
import eutros.metabotany.client.render.RenderHelper;
import eutros.metabotany.common.block.BlockSparkPainter;
import eutros.metabotany.common.block.flower.BlockFloatingSpecialFlower;
import eutros.metabotany.common.core.proxy.IProxy;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ClientProxy implements IProxy {

    @Override
    public void registerHandlers() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::clientSetup);
        bus.addListener(this::loadComplete);
        bus.register(RenderHelper.getInstance());
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
