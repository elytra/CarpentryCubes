package com.elytradev.carpentrycubes.common.proxy;

import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.carpentrycubes.common.handlers.CarpentryGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void registerRenderers(LoaderState.ModState state) {
    }

    public void registerHandlers() {
        NetworkRegistry.INSTANCE.registerGuiHandler(CarpentryMod.INSTANCE, new CarpentryGuiHandler());

        MinecraftForge.EVENT_BUS.register(this);
    }

}
