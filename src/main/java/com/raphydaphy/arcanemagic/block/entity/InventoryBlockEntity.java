package com.raphydaphy.arcanemagic.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class InventoryBlockEntity extends BlockEntity implements Inventory
{
	private DefaultedList<ItemStack> contents;
	private final int size;

	public InventoryBlockEntity(BlockEntityType<?> type, int size)
	{
		super(type);

		this.contents = DefaultedList.create(size, ItemStack.EMPTY);
		this.size = size;
	}

	@Override
	public void fromTag(CompoundTag tag)
	{
		super.fromTag(tag);
		contents = DefaultedList.create(size, ItemStack.EMPTY);
		InventoryUtil.deserialize(tag, contents);
	}

	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);
		writeContents(tag);
		return tag;
	}

	public void writeContents(CompoundTag tag)
	{
		InventoryUtil.serialize(tag, contents);
	}

	@Override
	public int getInvSize()
	{
		return size;
	}

	@Override
	public boolean isInvEmpty()
	{
		for (ItemStack stack : contents)
		{
			if (!stack.isEmpty())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getInvStack(int slot)
	{
		return contents.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int count)
	{
		ItemStack stack = InventoryUtil.splitStack(contents, slot, count);
		markDirty();

		return stack;
	}

	@Override
	public ItemStack removeInvStack(int slot)
	{
		ItemStack ret = InventoryUtil.removeStack(contents, slot);
		markDirty();
		return ret;
	}

	@Override
	public void setInvStack(int slot, ItemStack stack)
	{
		contents.set(slot, stack);
		markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity var1)
	{
		return false;
	}

	@Override
	public void clear()
	{
		contents.clear();
		markDirty();
	}

	public static int getComparatorOutput(World world, BlockPos pos)
	{
		BlockEntity entity = world.getBlockEntity(pos);

		if (entity instanceof Inventory)
		{
			Inventory inventory = (Inventory) entity;
			int count = 0;
			float percent = 0.0F;

			for (int slot = 0; slot < inventory.getInvSize(); ++slot)
			{
				ItemStack stack = inventory.getInvStack(slot);
				if (!stack.isEmpty())
				{
					percent += (float) stack.getAmount() / (float) Math.min(inventory.getInvMaxStackAmount(), stack.getMaxAmount());
					++count;
				}
			}

			percent /= (float) inventory.getInvSize();
			return MathHelper.floor(percent * 14.0F) + (count > 0 ? 1 : 0);
		}
		return 0;
	}
}
