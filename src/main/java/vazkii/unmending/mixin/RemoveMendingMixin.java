package vazkii.unmending.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class RemoveMendingMixin extends Entity {

	@Shadow public int pickupDelay;
	@Shadow public abstract int getExperienceAmount();

	public RemoveMendingMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "onPlayerCollision", at = @At(value = "HEAD"), cancellable = true)
	public void killMending(PlayerEntity player, CallbackInfo info) {
		if (!this.world.isClient) {
			if (this.pickupDelay == 0 && player.experiencePickUpDelay == 0) {
				player.experiencePickUpDelay = 2;
				player.sendPickup(this, 1);
				if (getExperienceAmount() > 0) {
					player.addExperience(getExperienceAmount());
				}

				this.remove();
			}
		}
	}

}
