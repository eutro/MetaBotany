package eutros.metabotany.common.core.network;

import eutros.metabotany.api.internal.network.IMetaBotanyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public class MetaBotanyEffectPacket implements IMetaBotanyPacket<MetaBotanyEffectPacket> {

    public static final IPacketHelper<MetaBotanyEffectPacket> HELPER = new Helper();

    public enum EffectType {
        /**
         * - Entity ID (int: 4 bytes)
         * - Particle Count (int: 4 bytes)
         */
        SMOKE(8);

        public final int bytes;

        EffectType(int bytes) {
            this.bytes = bytes;
        }
    }

    private EffectType type;
    private byte[] data;

    public MetaBotanyEffectPacket(EffectType type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    private static class Helper implements IPacketHelper<MetaBotanyEffectPacket> {

        @Override
        public void handle(MetaBotanyEffectPacket packet, final Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            if(context.getDirection().getReceptionSide().isServer()) {
                context.setPacketHandled(true);
                return;
            }

            context.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                World world = mc.world;
                ByteBuffer buffer = ByteBuffer.wrap(packet.data);

                if(world == null)
                    return;

                switch(packet.type) {
                    case SMOKE:
                        Entity entity = world.getEntityByID(buffer.getInt());
                        if(entity == null)
                            return;

                        int count = buffer.getInt();
                        doSmokeEffect(entity, count);

                        break;
                }
            });
            context.setPacketHandled(true);
        }

        @Override
        public MetaBotanyEffectPacket decode(PacketBuffer buffer) {
            EffectType type = EffectType.values()[buffer.readByte()];
            byte[] data = new byte[type.bytes];

            for(int i = 0; i < type.bytes; i++) {
                data[i] = buffer.readByte();
            }

            return new MetaBotanyEffectPacket(type, data);
        }

        @Override
        public void encode(MetaBotanyEffectPacket packet, PacketBuffer buffer) {
            buffer.writeByte(packet.type.ordinal());

            for(byte b : packet.data)
                buffer.writeByte(b);
        }

    }

    public static void doSmokeEffect(Entity entity, int count) {
        for(int i = 0; i < count; i++) {
            double m = 0.01;
            double d0 = entity.world.rand.nextGaussian() * m;
            double d1 = entity.world.rand.nextGaussian() * m;
            double d2 = entity.world.rand.nextGaussian() * m;
            double d3 = 10.0D;
            entity.world.addParticle(ParticleTypes.POOF,
                    entity.getPosX() + entity.world.rand.nextFloat() * entity.getWidth() * 2.0F - entity.getWidth() - d0 * d3,
                    entity.getPosY() + entity.world.rand.nextFloat() * entity.getHeight() - d1 * d3,
                    entity.getPosZ() + entity.world.rand.nextFloat() * entity.getWidth() * 2.0F - entity.getWidth() - d2 * d3, d0, d1, d2);
        }
    }

}
