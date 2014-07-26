package paintedstone;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PaintedStoneBlock extends Block
{
    public static final String[] colorNames = new String[] { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "silver", "aqua", "purple", "blue", "brown", "green", "red",
            "black" };
    public final String textureName;
    public final String localName;
    public IIcon[] icons;

    public PaintedStoneBlock(Material material, float hardness, String texture, String name)
    {
        super(material);
        this.setHardness(hardness);
        this.textureName = texture;
        this.localName = name;
        this.setCreativeTab(PaintedStone.tab);
    }

    public PaintedStoneBlock(Material material, float hardness, String texture, String name, Block dropID)
    {
        this(material, hardness, texture, name);
    }

    @Override
    public String getUnlocalizedName ()
    {
        return "tile." + localName;
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }
    public Block blockDropped (int par1, Random par2Random, int par3)
    {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[colorNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("paintedstone:" + textureName + "_" + colorNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        return meta < icons.length ? icons[meta] : icons[0];
    }

    @Override
    public void getSubBlocks (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < icons.length; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
