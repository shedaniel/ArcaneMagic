package com.raphydaphy.arcanemagic.item;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.client.gui.GuiParchment;
import com.raphydaphy.arcanemagic.parchment.IParchment;
import com.raphydaphy.arcanemagic.parchment.ParchmentDrownedDiscovery;
import com.raphydaphy.arcanemagic.parchment.ParchmentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Objects;

public class ItemWrittenParchment extends Item
{
    public static final String PARCHMENT_KEY = "key.arcanemagic.parchment_type";

    private final boolean ancient;

    public ItemWrittenParchment(boolean ancient)
    {
        super(new Item.Builder().group(ItemGroup.MISC).maxStackSize(1));
        this.ancient = ancient;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return ancient;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking())
        {

            IParchment parchment = ParchmentRegistry.getParchment(stack);
            if (parchment != null && parchment.isAncient() == ancient)
            {
                if (world.isRemote)
                {
                    openGUI(stack, parchment);
                } else
                {
                    addUsage(player, stack);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            player.setHeldItem(hand, new ItemStack(ArcaneMagic.PARCHMENT, stack.getCount()));
            player.openContainer.detectAndSendChanges();
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity holder, int p_onUpdate_4_, boolean p_onUpdate_5_)
    {
        if (world.getTotalWorldTime() % 50 == 0 && !world.isRemote && holder instanceof EntityPlayer)
        {
            onUpdateServer(stack, world, (EntityPlayer) holder);
        }
    }

    private void onUpdateServer(ItemStack stack, World world, EntityPlayer player)
    {
        IParchment parchment = ParchmentRegistry.getParchment(stack);
        if (parchment instanceof ParchmentDrownedDiscovery)
        {
            // Used altar but haven't found wizard hut
            if (!Objects.requireNonNull(stack.getTagCompound()).getBoolean(ParchmentDrownedDiscovery.FOUND_WIZARD_HUT) && Objects.requireNonNull(stack.getTagCompound()).getBoolean(ParchmentDrownedDiscovery.ALTAR_USED))
            {
                // They have opened an ancient parchment
                if (((EntityPlayerMP) player).getStatFile().readStat(StatList.OBJECT_USE_STATS.addStat(ArcaneMagic.ANCIENT_PARCHMENT)) > 0)
                {
                    // Add found wizard hut tag
                    Objects.requireNonNull(stack.getTagCompound()).setBoolean(ParchmentDrownedDiscovery.FOUND_WIZARD_HUT, true);
                    player.openContainer.detectAndSendChanges();
                }
            }
        }
    }

    private void openGUI(ItemStack stack, IParchment parchment)
    {
        Minecraft.getMinecraft().displayGuiScreen(new GuiParchment(stack, parchment));
    }

    private void addUsage(EntityPlayer player, ItemStack stack)
    {
        StatisticsManagerServer stats = ((EntityPlayerMP) player).getStatFile();
        stats.increaseStat(player, StatList.OBJECT_USE_STATS.addStat(stack.getItem()), 1);
        stats.sendStats((EntityPlayerMP) player);
    }
}
