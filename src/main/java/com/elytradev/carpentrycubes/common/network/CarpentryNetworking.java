package com.elytradev.carpentrycubes.common.network;

import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;

public class CarpentryNetworking {

    public static final NetworkContext NETWORK = NetworkContext.forChannel(CarpentryMod.MOD_ID);

    public static void setupNetwork() {
        registerMessage(TileUpdateMessage.class);
    }

    private static void registerMessage(Class<? extends Message> clazz) {
        NETWORK.register(clazz);
    }

}
