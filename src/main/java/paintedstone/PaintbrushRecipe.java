package paintedstone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class PaintbrushRecipe implements IRecipe
{
    private ItemStack output = null;
    private ArrayList input = new ArrayList();
    private int type;

    public PaintbrushRecipe(Block result, int type, Object... recipe)
    {
        this(new ItemStack(result), type, recipe);
    }

    public PaintbrushRecipe(Item result, int type, Object... recipe)
    {
        this(new ItemStack(result), type, recipe);
    }

    public PaintbrushRecipe(ItemStack result, int type, Object... recipe)
    {
        output = result.copy();
        this.type = type;
        for (Object in : recipe)
        {
            if (in instanceof ItemStack)
            {
                input.add(((ItemStack) in).copy());
            }
            else if (in instanceof Item)
            {
                input.add(new ItemStack((Item) in));
            }
            else if (in instanceof Block)
            {
                input.add(new ItemStack((Block) in));
            }
            else if (in instanceof String)
            {
                input.add(OreDictionary.getOres((String) in));
            }
            else
            {
                String ret = "Invalid shapeless ore recipe: ";
                for (Object tmp : recipe)
                {
                    ret += tmp + ", ";
                }
                ret += output;
                throw new RuntimeException(ret);
            }
        }
    }

    PaintbrushRecipe(ShapelessRecipes recipe, Map<ItemStack, String> replacements)
    {
        output = recipe.getRecipeOutput();

        for (ItemStack ingred : ((List<ItemStack>) recipe.recipeItems))
        {
            Object finalObj = ingred;
            for (Entry<ItemStack, String> replace : replacements.entrySet())
            {
                if (OreDictionary.itemMatches(replace.getKey(), ingred, false))
                {
                    finalObj = OreDictionary.getOres(replace.getValue());
                    break;
                }
            }
            input.add(finalObj);
        }
    }

    @Override
    public int getRecipeSize ()
    {
        return input.size();
    }

    @Override
    public ItemStack getRecipeOutput ()
    {
        return output;
    }

    @Override
    public ItemStack getCraftingResult (InventoryCrafting crafting)
    {
        ItemStack paintbrush = null;
        int amount = 0;
        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack stack = crafting.getStackInSlot(i);
            if (stack == null)
                continue;
            if (stack.getItem() instanceof PaintbrushItem)
                paintbrush = stack.copy();
            else
                amount++;
        }
        if (paintbrush == null || amount == 0)
            return null;
        if (!paintbrush.hasTagCompound())
            paintbrush.setTagCompound(new NBTTagCompound());
        paintbrush.getTagCompound().setInteger("PaintType", this.type);

        int paint = paintbrush.getItemDamage();
        if (paint == 0)
            paint = PaintbrushItem.maxPaint;
        paint -= amount * 27;
        if (paint < 0)
            paint = 0;
        paintbrush.setItemDamage(paint);

        return paintbrush;
    }

    @Override
    public boolean matches (InventoryCrafting var1, World world)
    {
        ArrayList required = new ArrayList(input);

        for (int x = 0; x < var1.getSizeInventory(); x++)
        {
            ItemStack slot = var1.getStackInSlot(x);

            if (slot != null)
            {
                boolean inRecipe = false;
                Iterator req = required.iterator();

                while (req.hasNext())
                {
                    boolean match = false;

                    Object next = req.next();

                    if (next instanceof ItemStack)
                    {
                        match = checkItemEquals((ItemStack) next, slot);
                    }
                    else if (next instanceof ArrayList)
                    {
                        for (ItemStack item : (ArrayList<ItemStack>) next)
                        {
                            match = match || checkItemEquals(item, slot);
                        }
                    }

                    if (match)
                    {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }

                if (!inRecipe)
                {
                    return false;
                }
            }
        }

        return required.isEmpty();
    }

    private boolean checkItemEquals (ItemStack target, ItemStack input)
    {
        if(target == null || input == null)
            return false;
        if (input.getItem() == PaintedStone.paintbrush)
        {
            return (!input.hasTagCompound() || input.getTagCompound().getInteger("PaintType") == 0 || input.getTagCompound().getInteger("PaintType") == type);
        }
        return (target.getItem() == input.getItem() && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage()));
    }

    /**
     * Returns the input for this recipe, any mod accessing this value should never
     * manipulate the values in this array as it will effect the recipe itself.
     * @return The recipes input vales.
     */
    public ArrayList getInput ()
    {
        return this.input;
    }
}
