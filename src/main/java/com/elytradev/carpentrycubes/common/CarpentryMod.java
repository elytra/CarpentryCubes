/*
 *    Copyright 2017 Benjamin K (darkevilmac)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.elytradev.carpentrycubes.common;

import static com.elytradev.carpentrycubes.common.CarpentryMod.GUI_FACTORY;
import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_ID;
import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_NAME;
import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_VER;

import com.elytradev.carpentrycubes.common.network.CarpentryNetworking;
import com.elytradev.carpentrycubes.common.proxy.CommonProxy;
import com.elytradev.probe.api.IProbeDataProvider;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VER, guiFactory = GUI_FACTORY)
public class CarpentryMod {

    public static final String MOD_ID = "carpentrycubes";
    public static final String MOD_NAME = "Carpentry Cubes";
    public static final String MOD_VER = "%VERSION%";
    public static final String GUI_FACTORY = "com.elytradev.carpentrycubes.client.gui.CarpentryGUIFactory";
    public static final String RESOURCE_DOMAIN = "carpentrycubes:";
    public static CarpentryContent CONTENT;

    public static boolean INDEV = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Instance
    public static CarpentryMod INSTANCE;

    @CapabilityInject(IProbeDataProvider.class)
    public static Capability<?> PROBE_CAPABILITY;

    @SidedProxy(serverSide = "com.elytradev.carpentrycubes.common.proxy.CommonProxy",
        clientSide = "com.elytradev.carpentrycubes.client.proxy.ClientProxy")
    public static CommonProxy PROXY;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        PROXY.registerHandlers();
        CONTENT = new CarpentryContent();
        CONTENT.preInit(e);
        CarpentryNetworking.setupNetwork();

        PROXY.registerRenderers(e.getModState());
    }

    @EventHandler
    public void onInit(FMLInitializationEvent e) {
        CONTENT.init(e);
        PROXY.registerRenderers(e.getModState());
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent e) {
        CONTENT.postInit(e);
        PROXY.registerRenderers(e.getModState());
    }
}