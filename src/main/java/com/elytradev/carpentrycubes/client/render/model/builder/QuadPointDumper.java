package com.elytradev.carpentrycubes.client.render.model.builder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.model.pipeline.LightUtil;

import javax.vecmath.Vector3f;

public class QuadPointDumper {

    private Vector3f[] points = new Vector3f[4];
    private int currentPoint = 0;
    private VertexFormat formatTo;

    public QuadPointDumper(BakedQuad quad) {
        float[] data = new float[4];
        VertexFormat formatFrom = DefaultVertexFormats.ITEM;
        this.formatTo = quad.getFormat();
        int countFrom = formatFrom.getElementCount();
        int countTo = this.formatTo.getElementCount();
        int[] eMap = LightUtil.mapFormats(formatFrom, this.formatTo);
        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < countFrom; e++) {
                if (eMap[e] != countTo) {
                    unpack(quad.getVertexData(), data, this.formatTo, v, eMap[e]);
                    put(e, data);
                } else {
                    put(e);
                }
            }
        }
    }

    private void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {
        int length = 4 < to.length ? 4 : to.length;
        VertexFormatElement element = formatFrom.getElement(e);
        int vertexStart = v * formatFrom.getSize() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        VertexFormatElement.EnumType type = element.getType();
        int size = type.getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + size - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                if (type == VertexFormatElement.EnumType.FLOAT) {
                    to[i] = Float.intBitsToFloat(bits);
                } else if (type == VertexFormatElement.EnumType.UBYTE || type == VertexFormatElement.EnumType.USHORT) {
                    to[i] = (float) bits / mask;
                } else if (type == VertexFormatElement.EnumType.UINT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / 0xFFFFFFFFL);
                } else if (type == VertexFormatElement.EnumType.BYTE) {
                    to[i] = ((float) (byte) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.EnumType.SHORT) {
                    to[i] = ((float) (short) bits) / (mask >> 1);
                } else if (type == VertexFormatElement.EnumType.INT) {
                    to[i] = (float) ((double) (bits & 0xFFFFFFFFL) / (0xFFFFFFFFL >> 1));
                }
            } else {
                to[i] = 0;
            }
        }
    }

    public void put(int element, float... data) {
        VertexFormatElement elementType = this.formatTo.getElement(element);
        if (elementType != DefaultVertexFormats.POSITION_3F)
            return;

        if (currentPoint == 4) {
            return;
        }

        points[currentPoint] = new Vector3f(data[0], data[1], data[2]);
        currentPoint++;
    }

    public Vector3f[] getPoints() {
        return points;
    }
}
