package eutros.metabotany.common;

import eutros.metabotany.api.MetaBotanyAPI;
import eutros.metabotany.client.core.proxy.ClientProxy;
import eutros.metabotany.common.config.MetaBotanyConfig;
import eutros.metabotany.common.core.network.PacketHandler;
import eutros.metabotany.common.core.proxy.IProxy;
import eutros.metabotany.common.core.proxy.ServerProxy;
import eutros.metabotany.common.item.MetaBotanyItems;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.api.mana.ILens;

@Mod(Reference.MOD_ID)
public class MetaBotany {

    public static MetaBotany instance;
    public static IProxy proxy;

    public MetaBotany() {
        instance = this;
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        proxy.registerHandlers();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, MetaBotanyConfig.COMMON_SPEC);
        ctx.registerConfig(ModConfig.Type.CLIENT, MetaBotanyConfig.CLIENT_SPEC);
        ctx.registerConfig(ModConfig.Type.SERVER, MetaBotanyConfig.SERVER_SPEC);

        MetaBotanyAPI.setInstance(EnchantmentType.create("MANA_LENS", ILens.class::isInstance));
    }

    private void commonSetup(FMLCommonSetupEvent evt) {
        MetaBotanyItems.addDispenserBehaviours();
        PacketHandler.init();
    }

}
