package vazkii.unmending.mixin;


import net.minecraft.container.AnvilContainer;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilContainer.class)
public abstract class RemoveMendingAnvilMixin extends Container {

	@Shadow @Final private Inventory inventory;
	@Shadow @Final private Inventory result;
	@Shadow @Final private Property levelCost;

	protected RemoveMendingAnvilMixin(ContainerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Inject(method = "updateResult", at = @At(value = "HEAD"), cancellable = true)
	public void onAnvilUpdate(CallbackInfo info) {
		ItemStack left = this.inventory.getInvStack(0);
		ItemStack right = this.inventory.getInvStack(1);
		ItemStack out = this.result.getInvStack(0);

		if (out.isEmpty() && (left.isEmpty() || right.isEmpty())) {
			return;
		}

		boolean isMended = false;

		Map<Enchantment, Integer> enchLeft = EnchantmentHelper.getEnchantments(left);
		Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);

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

			Map<Enchantment, Integer> enchOutput = EnchantmentHelper.getEnchantments(out);
			enchOutput.putAll(enchRight);
			enchOutput.remove(Enchantments.MENDING);
			EnchantmentHelper.set(enchOutput, out);

			out.setRepairCost(0);
			if(out.isDamageable()) {
				out.setDamage(0);
			}

			this.result.setInvStack(0, out);
			if (this.levelCost.get() == 0) {
				this.levelCost.set(1);
			}

			info.cancel();
		}

	}
}
