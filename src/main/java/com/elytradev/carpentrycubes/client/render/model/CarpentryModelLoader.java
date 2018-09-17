package com.elytradev.carpentrycubes.client.render.model;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_ID;

public class CarpentryModelLoader implements ICustomModelLoader {

    private List<ModelWithPredicate> models = Lists.newArrayList();

    private class ModelWithPredicate {
        private Predicate<ResourceLocation> predicate;
        private IModel model;

        public ModelWithPredicate(Predicate<ResourceLocation> predicate, IModel model) {
            this.predicate = predicate;
            this.model = model;
        }

        public boolean providesFor(ResourceLocation resourceLocation) {
            return predicate.test(resourceLocation);
        }

        public IModel getModel() {
            return model;
        }
    }

    public void registerModel(Predicate<ResourceLocation> predicate, IModel model) {
        this.models.add(new ModelWithPredicate(predicate, model));
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getNamespace().equals(MOD_ID) && models.stream().anyMatch(m -> m.providesFor(modelLocation));
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        Optional<ModelWithPredicate> model = models.stream().filter(m -> m.providesFor(modelLocation)).findFirst();
        return model.map(ModelWithPredicate::getModel).orElse(null);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NO-OP
    }
}
