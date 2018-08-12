package com.elytradev.carpentrycubes.client.render.model.builder;

import javax.vecmath.Vector3f;
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

    public float[] getUVs() {
        return new float[]{this.data[3], this.data[4]};
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
