package com.hyfata.najoan.koreanpatch.util.minecraft;

import com.hyfata.najoan.koreanpatch.mixin.accessor.GuiGraphicsAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class RenderUtil {
    static Minecraft client = Minecraft.getInstance();

    public static void drawCenteredText(GuiGraphics context, FormattedCharSequence text, float x, float y) {
        drawCenteredText(context, text, x, y, -1);
    }

    public static void drawCenteredText(GuiGraphics context, FormattedCharSequence text, float x, float y, int color) {
        Font textRenderer = client.font;
        float textWidth = textRenderer.width(text);
        float xPosition = x - textWidth / 2.0f;
        float yPosition = y - client.font.lineHeight / 2.0f;
        drawText(context, text, xPosition, yPosition, color);
    }

    public static void drawText(GuiGraphics context, FormattedCharSequence text, float x, float y) {
        drawText(context, text, x, y, -1);
    }

    public static void drawText(GuiGraphics context, FormattedCharSequence text, float x, float y, int color) {
        GuiGraphicsAccessor guiGraphicsAccessor = (GuiGraphicsAccessor) context;
        Font textRenderer = client.font;
        Matrix4f matrix = context.pose().last().pose();
        MultiBufferSource vertexConsumers = guiGraphicsAccessor.getBufferSource();
        RenderSystem.enableBlend();
        textRenderer.drawInBatch(text, x, y, color, false, matrix, vertexConsumers, Font.DisplayMode.NORMAL, 0, 15728880);
        RenderSystem.disableBlend();
    }

    public static void fill(GuiGraphics context, float x1, float y1, float x2, float y2, int color) {
        GuiGraphicsAccessor guiGraphicsAccessor = (GuiGraphicsAccessor) context;
        Matrix4f matrix = context.pose().last().pose();
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        RenderSystem.enableBlend();
        VertexConsumer vertexConsumer = guiGraphicsAccessor.getBufferSource().getBuffer(RenderType.gui());
        vertexConsumer.addVertex(matrix, x1, y1, 0f).setColor(color);
        vertexConsumer.addVertex(matrix, x1, y2, 0f).setColor(color);
        vertexConsumer.addVertex(matrix, x2, y2, 0f).setColor(color);
        vertexConsumer.addVertex(matrix, x2, y1, 0f).setColor(color);
        context.flush();
        RenderSystem.disableBlend();
    }

    public static void drawQuarterCircleFrame(GuiGraphics context, float centerX, float centerY, float radius, int frameColor, float frameThickness, QuarterCircleDirection direction) {
        float angleStep = Mth.PI / 2f / 36f;

        for (float angle = 0f; angle <= Math.PI / 2; angle += angleStep) {
            float projX = Mth.cos(angle);
            float projY = Mth.sin(angle);

            float startX;
            float startY;

            switch (direction) {
                case TOP_LEFT -> {
                    startX = centerX - radius * projX;
                    startY = centerY - radius * projY;
                }
                case TOP_RIGHT -> {
                    startX = centerX + radius * projX;
                    startY = centerY - radius * projY;
                }
                case BOTTOM_LEFT -> {
                    startX = centerX - radius * projX;
                    startY = centerY + radius * projY;
                }
                case BOTTOM_RIGHT -> {
                    startX = centerX + radius * projX;
                    startY = centerY + radius * projY;
                }
                default -> throw new IllegalArgumentException("Invalid QuarterCircleDirection");
            }

            float endX = startX + frameThickness;
            float endY = startY + frameThickness;

            RenderUtil.fill(context, startX, startY, endX, endY, frameColor);
        }
    }

    public enum QuarterCircleDirection {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
