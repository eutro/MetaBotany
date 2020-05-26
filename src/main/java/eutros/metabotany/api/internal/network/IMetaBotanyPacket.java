package eutros.metabotany.api.internal.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMetaBotanyPacket<T extends IMetaBotanyPacket<?>> {

    interface IPacketHelper<T extends IMetaBotanyPacket<?>> {

        void handle(T packet, final Supplier<NetworkEvent.Context> ctx);

        T decode(PacketBuffer buffer);

        void encode(T packet, PacketBuffer buffer);

    }

}
