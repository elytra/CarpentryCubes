package com.elytradev.carpentrycubes.client.render.model;

import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_ID;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class CarpentryModelLoader implements ICustomModelLoader {

    private IModel model = (state, format, bakedTextureGetter) -> new CarpentryBakedModel();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        // TODO: Additional checks for the resource path.
        return modelLocation.getResourceDomain().equals(MOD_ID) && !modelLocation.getResourcePath().contains("tool");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        if (model == null)
            model = (state, format, bakedTextureGetter) -> new CarpentryBakedModel();

        return model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        model = (state, format, bakedTextureGetter) -> new CarpentryBakedModel();
    }
}
