package net.synthrose.artofalchemy.item;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.synthrose.artofalchemy.essentia.Essentia;
import net.synthrose.artofalchemy.essentia.EssentiaContainer;
import net.synthrose.artofalchemy.essentia.HasEssentia;

public class ItemEssentiaVessel extends Item {
	
	public static final int DEFAULT_MULTI_CAPACITY = 4000;
	public static final int DEFAULT_SINGLE_CAPACITY = 4000;
	
	public final Essentia TYPE;
	private String translationKey;
	
	public ItemEssentiaVessel(Settings settings, Essentia type) {
		super(settings.maxCount(1));
		TYPE = type;
	}
	
	public ItemEssentiaVessel(Settings settings) {
		this(settings, null);
	}
	
	public boolean isEmpty() {
		return false;
	}
	
	public EssentiaContainer getContainer(ItemStack stack) {
		EssentiaContainer container = EssentiaContainer.of(stack);
		if (container == null) {
			container = new EssentiaContainer().setCapacity(DEFAULT_SINGLE_CAPACITY);
			if (TYPE != null) {
				container.whitelist(TYPE).setWhitelistEnabled(true);
				container.setCapacity(DEFAULT_MULTI_CAPACITY);
			}
		}
		return container;
	}
	
	public void setContainer(ItemStack stack, EssentiaContainer container) {
		if (TYPE != null) {
			container.setWhitelist(new HashSet<Essentia>()).whitelist(TYPE).setWhitelistEnabled(true);
		}
		container.in(stack);
	}
	
	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		EssentiaContainer container = getContainer(stack);
		setContainer(stack, container);
		super.onCraft(stack, world, player);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		EssentiaContainer container = getContainer(ctx.getStack());
		BlockEntity be = ctx.getWorld().getBlockEntity(ctx.getBlockPos());
		if (be != null && be instanceof HasEssentia) {
			
			HasEssentia target = (HasEssentia) be;
			int transferred = 0;
			for (int i = 0; i < target.getNumContainers() && transferred == 0; i++) {
				EssentiaContainer other = target.getContainer(i);
				int pulled = container.pullContents(other).getCount();
				transferred += pulled;
			}
			for (int i = 0; i < target.getNumContainers() && transferred == 0; i++) {
				EssentiaContainer other = target.getContainer(i);
				int pushed = container.pushContents(other).getCount();
				transferred -= pushed;
			}
			container.in(ctx.getStack());
			
			if (transferred > 0) {
				ctx.getPlayer().addMessage(new TranslatableText(tooltipPrefix() + "pulled", +transferred), true);
			} else if (transferred < 0) {
				ctx.getPlayer().addMessage(new TranslatableText(tooltipPrefix() + "pushed", -transferred), true);
			} else {
				return ActionResult.PASS;
			}
			be.markDirty();
			return ActionResult.SUCCESS;
			
		} else {
			return ActionResult.PASS;
		}
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			ItemStack stack = user.getStackInHand(hand);
			EssentiaContainer container = getContainer(stack);
			if (container.isInput() && container.isOutput()) {
				user.addMessage(new TranslatableText(tooltipPrefix() + "input"), true);
				container.setInput(true);
				container.setOutput(false);
			} else if (container.isInput() && !container.isOutput()) {
				user.addMessage(new TranslatableText(tooltipPrefix() + "output"), true);
				container.setInput(false);
				container.setOutput(true);
			} else if (!container.isInput() && container.isOutput()){
				user.addMessage(new TranslatableText(tooltipPrefix() + "locked"), true);
				container.setInput(false);
				container.setOutput(false);
			} else {
				user.addMessage(new TranslatableText(tooltipPrefix() + "unlocked"), true);
				container.setInput(true);
				container.setOutput(true);
			}
			container.in(stack);
			return TypedActionResult.consume(stack);
		}
		return super.use(world, user, hand);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
		
		if (world == null) {
			return;
		}
		
		EssentiaContainer container = getContainer(stack);
		String prefix = tooltipPrefix();
		
		if (container.isInfinite()) {
			tooltip.add(new TranslatableText(prefix + "infinite").formatted(Formatting.LIGHT_PURPLE));
			if (container.isWhitelistEnabled()) {
				if (container.getWhitelist().isEmpty()) {
					tooltip.add(new TranslatableText(prefix + "empty").formatted(Formatting.GRAY));
				} else {
					for (Essentia essentia : container.getWhitelist()) {
						tooltip.add(new TranslatableText(prefix + "component_inf",
							essentia.getName()).formatted(Formatting.GOLD));
					}
				}
			}
			
		} else if (container.hasUnlimitedCapacity()){
			if (container.isWhitelistEnabled() && container.getWhitelist().size() == 1) {
				for (Essentia essentia : container.getWhitelist()) {
					tooltip.add(new TranslatableText(prefix + "single_unlim",
						essentia.getName(), container.getCount(essentia)).formatted(Formatting.GREEN));
				}
			} else if (container.isWhitelistEnabled() && container.getWhitelist().isEmpty()) {
				tooltip.add(new TranslatableText(prefix + "empty").formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText(prefix + "mixed_unlim", container.getCount()).formatted(Formatting.AQUA));
				for (Essentia essentia : container.getContents().sortedList()) {
					if (container.getCount(essentia) != 0 && container.whitelisted(essentia)) {
						tooltip.add(new TranslatableText(prefix + "component",
								essentia.getName(), container.getCount(essentia)).formatted(Formatting.GOLD));
					}
				}
			}
			
		} else {
			if (container.isWhitelistEnabled() && container.getWhitelist().size() == 1) {
				for (Essentia essentia : container.getWhitelist()) {
					tooltip.add(new TranslatableText(prefix + "single", essentia.getName(),
						container.getCount(essentia), container.getCapacity()).formatted(Formatting.GREEN));
				}
			} else if (container.isWhitelistEnabled() && container.getWhitelist().isEmpty()) {
				tooltip.add(new TranslatableText(prefix + "empty").formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText(prefix + "mixed", container.getCount(),
					container.getCapacity()).formatted(Formatting.AQUA));
				for (Essentia essentia : container.getContents().sortedList()) {
					if (container.getCount(essentia) != 0 && container.whitelisted(essentia)) {
						tooltip.add(new TranslatableText(prefix + "component",
							essentia.getName(), container.getCount(essentia)).formatted(Formatting.GOLD));
					}
				}
			}
		}
		
		if (!container.isInput() && !container.isOutput()) {
			tooltip.add(new TranslatableText(prefix + "locked").formatted(Formatting.RED));
		} else if (!container.isInput()) {
			tooltip.add(new TranslatableText(prefix + "output").formatted(Formatting.RED));
		} else if (!container.isOutput()) {
			tooltip.add(new TranslatableText(prefix + "input").formatted(Formatting.RED));
		}
		
	}
	
	@Override
	protected String getOrCreateTranslationKey() {
		if (translationKey == null) {
			translationKey = Util.createTranslationKey("item",
				Registry.ITEM.getId(AoAItems.ESSENTIA_VESSELS.get(null)));
		}

		return this.translationKey;
	}
	
	@Override
	public String getTranslationKey() {
		return getOrCreateTranslationKey();
	}
	
	private String tooltipPrefix() {
		return getTranslationKey() + ".tooltip_";
	}

}