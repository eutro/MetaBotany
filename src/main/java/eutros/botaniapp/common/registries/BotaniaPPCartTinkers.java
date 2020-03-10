package eutros.botaniapp.common.registries;

import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.common.block.tinkerer.cart.handlers.ContainerTinkerHandler;
import eutros.botaniapp.common.block.tinkerer.cart.handlers.PoolTinkerHandler;
import eutros.botaniapp.common.block.tinkerer.cart.handlers.TNTTinkerHandler;
import eutros.botaniapp.common.utils.Reference;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static eutros.botaniapp.common.item.BotaniaPPItems.register;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class BotaniaPPCartTinkers {

    @SubscribeEvent
    public static void registerTinkers(RegistryEvent.Register<CartTinkerHandler> evt) {
        IForgeRegistry<CartTinkerHandler> r = evt.getRegistry();

        register(r, new ContainerTinkerHandler(), "container_tinker");
        register(r, new PoolTinkerHandler(), "pool_tinker");
        register(r, new TNTTinkerHandler(), "tnt_tinker");
    }
}
