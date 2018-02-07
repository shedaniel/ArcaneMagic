package com.raphydaphy.arcanemagic.client.render;

import com.raphydaphy.arcanemagic.common.tileentity.TileEntityPedestal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PedestalTESR extends TileEntitySpecialRenderer<TileEntityPedestal>
{
	@Override
	public void render(TileEntityPedestal te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		renderItem(te);

		GlStateManager.popMatrix();
	}

	private void renderItem(TileEntityPedestal te)
	{
		ItemStack stack = te.getStack();
		if (!stack.isEmpty())
		{
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(.5, 0.935, .5);
			GlStateManager.scale(.2f, .2f, .2f);
			GlStateManager.rotate(te.getFrameAge(), 0, 1, 0);
			GlStateManager.translate(0, Math.sin((Math.PI / 180) * (te.increaseFrameAge())) / 3.2 + 0.1, 0);
			if (te.getFrameAge() < 0 || te.getFrameAge() == Integer.MAX_VALUE)
			{
				te.setFrameAge(0);
			}
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
			GlStateManager.popMatrix();
		}
	}
}