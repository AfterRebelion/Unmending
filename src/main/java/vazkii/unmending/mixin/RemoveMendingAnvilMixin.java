package vazkii.unmending.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class RemoveMendingAnvilMixin extends ForgingScreenHandler {

	@Shadow @Final private Property levelCost;

	public RemoveMendingAnvilMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
	public void onAnvilUpdate(CallbackInfo info) {
		ItemStack left = this.input.getStack(0);
		ItemStack right = this.input.getStack(1);
		ItemStack out = this.output.getStack(0);

		if (out.isEmpty() && (left.isEmpty() || right.isEmpty())) {
			return;
		}

		boolean isMended = false;

		Map<Enchantment, Integer> enchLeft = EnchantmentHelper.get(left);
		Map<Enchantment, Integer> enchRight = EnchantmentHelper.get(right);

		if (enchLeft.containsKey(Enchantments.MENDING) || enchRight.containsKey(Enchantments.MENDING)) {
			if (left.getItem() == right.getItem()) {
				isMended = true;
			}

			if (right.getItem() == Items.ENCHANTED_BOOK) {
				isMended = true;
			}
		}

		if (isMended) {
			if (out.isEmpty()) {
				out = left.copy();
			}

			if (!out.hasTag()) {
				out.setTag(new CompoundTag());
			}

			Map<Enchantment, Integer> enchOutput = EnchantmentHelper.get(out);
			enchOutput.putAll(enchRight);
			enchOutput.remove(Enchantments.MENDING);
			EnchantmentHelper.set(enchOutput, out);

			out.setRepairCost(0);
			if(out.isDamageable()) {
				out.setDamage(0);
			}

			this.output.setStack(0, out);
			if (this.levelCost.get() == 0) {
				this.levelCost.set(1);
			}

			info.cancel();
		}

	}
}
