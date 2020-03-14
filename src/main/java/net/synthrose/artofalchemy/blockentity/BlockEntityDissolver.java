package net.synthrose.artofalchemy.blockentity;

import java.util.HashMap;
import java.util.Map;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.synthrose.artofalchemy.EssentiaSerializer;
import net.synthrose.artofalchemy.EssentiaType;
import net.synthrose.artofalchemy.ImplementedInventory;
import net.synthrose.artofalchemy.block.BlockDissolver;
import net.synthrose.artofalchemy.recipe.RecipeDissolution;
import net.synthrose.artofalchemy.recipe.AoARecipes;

public class BlockEntityDissolver extends BlockEntity
	implements ImplementedInventory, Tickable, PropertyDelegateHolder, BlockEntityClientSerializable {
	
	private int OPERATION_TIME = 100;
	private int TANK_SIZE = 4000;
	
	private int alkahest = 0;
	private int maxAlkahest = TANK_SIZE;
	private int progress = 0;
	private int maxProgress = OPERATION_TIME;
	private boolean lit = false;
	private Map<EssentiaType, Integer> essentia = new HashMap<>();
	
	protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
	protected final PropertyDelegate delegate = new PropertyDelegate() {
		
		@Override
		public int size() {
			return 4;
		}
		
		@Override
		public void set(int index, int value) {
			switch(index) {
			case 0:
				alkahest = value;
				break;
			case 1:
				maxAlkahest = value;
				break;
			case 2:
				progress = value;
				break;
			case 3:
				maxProgress = value;
				break;
			}
		}
		
		@Override
		public int get(int index) {
			switch(index) {
			case 0:
				return alkahest;
			case 1:
				return maxAlkahest;
			case 2:
				return progress;
			case 3:
				return maxProgress;
			default:
				return 0;
			}
		}
		
	};
	
	public BlockEntityDissolver() {
		super(AoABlockEntities.DISSOLVER);
	}
	
	public boolean hasAlkahest() {
		return alkahest > 0;
	}
	
	public int getAlkahest() {
		return alkahest;
	}
	
	public Map<EssentiaType, Integer> getEssentia() {
		return essentia;
	}
	
	public int getEssentia(EssentiaType type) {
		return essentia.getOrDefault(type, 0);
	}
	
	public int getTotalEssentia() {
		int sum = 0;
		for (int amount : essentia.values()) {
			sum += amount;
		}
		return sum;
	}
	
	public boolean setAlkahest(int amount) {
		if (amount >= 0 && amount <= maxAlkahest) {
			alkahest = amount;
			world.setBlockState(pos, world.getBlockState(pos).with(BlockDissolver.FILLED, alkahest > 0));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addAlkahest(int amount) {
		return setAlkahest(alkahest + amount);
	}
	
	private boolean canCraft(RecipeDissolution recipe) {
		ItemStack inSlot = items.get(0);
		
		if (recipe == null || inSlot.isEmpty()) {
			return false;
		} else {
			Map<EssentiaType, Integer> results = recipe.getEssentia();
			int totalEssentia = 0;
			
			if (inSlot.isDamageable()) {
				double multiplier = 1.0 - inSlot.getDamage() / inSlot.getMaxDamage();
				for (EssentiaType type : results.keySet()) {
					results.put(type, (int) (multiplier * results.get(type)));
				}
			}
			
			for (int amount : results.values()) {
				totalEssentia += amount;
			}
			
			return (totalEssentia <= alkahest && totalEssentia + getTotalEssentia() <= TANK_SIZE);
		}
	}
	
	// Be sure to check canCraft() first!
	private void doCraft(RecipeDissolution recipe) {
		ItemStack inSlot = items.get(0);
		Map<EssentiaType, Integer> results = recipe.getEssentia();
		int totalEssentia = 0;
		
		if (inSlot.isDamageable()) {
			double multiplier = 1.0 - inSlot.getDamage() / inSlot.getMaxDamage();
			for (EssentiaType type : results.keySet()) {
				results.put(type, (int) (multiplier * results.get(type)));
			}
		}
		
		for (int amount : results.values()) {
			totalEssentia += amount;
		}
		
		for (EssentiaType type : results.keySet()) {
			if (essentia.containsKey(type)) {
				essentia.put(type, essentia.get(type) + results.get(type));
			} else {
				essentia.put(type, results.get(type));
			}
		}
		
		alkahest -= totalEssentia;
		inSlot.decrement(1);
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("alkahest", alkahest);
		tag.putInt("progress", progress);
		tag.put("essentia", EssentiaSerializer.mapToTag(essentia));
		Inventories.toTag(tag, items);
		return super.toTag(tag);
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		Inventories.fromTag(tag, items);
		alkahest = tag.getInt("alkahest");
		progress = tag.getInt("progress");
		essentia = EssentiaSerializer.tagToMap(tag.getCompound("essentia"));
		maxAlkahest = TANK_SIZE;
		maxProgress = OPERATION_TIME;
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return items;
	}
	
	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}
	

	@Override
	public void tick() {
		boolean dirty = false;
		
		if (!world.isClient()) {
			ItemStack inSlot = items.get(0);
			boolean canWork = false;
			
			if (!inSlot.isEmpty() && hasAlkahest()) {
				RecipeDissolution recipe = world.getRecipeManager()
						.getFirstMatch(AoARecipes.DISSOLUTION, this, world).orElse(null);
				canWork = canCraft(recipe);
			
				if (canWork) {
					if (progress < maxProgress) {
						if (!lit) {
							world.setBlockState(pos, world.getBlockState(pos).with(BlockDissolver.LIT, true));
							lit = true;
						}
						progress++;
					}
					if (progress >= maxProgress) {
						progress -= maxProgress;
						doCraft(recipe);
						if (alkahest <= 0) {
							world.setBlockState(pos, world.getBlockState(pos).with(BlockDissolver.FILLED, false));
						}
						sync();
					}
				}
			}
			
			if (!canWork) {
				if (progress != 0) {
					progress = 0;
				}
				if (lit) {
					lit = false;
					world.setBlockState(pos, world.getBlockState(pos).with(BlockDissolver.LIT, false));
				}
			}
		}
		
		if (dirty) {
			markDirty();
		}
	}

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return delegate;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}
	
}
