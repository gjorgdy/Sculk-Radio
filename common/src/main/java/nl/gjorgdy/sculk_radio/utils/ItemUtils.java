package nl.gjorgdy.sculk_radio.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import java.util.Optional;

public abstract class ItemUtils {

	private static final String frequency_key = "frequency";

	public static void setFrequency(ItemStack itemStack, int frequency) {
		// Store the frequency in the item's custom data
		var compoundTag = getTag(itemStack);
		compoundTag.putInt(frequency_key, frequency);
		saveTag(itemStack, compoundTag);
		// Set the lore of the item
		var lore = itemStack.getComponents().getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
		var line = Component.literal(Integer.toHexString(frequency).toUpperCase())
			.withStyle(Style.EMPTY.withItalic(false))
			.withColor(0x767676);
		itemStack.set(DataComponents.LORE, lore.withLineAdded(line));
	}

	public static Optional<Integer> getFrequency(ItemStack itemStack) {
		var compoundTag = getTag(itemStack);
		return compoundTag.getInt(frequency_key);
	}

	private static CompoundTag getTag(ItemStack itemStack) {
		return itemStack.getComponents()
			.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
			.copyTag();
	}

	private static void saveTag(ItemStack itemStack, CompoundTag compoundTag) {
		itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
	}

}
