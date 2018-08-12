package com.elytradev.carpentrycubes.client.render.model.builder;

import net.minecraft.util.EnumFacing;

import javax.vecmath.Vector3f;
import java.util.Arrays;
import java.util.Objects;

public class CarpentryQuad {

    private EnumFacing face;
    private CarpentryVertex[] vertices = new CarpentryVertex[4];
    private Vector3f normals = null;

    public CarpentryQuad(EnumFacing face) {
        this.face = face;
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
        if (true) {
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
