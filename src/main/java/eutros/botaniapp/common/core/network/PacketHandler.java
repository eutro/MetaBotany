package eutros.botaniapp.common.core.network;

import eutros.botaniapp.api.internal.network.IBotaniaPPPacket;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "chan"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void init() {
        int id = 0;
        HANDLER.registerMessage(id, BotaniaPPEffectPacket.class, BotaniaPPEffectPacket.HELPER::encode, BotaniaPPEffectPacket.HELPER::decode, BotaniaPPEffectPacket.HELPER::handle);
    }

    /**
     * Send message to all within 64 blocks that have this chunk loaded
     */
    public static void sendToNearby(World world, BlockPos pos, IBotaniaPPPacket<?> toSend) {
        if(world instanceof ServerWorld) {
            ServerWorld ws = (ServerWorld) world;

            ws.getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(pos), false)
                    .filter(p -> p.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
                    .forEach(p -> HANDLER.send(PacketDistributor.PLAYER.with(() -> p), toSend));
        }
    }

    public static void sendToNearby(World world, Entity e, IBotaniaPPPacket<?> toSend) {
        sendToNearby(world, new BlockPos(e), toSend);
    }

    public static void sendTo(ServerPlayerEntity playerMP, IBotaniaPPPacket<?> toSend) {
        HANDLER.sendTo(toSend, playerMP.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendNonLocal(ServerPlayerEntity playerMP, IBotaniaPPPacket<?> toSend) {
        if(playerMP.server.isDedicatedServer() || !playerMP.getGameProfile().getName().equals(playerMP.server.getServerOwner())) {
            sendTo(playerMP, toSend);
        }
    }

    public static void sendToServer(IBotaniaPPPacket<?> msg) {
        HANDLER.sendToServer(msg);
    }

    private PacketHandler() {
    }

}
