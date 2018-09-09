package com.elytradev.carpentrycubes.client.render.model.builder;

import com.elytradev.carpentrycubes.client.render.QuadBuilder;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class CarpentryQuad {

    private EnumFacing face;
    private CarpentryVertex[] vertices = new CarpentryVertex[4];
    private Vector3f normals = null;
    private Map<TRSRTransformation, PrebuiltData> prebuiltQuads = Maps.newHashMap();

    class PrebuiltData {
        BakedQuad baseQuad;
        Map<TextureAtlasSprite, BakedQuad> quadsForSprite = Maps.newHashMap();

        public PrebuiltData(BakedQuad baseQuad) {
            this.baseQuad = baseQuad;
            this.quadsForSprite.put(baseQuad.getSprite(), baseQuad);
        }

        public BakedQuad getQuad(TextureAtlasSprite sprite, int tint) {
            BakedQuad quadOut = quadsForSprite.get(sprite);
            if (quadOut != null) {
                if (tint != -1) {
                    quadOut = this.reTintQuad(quadOut, tint);
                }
            } else {
                quadOut = new BakedQuadRetextured(baseQuad, sprite);
                quadsForSprite.put(sprite, quadOut);
                if (tint != -1) {
                    quadOut = this.reTintQuad(quadOut, tint);
                }
            }
            return quadOut;
        }

        private BakedQuad reTintQuad(BakedQuad quad, int newTint) {
            return new BakedQuad(quad.getVertexData(), newTint, quad.getFace(), quad.getSprite(),
                    quad.shouldApplyDiffuseLighting(), quad.getFormat());
        }

    }

    public CarpentryQuad(EnumFacing face) {
        this.face = face;
    }

    public BakedQuad bake(TRSRTransformation transform, EnumFacing facing, TextureAtlasSprite sprite, int tintIndex) {
        PrebuiltData prebuiltData = this.prebuiltQuads.get(transform);
        if (prebuiltData == null) {
            QuadBuilder builder = new QuadBuilder(transform, facing);
            for (CarpentryVertex vertex : getVertices()) {
                float[] UVs = vertex.getUVs(transform);
                float u = sprite.getInterpolatedU(UVs[0]), v = sprite.getInterpolatedV(UVs[1]);
                builder.putVertex(vertex.getX(), vertex.getY(), vertex.getZ(), u, v,
                        vertex.getNormalX(), vertex.getNormalY(), vertex.getNormalZ());
            }
            PrebuiltData baseQuad = new PrebuiltData(builder.build(sprite, tintIndex));
            prebuiltQuads.put(transform, baseQuad);
            return baseQuad.baseQuad;
        } else {
            return prebuiltData.getQuad(sprite, tintIndex);
        }
    }

    public int getNextVertex() {
        for (int i = 0; i < this.vertices.length; i++) {
            if (this.vertices[i] == null)
                return i;
        }
        throw new IllegalArgumentException("Unable to determine next empty vertex on the given quad.");
    }

    public void setVertex(int index, float[] data) {
        this.vertices[index] = new CarpentryVertex(this, data);
    }

    public EnumFacing getFace() {
        return face;
    }

    public Vector3f getNormals() {
        if (this.normals == null) {
            // Generate normals
            CarpentryVertex[] vertices = this.getVertices();

            Vector3f vPrev = vertices[3].getPosition();
            Vector3f vCur = vertices[0].getPosition();
            Vector3f vNext = vertices[1].getPosition();

            if (vPrev.equals(vCur)) {
                vPrev = vertices[2].getPosition();
            }
            if (vNext.equals(vCur)) {
                vNext = vertices[2].getPosition();
            }

            vPrev.sub(vCur);
            vNext.sub(vCur);

            this.normals = new Vector3f();
            this.normals.cross(vNext, vPrev);
            this.normals.normalize();
        }
        return this.normals;
    }

    public void setNormals(Vector3f normals) {
        this.normals = normals;
    }

    public CarpentryVertex[] getVertices() {
        return this.vertices;
    }

    public boolean isComplete() {
        return Arrays.stream(vertices).allMatch(Objects::nonNull);
    }
}
