package com.elytradev.carpentrycubes.client.render.model.data;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.block.BlockCarpentryMorphable;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentryMorphable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class CarpentryMorphableModel implements ICarpentryModel<BlockCarpentryMorphable> {

    private static CarpentryMorphableModel INSTANCE = new CarpentryMorphableModel();

    private CarpentryMorphableModelData modelData = new CarpentryMorphableModelData(this);

    public static CarpentryMorphableModel getInstance() {
        return INSTANCE;
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos, ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites, int[] quadCount) {
        if (state instanceof IExtendedBlockState) {
            TileCarpentry tile = ((IExtendedBlockState) state).getValue(BlockCarpentry.CARPENTRY_TILE);
            if (tile instanceof TileCarpentryMorphable) {
                this.modelData.setState(state);
                this.modelData.setTile((TileCarpentryMorphable) tile);
                for (int i = 0; i < EnumFacing.values().length; i++) {
                    for (int q = 0; q < quadCount[i]; q++) {
                        EnumFacing side = EnumFacing.values()[i];
                        TextureAtlasSprite sprite = faceSprites[i].get(q);
                        int tintIndex = tintIndices[i].get(q);

                        this.modelData.setFaceData(q, side, sprite, tintIndex);
                    }
                }
            }
        }

        return this.modelData.buildModel();
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }

    @Override
    public List<BakedQuad> getDefaultModel() {
        return this.modelData.buildModel().getGeneralQuads();
    }
}
