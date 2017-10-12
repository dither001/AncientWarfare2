package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderTools {


    public static void setFullColorLightmap() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.f, 240.f);
    }

    /*
     * @param textureWidth  texture width
     * @param textureHeight texture height
     * @param texStartX     pixel start U
     * @param texStartY     pixel start V
     * @param texUsedWidth  pixel U width (width of used tex in pixels)
     * @param texUsedHeight pixel V height (height of used tex in pixels)
     * @param renderStartX  render position x
     * @param renderStartY  render position y
     * @param renderHeight  render height
     * @param renderWidth   render width
     */
    public static void renderQuarteredTexture(int textureWidth, int textureHeight, int texStartX, int texStartY, int texUsedWidth, int texUsedHeight, int renderStartX, int renderStartY, int renderWidth, int renderHeight) {
        //perspective percent x, y
        float perX = 1.f / ((float) textureWidth);
        float perY = 1.f / ((float) textureHeight);
        float texMinX = ((float) texStartX) * perX;
        float texMinY = ((float) texStartY) * perY;
        float texMaxX = (float) (texStartX + texUsedWidth) * perX;
        float texMaxY = (float) (texStartY + texUsedHeight) * perY;
        float halfWidth = (((float) renderWidth) / 2.f) * perX;
        float halfHeight = (((float) renderHeight) / 2.f) * perY;
        float halfRenderWidth = ((float) renderWidth) * 0.5f;
        float halfRenderHeight = ((float) renderHeight) * 0.5f;

        //draw top-left quadrant
        renderTexturedQuad(renderStartX, renderStartY, renderStartX + halfRenderWidth, renderStartY + halfRenderHeight, texMinX, texMinY, texMinX + halfWidth, texMinY + halfHeight);

        //draw top-right quadrant
        renderTexturedQuad(renderStartX + halfRenderWidth, renderStartY, renderStartX + halfRenderWidth * 2, renderStartY + halfRenderHeight, texMaxX - halfWidth, texMinY, texMaxX, texMinY + halfHeight);

        //draw bottom-left quadrant
        renderTexturedQuad(renderStartX, renderStartY + halfRenderHeight, renderStartX + halfRenderWidth, renderStartY + halfRenderHeight * 2, texMinX, texMaxY - halfHeight, texMinX + halfWidth, texMaxY);

        //draw bottom-right quadrant
        renderTexturedQuad(renderStartX + halfRenderWidth, renderStartY + halfRenderHeight, renderStartX + halfRenderWidth * 2, renderStartY + halfRenderHeight * 2, texMaxX - halfWidth, texMaxY - halfHeight, texMaxX, texMaxY);
    }

    public static void renderTexturedQuad(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y1);
        GlStateManager.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y2);
        GlStateManager.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y2);
        GlStateManager.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y1);
        GlStateManager.glEnd();
    }

    /*
     * render a BB as a set of enlarged cuboids.
     */
    public static void drawOutlinedBoundingBox2(AxisAlignedBB bb, float r, float g, float b, float width) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, 0.4F);
        GlStateManager.bindTexture(0);
        float hw = width / 2;
        drawCuboid((float) bb.minX, (float) bb.minY - hw, (float) bb.minZ - hw, (float) bb.maxX, (float) bb.minY + hw, (float) bb.minZ + hw);
        drawCuboid((float) bb.minX, (float) bb.maxY - hw, (float) bb.minZ - hw, (float) bb.maxX, (float) bb.maxY + hw, (float) bb.minZ + hw);
        drawCuboid((float) bb.minX, (float) bb.minY - hw, (float) bb.maxZ - hw, (float) bb.maxX, (float) bb.minY + hw, (float) bb.maxZ + hw);
        drawCuboid((float) bb.minX, (float) bb.maxY - hw, (float) bb.maxZ - hw, (float) bb.maxX, (float) bb.maxY + hw, (float) bb.maxZ + hw);

        drawCuboid((float) bb.minX - hw, (float) bb.minY, (float) bb.minZ - hw, (float) bb.minX + hw, (float) bb.maxY, (float) bb.minZ + hw);
        drawCuboid((float) bb.maxX - hw, (float) bb.minY, (float) bb.minZ - hw, (float) bb.maxX + hw, (float) bb.maxY, (float) bb.minZ + hw);
        drawCuboid((float) bb.minX - hw, (float) bb.minY, (float) bb.maxZ - hw, (float) bb.minX + hw, (float) bb.maxY, (float) bb.maxZ + hw);
        drawCuboid((float) bb.maxX - hw, (float) bb.minY, (float) bb.maxZ - hw, (float) bb.maxX + hw, (float) bb.maxY, (float) bb.maxZ + hw);

        drawCuboid((float) bb.minX - hw, (float) bb.minY - hw, (float) bb.minZ, (float) bb.minX + hw, (float) bb.minY + hw, (float) bb.maxZ);
        drawCuboid((float) bb.minX - hw, (float) bb.maxY - hw, (float) bb.minZ, (float) bb.minX + hw, (float) bb.maxY + hw, (float) bb.maxZ);
        drawCuboid((float) bb.maxX - hw, (float) bb.minY - hw, (float) bb.minZ, (float) bb.maxX + hw, (float) bb.minY + hw, (float) bb.maxZ);
        drawCuboid((float) bb.maxX - hw, (float) bb.maxY - hw, (float) bb.minZ, (float) bb.maxX + hw, (float) bb.maxY + hw, (float) bb.maxZ);
        GlStateManager.disableBlend();
    }

    public static void drawCuboid(float x, float y, float z, float mx, float my, float mz) {
        GlStateManager.glBegin(GL11.GL_QUADS);
        //z+ side
        GlStateManager.glNormal3f(0, 0, 1);
        GlStateManager.glVertex3f(x, my, mz);
        GlStateManager.glVertex3f(x, y, mz);
        GlStateManager.glVertex3f(mx, y, mz);
        GlStateManager.glVertex3f(mx, my, mz);

        //x+ side
        GlStateManager.glNormal3f(1, 0, 0);
        GlStateManager.glVertex3f(mx, my, mz);
        GlStateManager.glVertex3f(mx, y, mz);
        GlStateManager.glVertex3f(mx, y, z);
        GlStateManager.glVertex3f(mx, my, z);

        //y+ side
        GlStateManager.glNormal3f(0, 1, 0);
        GlStateManager.glVertex3f(x, my, z);
        GlStateManager.glVertex3f(x, my, mz);
        GlStateManager.glVertex3f(mx, my, mz);
        GlStateManager.glVertex3f(mx, my, z);

        //z- side
        GlStateManager.glNormal3f(0, 0, -1);
        GlStateManager.glVertex3f(x, my, z);
        GlStateManager.glVertex3f(mx, my, z);
        GlStateManager.glVertex3f(mx, y, z);
        GlStateManager.glVertex3f(x, y, z);

        //x-side
        GlStateManager.glNormal3f(-1, 0, 0);
        GlStateManager.glVertex3f(x, y, mz);
        GlStateManager.glVertex3f(x, my, mz);
        GlStateManager.glVertex3f(x, my, z);
        GlStateManager.glVertex3f(x, y, z);

        //y- side
        GlStateManager.glNormal3f(0, -1, 0);
        GlStateManager.glVertex3f(x, y, z);
        GlStateManager.glVertex3f(mx, y, z);
        GlStateManager.glVertex3f(mx, y, mz);
        GlStateManager.glVertex3f(x, y, mz);

        GlStateManager.glEnd();
    }

    /*
     * Renders a white point in center, and RGB lines/points for X,Y,Z axis'
     */
    public static void renderOrientationPoints(float colorMult) {
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glPointSize(3.f);

        GlStateManager.color(colorMult, colorMult, colorMult, 1.f);

        //draw origin point
        GlStateManager.glBegin(GL11.GL_POINTS);
        GlStateManager.glVertex3f(0, 0, 0);
        GlStateManager.glEnd();


        GlStateManager.color(colorMult, 0, 0, 1.f);//red for x axis
        GlStateManager.glBegin(GL11.GL_LINES);
        GlStateManager.glVertex3f(0, 0, 0);
        GlStateManager.glVertex3f(1, 0, 0);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_POINTS);
        GlStateManager.glVertex3f(1, 0, 0);
        GlStateManager.glEnd();


        GlStateManager.color(0, colorMult, 0, 1.f);//green for y axis
        GlStateManager.glBegin(GL11.GL_LINES);
        GlStateManager.glVertex3f(0, 0, 0);
        GlStateManager.glVertex3f(0, 1, 0);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_POINTS);
        GlStateManager.glVertex3f(0, 1, 0);
        GlStateManager.glEnd();


        GlStateManager.color(0, 0, colorMult, 1.f);//blue for z axis
        GlStateManager.glBegin(GL11.GL_LINES);
        GlStateManager.glVertex3f(0, 0, 0);
        GlStateManager.glVertex3f(0, 0, 1);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_POINTS);
        GlStateManager.glVertex3f(0, 0, 1);
        GlStateManager.glEnd();

        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    /*
     * draw a player-position-normalized bounding box (can only be called during worldRender)
     */
    public static void drawOutlinedBoundingBox(AxisAlignedBB bb, float r, float g, float b) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, 0.4F);
        GlStateManager.glLineWidth(8.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tess.getBuffer();
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        tess.draw();
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tess.draw();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /*
     * @param bb
     * @param player
     * @param partialTick
     * @return
     */
    public static AxisAlignedBB adjustBBForPlayerPos(AxisAlignedBB bb, EntityPlayer player, float partialTick) {
        double x = getRenderOffsetX(player, partialTick);
        double y = getRenderOffsetY(player, partialTick);
        double z = getRenderOffsetZ(player, partialTick);
        return bb.offset(-x, -y, -z);
    }

    public static double getRenderOffsetX(EntityPlayer player, float partialTick) {
        return player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
    }

    public static double getRenderOffsetY(EntityPlayer player, float partialTick) {
        return player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
    }

    public static double getRenderOffsetZ(EntityPlayer player, float partialTick) {
        return player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
    }

}
