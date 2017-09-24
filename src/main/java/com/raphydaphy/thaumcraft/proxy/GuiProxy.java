package com.raphydaphy.thaumcraft.proxy;

import com.raphydaphy.thaumcraft.block.BlockArcaneWorktable;
import com.raphydaphy.thaumcraft.container.ContainerArcaneWorktable;
import com.raphydaphy.thaumcraft.gui.GuiArcaneWorktable;
import com.raphydaphy.thaumcraft.gui.GuiThaumonomicon;
import com.raphydaphy.thaumcraft.init.ModItems;
import com.raphydaphy.thaumcraft.item.ItemThaumonomicon;
import com.raphydaphy.thaumcraft.tileentity.TileEntityArcaneWorktable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
		case BlockArcaneWorktable.GUI_ID:
			TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
			if (te instanceof TileEntityArcaneWorktable)
				return new ContainerArcaneWorktable(player.inventory, (TileEntityArcaneWorktable) te);
			break;

		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
		case ItemThaumonomicon.GUI_ID:
			if (player.getHeldItemMainhand().getItem() == ModItems.thaumonomicon
					|| player.getHeldItemOffhand().getItem() == ModItems.thaumonomicon)
			{
				return new GuiThaumonomicon(player);
			}
			break;
		case BlockArcaneWorktable.GUI_ID:
			BlockPos pos = new BlockPos(x, y, z);
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityArcaneWorktable)
			{
				TileEntityArcaneWorktable containerTileEntity = (TileEntityArcaneWorktable) te;
				return new GuiArcaneWorktable(containerTileEntity,
						new ContainerArcaneWorktable(player.inventory, containerTileEntity));
			}
		}
		return null;
	}
}