package eutros.metabotany.common.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class MetaBotanyFakePlayer extends FakePlayer {

    private static final GameProfile gameProfile = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "METABOTANY_FAKE_PLAYER");

    public MetaBotanyFakePlayer(ServerWorld world) {
        super(world, gameProfile);
    }

}
