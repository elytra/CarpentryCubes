package com.elytradev.carpentrycubes.client.render.model.builder;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import javax.vecmath.Vector3f;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuadEligibilityTester {

    private Vector3f[] points = new Vector3f[4];
    private int currentPoint = 0;
    private Vec3i requiredOffset;

    public QuadEligibilityTester(BakedQuad quad) {
        this.requiredOffset = quad.getFace().getDirectionVec();
    }

    public void put(float... data) {
        if (currentPoint == 4) {
            return;
        }

        // Some rounding to fix weird numbers caused by conversion back to floats.
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.round(data[i] * (10000F)) / 10000F;
        }

        points[currentPoint] = new Vector3f(data[0], data[1], data[2]);
        currentPoint++;
    }

    /**
     * Determines if the quad given is a 1x1 square.
     *
     * @return true if the quad matches, false otherwise.
     */
    public boolean isValid() {
        if (!checkForMatchingPoints())
            return false;

        for (int i = 0; i < 4; i++) {
            Vector3f point = points[i];

            if (!checkPointAgainstFace(point))
                return false;

            if (!checkPointRounding(point))
                return false;
        }

        return true;
    }

    /**
     * Checks that all the points are distinct and that there are no overlaps.
     *
     * @return true if every point is distinct, false otherwise.
     */
    private boolean checkForMatchingPoints() {
        List<Integer> uniquePoints = Lists.newArrayList();
        for (Vector3f point : this.points) {
            int hashCode = point.hashCode();
            if(!uniquePoints.contains(hashCode)){
                uniquePoints.add(hashCode);
            }
        }

        return uniquePoints.size() == this.points.length;
    }

    /**
     * Check if the given point only uses round numbers.
     *
     * @param point the point to test against.
     * @return true if the point matches, false if it failed.
     */
    private boolean checkPointRounding(Vector3f point) {
        return (!(point.getX() != 0) || !(point.getX() != 1)) &&
                (!(point.getY() != 0) || !(point.getY() != 1)) &&
                (!(point.getZ() != 0) || !(point.getZ() != 1));
    }

    /**
     * Checks that a point has the correct transform for its given face.
     *
     * @param point the point to test against.
     * @return true if the point matches, false if it failed.
     */
    private boolean checkPointAgainstFace(Vector3f point) {
        boolean passed = true;

        if ((requiredOffset.getX() == -1 && point.getX() != 0) ||
                (requiredOffset.getX() == 1 && point.getX() != 1))
            passed = false;
        if ((requiredOffset.getY() == -1 && point.getY() != 0) ||
                (requiredOffset.getY() == 1 && point.getY() != 1))
            passed = false;
        if ((requiredOffset.getZ() == -1 && point.getZ() != 0) ||
                (requiredOffset.getZ() == 1 && point.getZ() != 1))
            passed = false;

        return passed;
    }
}
