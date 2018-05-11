package com.elytradev.carpentrycubes.client.render.model;

import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.util.vector.Vector3f;

public class CarpentryTransformData {

    private Vector3f translation = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);

    public CarpentryTransformData() {
    }

    public CarpentryTransformData(javax.vecmath.Vector3f translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
    }

    public CarpentryTransformData(javax.vecmath.Vector3f translation,
        javax.vecmath.Vector3f rotation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        this.rotation = new Vector3f(rotation.x, rotation.y, rotation.z);
    }

    public CarpentryTransformData(javax.vecmath.Vector3f translation,
        javax.vecmath.Vector3f rotation, javax.vecmath.Vector3f scale) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        this.rotation = new Vector3f(rotation.x, rotation.y, rotation.z);
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public CarpentryTransformData setTranslation(Vector3f translation) {
        this.translation = translation;
        return this;
    }

    public CarpentryTransformData setTranslation(javax.vecmath.Vector3f translation) {
        return this.setTranslation(new Vector3f(translation.x, translation.y, translation.z));
    }

    public CarpentryTransformData setTranslation(float x, float y, float z) {
        return this.setTranslation(new Vector3f(x, y, z));
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public CarpentryTransformData setRotation(Vector3f rotation) {
        this.rotation = rotation;
        return this;
    }

    public CarpentryTransformData setRotation(javax.vecmath.Vector3f rotation) {
        return this.setRotation(new Vector3f(rotation.x, rotation.y, rotation.z));
    }

    public CarpentryTransformData setRotation(float x, float y, float z) {
        return this.setRotation(new Vector3f(x, y, z));
    }

    public Vector3f getScale() {
        return scale;
    }

    public CarpentryTransformData setScale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    public CarpentryTransformData setScale(javax.vecmath.Vector3f scale) {
        return this.setScale(new Vector3f(scale.x, scale.y, scale.z));
    }

    public CarpentryTransformData setScale(float x, float y, float z) {
        return this.setScale(new Vector3f(x, y, z));
    }

    public TRSRTransformation asTRSRTransform() {
        if (translation.equals(new Vector3f(0, 0, 0)) && scale.equals(new Vector3f(1, 1, 1))
            && rotation.equals(new Vector3f(0, 0, 0))) {
            return TRSRTransformation.identity();
        }

        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            TRSRTransformation.toVecmath(translation),
            TRSRTransformation.quatFromXYZDegrees(TRSRTransformation.toVecmath(this.rotation)),
            TRSRTransformation.toVecmath(scale), null));
    }
}
