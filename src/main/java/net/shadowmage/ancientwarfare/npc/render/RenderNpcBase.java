package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderNpcBase extends RenderBiped<NpcBase> {

    private boolean isSleeping;

    List<Integer> renderTasks = new ArrayList<>();

    public RenderNpcBase(RenderManager renderManager) {
        super(renderManager, new ModelNpc(), 0.6f);
        addLayer(new LayerBipedArmor(this));
        addLayer(new LayerHeldItem(this));
    }

    @Override
    protected void applyRotations(NpcBase npc, float parFloat1, float parFloat2, float parFloat3) {
        isSleeping = npc.getSleeping();
        if (isSleeping) {
            float bedDirection = npc.getBedOrientationInDegrees();
            if (bedDirection != -1) {
                GlStateManager.rotate(bedDirection, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(this.getDeathMaxRotation(npc), 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                return;
            }
            isSleeping = false;
        }
        super.applyRotations(npc, parFloat1, parFloat2, parFloat3);
    }

    @Override
    public void doRender(NpcBase npc, double x, double y, double z, float par8, float par9) {
        isSleeping = npc.getSleeping();
        if (isSleeping) {
            // render the body a bit offset because we're manually shifting the bounding box
            double xOffset = x;
            double zOffset = z;
            switch (npc.getBedDirection()) {
                case SOUTH:
                    zOffset -= 0.9f;
                    break;
                case WEST:
                    xOffset += 0.9f;
                    break;
                case NORTH:
                    zOffset += 0.9f;
                    break;
                case EAST:
                    xOffset -= 0.9f;
                    break;
            }
            super.doRender(npc, xOffset, y, zOffset, par8, par9);
        } else {
            super.doRender(npc, x, y, z, par8, par9);
        }

        if (isSleeping)
            y -= 1.5f;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (npc.isHostileTowards(player)) {
            if (AWNPCStatics.renderHostileNames.getBoolean()) {
                String name = getNameForRender(npc, true);
                if (AWNPCStatics.renderTeamColors.getBoolean()) {
                    ScorePlayerTeam playerTeam = player.world.getScoreboard().getTeam(player.getName());
                    ScorePlayerTeam npcTeam = (ScorePlayerTeam) npc.getTeam();
                    if (npcTeam != null && npcTeam != playerTeam) {
                        name = npcTeam.getPrefix() + name + npcTeam.getSuffix();
                    }
                }
                renderColoredLabel(npc, name, x, y, z, 64, 0x20ff0000, 0xffff0000);
            }
        } else {
            boolean canBeCommandedBy = npc.hasCommandPermissions(player.getName());
            if (AWNPCStatics.renderFriendlyNames.getBoolean()) {
                String name = getNameForRender(npc, false);
                if (AWNPCStatics.renderTeamColors.getBoolean()) {
                    ScorePlayerTeam playerTeam = player.world.getScoreboard().getTeam(player.getName());
                    ScorePlayerTeam npcTeam = (ScorePlayerTeam) npc.getTeam();
                    if (npcTeam != null && npcTeam != playerTeam) {
                        name = npcTeam.getPrefix() + name + npcTeam.getSuffix();
                    }
                } else if (!canBeCommandedBy) {
                    name = TextFormatting.DARK_GRAY.toString() + name;
                }
                renderColoredLabel(npc, name, x, y, z, 64, 0x20ffffff, 0xffffffff);
            }
            if (canBeCommandedBy) {
                if (AWNPCStatics.renderAI.getBoolean()) {
                    renderNpcAITasks(npc, x, y, z, 64);
                }
            }
        }
    }

    @Override
    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    protected boolean canRenderName(NpcBase par1EntityLivingBase) {
        return false;
    }

    @Override
    protected void renderLivingLabel(NpcBase entityIn, String str, double x, double y, double z, int maxDistance) {
        //noop to disable vanilla nameplate rendering, custom label rendering handled through custom rendering
    }

    private String getNameForRender(NpcBase npc, boolean hostile) {
        String customName = npc.hasCustomName() ? npc.getCustomNameTag() : npc.getName();
        boolean addHealth = (hostile && AWNPCStatics.renderHostileHealth.getBoolean()) || (!hostile && AWNPCStatics.renderFriendlyHealth.getBoolean());
        if (addHealth) {
            customName += " " + getHealthForRender(npc);
        }
        return customName;
    }

    private String getHealthForRender(NpcBase npc) {
        return String.format("%.1f", npc.getHealth());
    }

    private void renderNpcAITasks(NpcBase entity, double x, double y, double z, int renderDistance) {
        double d3 = entity.getDistanceSqToEntity(this.renderManager.renderViewEntity);

        if (d3 <= (double) (renderDistance * renderDistance) && entity.canEntityBeSeen(renderManager.renderViewEntity)) {
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.0F, (float) y + entity.height + 0.5F, (float) z);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int tasks = entity.getAITasks();
            int mask;
            String icon;

            for (int i = 0; i < NpcAI.NUMBER_OF_TASKS; i++) {
                mask = 1 << i;
                if ((tasks & mask) != 0) {
                    renderTasks.add(mask);
                }
            }

            int offset = (renderTasks.size() * 10 / 2);
            int startX = -offset;

            for (int i = 0; i < renderTasks.size(); i++) {
                icon = getIconFor(renderTasks.get(i));
                renderIcon(icon, 16, 16, startX + i * 20, -16);
            }
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            this.renderTasks.clear();
        }
    }

    private void renderColoredLabel(NpcBase entity, String string, double x, double y, double z, int renderDistance, int color1, int color2) {
        double d3 = entity.getDistanceSqToEntity(this.renderManager.renderViewEntity);

        if (d3 <= (double) (renderDistance * renderDistance) && entity.canEntityBeSeen(renderManager.renderViewEntity)) {
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.0F, (float) y + entity.height + 0.5F, (float) z);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();

            GlStateManager.disableTexture2D();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            int j = fontrenderer.getStringWidth(string) / 2;
            bufferBuilder.pos((double) (-j - 1), (double) (-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            bufferBuilder.pos((double) (-j - 1), (double) (8), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            bufferBuilder.pos((double) (j + 1), (double) (8), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            bufferBuilder.pos((double) (j + 1), (double) (-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(string, -fontrenderer.getStringWidth(string) / 2, 0, color1);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(string, -fontrenderer.getStringWidth(string) / 2, 0, color2);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(NpcBase npc) {
        return npc.getTexture();
    }

    private void renderIcon(String tex, int width, int height, int x, int y) {
        Tessellator tess = Tessellator.getInstance();
        AWTextureManager.instance().bindLocationTexture(tex);
        int halfW = width / 2;
        int halfH = height / 2;
        BufferBuilder bufferBuilder = tess.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(x - halfW, y - halfH, 0).tex(0, 0).endVertex();
        bufferBuilder.pos(x - halfW, y + halfH, 0).tex(0, 1).endVertex();
        bufferBuilder.pos(x + halfW, y + halfH, 0).tex(1, 1).endVertex();
        bufferBuilder.pos(x + halfW, y + -halfH, 0).tex(1, 0).endVertex();
        tess.draw();
    }

    private String getIconFor(int task) {
        switch (task) {
            case 0:
                return null;
            case NpcAI.TASK_ATTACK:
                return "ancientwarfare:textures/entity/npc/ai/task_attack.png";
            case NpcAI.TASK_UPKEEP:
                return "ancientwarfare:textures/entity/npc/ai/task_upkeep.png";
            case NpcAI.TASK_IDLE_HUNGRY:
                return "ancientwarfare:textures/entity/npc/ai/task_upkeep2.png";
            case NpcAI.TASK_GO_HOME:
                return "ancientwarfare:textures/entity/npc/ai/task_home.png";
            case NpcAI.TASK_WORK:
                return "ancientwarfare:textures/entity/npc/ai/task_work.png";
            case NpcAI.TASK_PATROL:
                return "ancientwarfare:textures/entity/npc/ai/task_patrol.png";
            case NpcAI.TASK_GUARD:
                return "ancientwarfare:textures/entity/npc/ai/task_guard.png";
            case NpcAI.TASK_FOLLOW:
                return "ancientwarfare:textures/entity/npc/ai/task_follow.png";
            case NpcAI.TASK_WANDER:
                return "ancientwarfare:textures/entity/npc/ai/task_wander.png";
            case NpcAI.TASK_MOVE:
                return "ancientwarfare:textures/entity/npc/ai/task_move.png";
            case NpcAI.TASK_ALARM:
                return "ancientwarfare:textures/entity/npc/ai/task_alarm.png";
            case NpcAI.TASK_FLEE:
                return "ancientwarfare:textures/entity/npc/ai/task_flee.png";
            case NpcAI.TASK_SLEEP:
                return "minecraft:textures/items/bed.png";
            case NpcAI.TASK_RAIN:
                return "ancientwarfare:textures/entity/npc/ai/task_rain.png";
            default:
                return null;
        }
    }

}
