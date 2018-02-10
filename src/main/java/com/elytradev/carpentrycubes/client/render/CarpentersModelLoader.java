package com.elytradev.carpentrycubes.client.render;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_ID;

public class CarpentersModelLoader implements ICustomModelLoader {

    private CarpentersModel model;

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        // TODO: Additional checks for the resource path.
        return modelLocation.getResourceDomain().equals(MOD_ID);

    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        if (model == null)
            model = new CarpentersModel();

        return model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        model = null;
    }
}
