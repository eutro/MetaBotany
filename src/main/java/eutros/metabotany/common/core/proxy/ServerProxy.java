package eutros.metabotany.common.core.proxy;

import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerProxy implements IProxy {

    @Override
    public void registerHandlers() {
    }

    @Override
    public long getWorldElapsedTicks() {
        return ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD).getGameTime();
    }

}
