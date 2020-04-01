package eutros.botaniapp.api.internal.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IBotaniaPPPacket<T extends IBotaniaPPPacket<?>> {

    interface IPacketHelper<T extends IBotaniaPPPacket<?>> {

        void handle(T packet, final Supplier<NetworkEvent.Context> ctx);

        T decode(PacketBuffer buffer);

        void encode(T packet, PacketBuffer buffer);

    }

}
