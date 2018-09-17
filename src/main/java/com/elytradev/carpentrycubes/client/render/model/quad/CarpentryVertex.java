package com.elytradev.carpentrycubes.client.render.model.quad;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Arrays;

public class CarpentryVertex {

    private CarpentryQuad parent;
    private float[] data;

    protected CarpentryVertex(CarpentryQuad parent, float[] data) {
        this.parent = parent;
        this.data = data;
    }

    public Vector3f getNormals() {
        return parent.getNormals();
    }

    public Vector3f getPosition() {
        return new Vector3f(data);
    }

    public Vector3f getPosition(TRSRTransformation transform) {
        Vector3f position = this.getPosition();
        Vector4f transformedPosition = new Vector4f(position.getX(), position.getY(), position.getZ(), 1);
        transform.getMatrix().transform(transformedPosition);
        return new Vector3f(transformedPosition.getX(), transformedPosition.getY(), transformedPosition.getZ());
    }

    public float[] getUVs(TRSRTransformation transform) {
        Vector3f pos = this.getPosition(transform);
        EnumFacing face = transform.rotate(parent.getFace());
        float u = 0, v = 0;
        switch (face) {
            case DOWN:
                u = pos.getX() * 16F;
                v = -16F * (pos.getZ() - 1F);
                break;
            case UP:
                u = pos.getX() * 16F;
                v = pos.getZ() * 16F;
                break;
            case NORTH:
                u = -16F * (pos.getX() - 1F);
                v = -16F * (pos.getY() - 1F);
                break;
            case SOUTH:
                u = 16F * pos.getX();
                v = -16F * (pos.getY() - 1F);
                break;
            case WEST:
                u = 16F * pos.getZ();
                v = -16F * (pos.getY() - 1F);
                break;
            case EAST:
                u = -16F * (pos.getZ() - 1F);
                v = -16F * (pos.getY() - 1F);
                break;
        }
        return new float[]{u, v};
    }

    public float getX() {
        return data[0];
    }

    public float getY() {
        return data[1];
    }

    public float getZ() {
        return data[2];
    }

    public float getNormalX() {
        return this.getNormals().x;
    }

    public float getNormalY() {
        return this.getNormals().y;
    }

    public float getNormalZ() {
        return this.getNormals().z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CarpentryVertex) {
            return Arrays.equals(((CarpentryVertex) obj).data, this.data);
        }

        return super.equals(obj);
    }
}
