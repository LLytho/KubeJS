package dev.latvian.kubejs;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.server.TagEventJS;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class KubeJSCore
{
	// Access Transformers //

	public static List<IFutureReloadListener> getReloadListeners(SimpleReloadableResourceManager manager)
	{
		return manager.reloadListeners;
	}

	public static List<IFutureReloadListener> getInitTaskQueue(SimpleReloadableResourceManager manager)
	{
		return manager.initTaskQueue;
	}

	public static Map<String, FallbackResourceManager> getNamespaceResourceManagers(SimpleReloadableResourceManager manager)
	{
		return manager.namespaceResourceManagers;
	}

	public static Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> getRecipes(RecipeManager manager)
	{
		return manager.recipes;
	}

	public static void setRecipes(RecipeManager manager, Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes)
	{
		manager.recipes = recipes;
	}

	public static void setLifetime(FireworkRocketEntity rocket, int lifeime)
	{
		rocket.lifetime = lifeime;
	}

	public static boolean isDestroyingBlock(PlayerInteractionManager manager)
	{
		return manager.isDestroyingBlock;
	}

	public static Map<String, INBT> getNBTTagMap(CompoundNBT nbt)
	{
		return nbt.tagMap;
	}

	public static void setNBTTagMap(CompoundNBT nbt, Map<String, INBT> map)
	{
		nbt.tagMap = map;
	}

	public static void setHardness(Block block, float hardness)
	{
		block.blockHardness = hardness;
	}

	public static void setResistance(Block block, float resistance)
	{
		block.blockResistance = resistance;
	}

	public static void setLightLevel(Block block, int lightLevel)
	{
		block.lightValue = lightLevel;
	}

	// Mixin Helpers //

	public static <T> void customTags(Map<ResourceLocation, Tag.Builder<Block>> blocks, Map<ResourceLocation, Tag.Builder<Item>> items, Map<ResourceLocation, Tag.Builder<Fluid>> fluids, Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes)
	{
		if (ServerJS.instance != null)
		{
			new TagEventJS<>("blocks", blocks, valueGetter(ForgeRegistries.BLOCKS, Blocks.AIR)).post();
			new TagEventJS<>("items", items, valueGetter(ForgeRegistries.ITEMS, Items.AIR)).post();
			new TagEventJS<>("fluids", fluids, valueGetter(ForgeRegistries.FLUIDS, Fluids.EMPTY)).post();
			new TagEventJS<>("entity_types", entityTypes, valueGetter(ForgeRegistries.ENTITIES, null)).post();
		}
	}

	private static <T extends IForgeRegistryEntry<T>> Function<ResourceLocation, Optional<T>> valueGetter(IForgeRegistry<T> registry, @Nullable T def)
	{
		return id -> {
			T value = registry.getValue(id);

			if (value != null && value != def)
			{
				return Optional.of(value);
			}

			return Optional.empty();
		};
	}

	public static void customRecipes(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap, IResourceManager resourceManager, IProfiler profiler)
	{
		ScriptType.SERVER.console.logger.info("Scanning recipes...");
		int count = 0;

		for (Map.Entry<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> entry : getRecipes(recipeManager).entrySet())
		{
			ScriptType.SERVER.console.logger.debug(entry.getKey().toString());

			for (IRecipe<?> recipe : entry.getValue().values())
			{
				ScriptType.SERVER.console.logger.debug("* " + recipe.getSerializer().getRegistryName() + " / " + recipe.getId());
				count++;
			}
		}

		ScriptType.SERVER.console.logger.info("Found " + count + " recipes");
	}
}