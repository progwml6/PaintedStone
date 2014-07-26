package paintedstone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import tconstruct.tools.TinkerTools;

@Mod(modid = "PaintedStone", name = "Painted Stone", version = "Dango", dependencies ="after:tconstruct")
public class PaintedStone
{
    /* Define blocks, items, crucial info */
    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        tconstruct = Loader.isModLoaded("tconstruct");
        tab = new TabPaintedStone("paintedstone");

        coloredCobble = new PaintedStoneBlock(Material.rock, 2.0f, "stone_cobble", "stone.cobble").setBlockName("paintedstone.cobble");
        GameRegistry.registerBlock(coloredCobble, PaintedStoneItem.class, "paintedstone.cobble");
        coloredStone = new PaintedStoneBlock(Material.rock, 1.5f, "stone_raw", "stone.raw", coloredCobble).setBlockName("paintedstone.raw");
        GameRegistry.registerBlock(coloredStone, PaintedStoneItem.class, "paintedstone.raw");
        coloredMossCobble = new PaintedStoneBlock(Material.rock, 2.0f, "stone_mosscobble", "stone.mosscobble").setBlockName("paintedstone.mosscobble");
        GameRegistry.registerBlock(coloredMossCobble, PaintedStoneItem.class, "paintedstone.mosscobble");
        coloredStoneBrick = new PaintedStoneBlock(Material.rock, 1.5f, "stone_brick", "stone.brick").setBlockName("paintedstone.brick");
        GameRegistry.registerBlock(coloredStoneBrick, PaintedStoneItem.class, "paintedstone.brick");
        coloredMossStoneBrick = new PaintedStoneBlock(Material.rock, 1.5f, "stone_mossbrick", "stone.mossbrick").setBlockName("paintedstone.mossbrick");
        GameRegistry.registerBlock(coloredMossStoneBrick, PaintedStoneItem.class, "paintedstone.mossbrick");
        coloredCrackedStoneBrick = new PaintedStoneBlock(Material.rock, 1.5f, "stone_crackedbrick", "stone.crackedbrick").setBlockName("paintedstone.crackedbrick");
        GameRegistry.registerBlock(coloredCrackedStoneBrick, PaintedStoneItem.class, "paintedstone.crackedbrick");
        coloredStoneRoad = new PaintedStoneBlock(Material.rock, 1.5f, "stone_road", "stone.road").setBlockName("paintedstone.road");
        GameRegistry.registerBlock(coloredStoneRoad, PaintedStoneItem.class, "paintedstone.road");
        coloredStoneFancyBrick = new PaintedStoneBlock(Material.rock, 1.5f, "stone_fancy", "stone.fancy").setBlockName("paintedstone.fancy");
        GameRegistry.registerBlock(coloredStoneFancyBrick, PaintedStoneItem.class, "paintedstone.fancy");
        coloredStoneSquareBrick = new PaintedStoneBlock(Material.rock, 1.5f, "stone_square", "stone.chiseled").setBlockName("paintedstone.chiseled");
        GameRegistry.registerBlock(coloredStoneSquareBrick, PaintedStoneItem.class, "paintedstone.chiseled");
        clayBrickSmall  = new PaintedStoneBlock(Material.rock, 2.0f, "clay_smallbrick", "clay.brick").setBlockName("paintedstone.clay.smallbrick");
        GameRegistry.registerBlock(clayBrickSmall, PaintedStoneItem.class, "paintedstone.clay.smallbrick");

        paintbrush = new PaintbrushItem().setUnlocalizedName("paintedstone.brush");
        GameRegistry.registerItem(paintbrush, "paintbrush");
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(paintbrush), "#", "|", '#', new ItemStack(Blocks.wool, 1, Short.MAX_VALUE), '|', "stickWood"));

        for (int i = 0; i < 16; i++)
        {
            FurnaceRecipes.smelting().func_151394_a(new ItemStack(coloredCobble,1,i ), new ItemStack(coloredStone, 1, i), 0.2F);
            GameRegistry.addRecipe(new ItemStack(coloredStoneBrick, 4, i), "##", "##", '#', new ItemStack(coloredStone, 1, i));
            OreDictionary.registerOre("stone", new ItemStack(coloredStone, 1, i));
            OreDictionary.registerOre("cobblestone", new ItemStack(coloredCobble, 1, i));
            for (int dyeAmount = 1; dyeAmount <= 8; dyeAmount++)
            {
                Object[] input = new Object[dyeAmount + 1];
                input[0] = paintbrush;
                String dyeType = "dye" + dyeTypes[i];
                switch (dyeAmount)
                {
                case 8:
                    input[8] = dyeType;
                case 7:
                    input[7] = dyeType;
                case 6:
                    input[6] = dyeType;
                case 5:
                    input[5] = dyeType;
                case 4:
                    input[4] = dyeType;
                case 3:
                    input[3] = dyeType;
                case 2:
                    input[2] = dyeType;
                case 1:
                    input[1] = dyeType;
                }
                GameRegistry.addRecipe(new PaintbrushRecipe(paintbrush, i + 1, input));
            }
        }
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        if (tconstruct)
        {
            Detailing chiseling = TConstructRegistry.getChiselDetailing();
            for (int i = 0; i < 16; i++)
            {
                chiseling.addDetailing(coloredStone, i, coloredStoneBrick, i, TinkerTools.chisel);
                chiseling.addDetailing(coloredStoneBrick, i, coloredStoneRoad, i, TinkerTools.chisel);
                chiseling.addDetailing(coloredStoneRoad, i, coloredStoneFancyBrick, i, TinkerTools.chisel);
                chiseling.addDetailing(coloredStoneFancyBrick, i, coloredStoneSquareBrick, i, TinkerTools.chisel);
            }
        }
    }

    public static final String[] dyeTypes = new String[] { "White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red",
            "Black" };

    public int colorStoneBlocks (World world, int x, int y, int z, int inputMeta, int range, int maxBlocks)
    {
        boolean changed = false;
        //System.out.println("Input: "+inputMeta);
        int amount = 0;
        for (int xPos = -range; xPos <= range; xPos++)
        {
            if (amount > maxBlocks)
                break;
            for (int yPos = -range; yPos <= range; yPos++)
            {
                if (amount > maxBlocks)
                    break;
                for (int zPos = -range; zPos <= range; zPos++)
                {
                    if (amount > maxBlocks)
                        break;
                    Block block = world.getBlock(x + xPos, y + yPos, z + zPos);
                    if (block == Blocks.stone)
                    {
                        amount++;
                        world.setBlock(x + xPos, y + yPos, z + zPos, coloredStone, inputMeta, 3);
                    }
                    else if (block == Blocks.cobblestone)
                    {
                        amount++;
                        world.setBlock(x + xPos, y + yPos, z + zPos, coloredCobble, inputMeta, 3);
                    }
                    else if (block == Blocks.mossy_cobblestone)
                    {
                        amount++;
                        world.setBlock(x + xPos, y + yPos, z + zPos, coloredMossCobble, inputMeta, 3);
                    }
                    else if (block == Blocks.stonebrick)
                    {
                        amount++;
                        int meta = world.getBlockMetadata(x + xPos, y + yPos, z + zPos);

                        if (meta == 0)
                            world.setBlock(x + xPos, y + yPos, z + zPos, coloredStoneBrick, inputMeta, 3);
                        else if (meta == 1)
                            world.setBlock(x + xPos, y + yPos, z + zPos, coloredMossStoneBrick, inputMeta, 3);
                        else if (meta == 2)
                            world.setBlock(x + xPos, y + yPos, z + zPos, coloredCrackedStoneBrick, inputMeta, 3);
                        else if (meta == 3)
                            world.setBlock(x + xPos, y + yPos, z + zPos, coloredStoneSquareBrick, inputMeta, 3);

                    }
                    else if (block == Blocks.hardened_clay)
                    {
                        amount++;
                        world.setBlock(x + xPos, y + yPos, z + zPos, Blocks.stained_hardened_clay, inputMeta, 3);
                    }
                    else if (block == Blocks.brick_block)
                    {
                        amount++;
                        world.setBlock(x + xPos, y + yPos, z + zPos, clayBrickSmall, inputMeta, 3);
                    }
                    else if (tconstruct && block == TinkerTools.multiBrickFancy)
                    {
                        int meta = world.getBlockMetadata(x + xPos, y + yPos, z + zPos);
                        if (meta == 14)
                        {
                            amount++;
                            world.setBlock(x + xPos, y + yPos, z + zPos, coloredStoneFancyBrick, inputMeta, 3);
                        }
                        else if (meta == 15)
                        {
                            amount++;
                            world.setBlock(x + xPos, y + yPos, z + zPos, coloredStoneRoad, inputMeta, 3);
                        }
                    }
                }
            }
        }
        return amount;
    }

    @Instance("PaintedStone")
    public static PaintedStone instance;
    public static CreativeTabs tab;
    private boolean tconstruct;

    public static Block coloredStone;
    public static Block coloredCobble;
    public static Block coloredMossCobble;
    public static Block coloredStoneBrick;
    public static Block coloredMossStoneBrick;
    public static Block coloredCrackedStoneBrick;
    public static Block coloredStoneRoad;
    public static Block coloredStoneFancyBrick;
    public static Block coloredStoneSquareBrick;
    public static Block clayBrickSmall;

    public static Item paintbrush;
}
