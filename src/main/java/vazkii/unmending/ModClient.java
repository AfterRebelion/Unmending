package vazkii.unmending;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class ModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Unmending.LOGGER.info("Hello Client world!");
		ItemTooltipCallback.EVENT.register(IsMendedEvent::getTooltip);
	}
}
