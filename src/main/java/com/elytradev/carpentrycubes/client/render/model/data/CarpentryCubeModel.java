package com.elytradev.carpentrycubes.client.render.model.data;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.List;

public class CarpentryCubeModel implements ICarpentryModel<BlockCarpentry> {
    private static CarpentryCubeModel INSTANCE = new CarpentryCubeModel();
    private CarpentryModelData modelData;

    private CarpentryCubeModel() {
        this.modelData = new CarpentryModelData(getInstance());

        this.modelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        this.modelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        this.modelData.addQuadInstruction(EnumFacing.UP, 0, 1, 1);
        this.modelData.addQuadInstruction(EnumFacing.UP, 1, 1, 1);
        this.modelData.addQuadInstruction(EnumFacing.UP, 1, 1, 0);
        this.modelData.addQuadInstruction(EnumFacing.UP, 0, 1, 0);

        this.modelData.addQuadInstruction(EnumFacing.NORTH, 1, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.NORTH, 0, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.NORTH, 0, 1, 1);
        this.modelData.addQuadInstruction(EnumFacing.NORTH, 1, 1, 1);

        this.modelData.addQuadInstruction(EnumFacing.SOUTH, 0, 0, 1);
        this.modelData.addQuadInstruction(EnumFacing.SOUTH, 1, 0, 1);
        this.modelData.addQuadInstruction(EnumFacing.SOUTH, 1, 1, 1);
        this.modelData.addQuadInstruction(EnumFacing.SOUTH, 0, 1, 1);

        this.modelData.addQuadInstruction(EnumFacing.WEST, 0, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.WEST, 1, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.WEST, 1, 0, 1);
        this.modelData.addQuadInstruction(EnumFacing.WEST, 0, 0, 1);

        this.modelData.addQuadInstruction(EnumFacing.EAST, 0, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.EAST, 1, 0, 0);
        this.modelData.addQuadInstruction(EnumFacing.EAST, 1, 0, 1);
        this.modelData.addQuadInstruction(EnumFacing.EAST, 0, 0, 1);
    }

    public static CarpentryCubeModel getInstance() {
        return INSTANCE;
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos, ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites, int[] quadCount) {
        this.modelData.setState(state);
        for (int i = 0; i < EnumFacing.values().length; i++) {
            for (int q = 0; q < quadCount[i]; q++) {
                EnumFacing side = EnumFacing.values()[i];
                TextureAtlasSprite sprite = faceSprites[i].get(q);
                int tintIndex = tintIndices[i].get(q);

                this.modelData.setFaceData(q, side, sprite, tintIndex);
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
        this.modelData.setState(null);
        this.modelData.setTransform(EnumFacing.UP, TRSRTransformation.identity());
        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing side = EnumFacing.values()[i];
            TextureAtlasSprite sprite = getDefaultSprite();
            int tintIndex = -1;

            this.modelData.setFaceData(0, side, sprite, tintIndex);
        }

        return this.modelData.buildModel().getGeneralQuads();
    }
}
