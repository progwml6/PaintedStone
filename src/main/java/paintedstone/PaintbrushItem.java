package paintedstone;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PaintbrushItem extends Item
{
    public static final int maxPaint = 1728;

    public PaintbrushItem()
    {
        super();
        this.setCreativeTab(PaintedStone.tab);
        this.maxStackSize = 1;
        this.setMaxDamage(maxPaint);
    }

    @Override
    public boolean onEntitySwing (EntityLivingBase entity, ItemStack stack)
    {
        MovingObjectPosition mop = this.raytraceFromPlayer(entity.worldObj, entity, false);

        if (mop != null)
        {
            int xPos = mop.blockX;
            int yPos = mop.blockY;
            int zPos = mop.blockZ;
            ForgeDirection sideHit = ForgeDirection.getOrientation(mop.sideHit);
            colorBlocks(stack, entity, entity.worldObj, xPos, yPos, zPos, 0);
        }
        return false;
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return colorBlocks(stack, player, world, x, y, z, 1);
    }

    private boolean colorBlocks (ItemStack stack, EntityLivingBase player, World world, int x, int y, int z, int paintRadius)
    {
        int damage = stack.getItemDamage();
        if (stack.hasTagCompound())
        {
            int type = stack.getTagCompound().getInteger("PaintType");
            if (type != 0)
            {
                int max = maxPaint - damage;
                int amount = PaintedStone.instance.colorStoneBlocks(world, x, y, z, type - 1, paintRadius, max);
                if (amount > 0)
                {
                    if (!player.worldObj.isRemote)
                    {
                        Block block = Blocks.stone;
                        player.worldObj.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block.stepSound.getBreakSound(),
                                (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                    }
                    int total = amount + damage;
                    if (total >= maxPaint)
                        resetItem(stack);
                    else
                        stack.damageItem(amount, player);
                    return true;
                }
            }
        }
        return false;
    }

    public MovingObjectPosition raytraceFromPlayer (World world, EntityLivingBase entity, boolean par3)
    {
        float f = 1.0F;
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * f;
        float f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * f;
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) f;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) f + 1.62D - (double) entity.yOffset;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D; //Max range
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return world.rayTraceBlocks_do_do(vec3, vec31, par3, !par3);
    }

    void resetItem (ItemStack stack)
    {
        stack.setItemDamage(0);
        stack.getTagCompound().setInteger("PaintType", 0);
    }

    /* Rendering */
    IIcon bristles;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("paintedstone:paintbrush");
        this.bristles = iconRegister.registerIcon("paintedstone:paintbristles");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass (int par1, int par2)
    {
        return par2 == 1 ? this.bristles : this.itemIcon;
    }

    public static final int[] dyeColors = new int[] { 0xFFFFFF, 0xdb7b3b, 0xd532ad, 0x699ce3, 0xb7ab29, 0x49bd3d, 0xcc7890, 0x5d5d5d, 0xa2a8a8, 0x50acbd, 0x9947de, 0x3650c4, 0x74482b, 0x46631c,
            0xd72b27, 0x202020 };

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack (ItemStack stack, int pass)
    {
        if (pass == 1)
        {
            if (stack.hasTagCompound())
            {
                int type = stack.getTagCompound().getInteger("PaintType");
                if (type != 0)
                {
                    return dyeColors[(type - 1)];
                }
            }
        }
        return 0xffffff;
    }

    @Override
    public String getItemStackDisplayName (ItemStack par1ItemStack)
    {
        String base = ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(par1ItemStack) + ".name")).trim();
        String color = getDyeName(par1ItemStack);
        if (!color.equals(""))
            color = " ("+StatCollector.translateToLocal(color)+")";
        return base + color;
    }

    ItemStack dyeType = new ItemStack(Items.dye);

    public String getDyeName (ItemStack stack)
    {
        String color = "";
        if (stack.hasTagCompound())
        {
            int type = stack.getTagCompound().getInteger("PaintType");
            if (type != 0)
            {
                dyeType.setItemDamage(16 - type);
                return dyeType.getUnlocalizedName()+".name";
            }
        }
        return color;
    }
}
