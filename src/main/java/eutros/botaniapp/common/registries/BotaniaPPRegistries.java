package eutros.botaniapp.common.registries;

import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.common.block.tinkerer.cart.TinkerHandlerMap;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BotaniaPPRegistries {

    public static IForgeRegistry<CartTinkerHandler> CART_TINKER;
    public static final ResourceLocation CART_TINKER_DEFAULT = new ResourceLocation(Reference.MOD_ID, "default_tinker");

    @SubscribeEvent
    public static void registerRegistries(RegistryEvent.NewRegistry event) {
        RegistryBuilder<CartTinkerHandler> builder = new RegistryBuilder<>();
        builder.setName(new ResourceLocation(Reference.MOD_ID, "cart_tinker"));
        builder.setDefaultKey(CART_TINKER_DEFAULT);
        builder.setType(CartTinkerHandler.class);
        builder.add((IForgeRegistry.ClearCallback<CartTinkerHandler>) TinkerHandlerMap.getInstance()::clear);
        builder.add((IForgeRegistry.CreateCallback<CartTinkerHandler>) TinkerHandlerMap.getInstance()::create);
        builder.add(TinkerHandlerMap.getInstance()::add);
        CART_TINKER = builder.create();
    }

}
