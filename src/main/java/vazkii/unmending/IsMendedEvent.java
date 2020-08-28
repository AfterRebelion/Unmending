package vazkii.unmending;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

public class IsMendedEvent {
	static void getTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> components) {
		TranslatableText itemGotModified = new TranslatableText("unmending.repaired");
		itemGotModified.formatted(Formatting.YELLOW);
		int repairCost = stack.getRepairCost();
		if (repairCost > 0) {
			components.add(itemGotModified);
		}
	}
}
