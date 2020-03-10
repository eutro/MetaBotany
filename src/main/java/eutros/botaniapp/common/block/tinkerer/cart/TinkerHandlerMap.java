package eutros.botaniapp.common.block.tinkerer.cart;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import eutros.botaniapp.api.carttinkerer.CartTinkerHandler;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class TinkerHandlerMap {
    private static TinkerHandlerMap instance;

    public static final ResourceLocation STATE_MAP_LOC = new ResourceLocation(Reference.MOD_ID, "state_map");
    private Multimap<BlockState, ResourceLocation> stateMap;

    public static final ResourceLocation CART_MAP_LOC = new ResourceLocation(Reference.MOD_ID, "cart_map");
    private Multimap<Class<? extends AbstractMinecartEntity>, ResourceLocation> cartMap;

    private TinkerHandlerMap() {
        stateMap = ArrayListMultimap.create();
        cartMap = ArrayListMultimap.create();
    }

    public static TinkerHandlerMap getInstance() {
        if(instance == null) {
            instance = new TinkerHandlerMap();
        }
        return instance;
    }

    public void create(@NotNull IForgeRegistryInternal<CartTinkerHandler> owner, RegistryManager stage) {
        owner.setSlaveMap(STATE_MAP_LOC, stateMap);
        owner.setSlaveMap(CART_MAP_LOC, cartMap);
    }

    public void clear(IForgeRegistryInternal<CartTinkerHandler> owner, RegistryManager stage) {
        stateMap.clear();
        cartMap.clear();
    }

    public void add(IForgeRegistryInternal<CartTinkerHandler> owner, RegistryManager stage, int id, @NotNull CartTinkerHandler handler, @Nullable CartTinkerHandler oldHandler) {
        handler.workingStates.forEach(state -> stateMap.put(state, handler.getRegistryName()));
        handler.cartTypes.forEach(type -> cartMap.put(type, handler.getRegistryName()));
    }
}
