package safeparrots;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeParrotsMod implements ModInitializer {
	public static final String MOD_ID = "safeparrots";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			if (!(entity instanceof Parrot parrot) || !player.getItemInHand(hand).is(Items.COOKIE)) {
				return InteractionResult.PASS;
			}

			if (level.isClientSide()) {
				// Forward interaction to the logical server (integrated server in singleplayer).
				return InteractionResult.CONSUME;
			}

			ItemStack cookie = player.getItemInHand(hand).copyWithCount(1);
			if (!player.getAbilities().instabuild) {
				player.getItemInHand(hand).shrink(1);
			}

			ItemEntity itemEntity = new ItemEntity(
					level,
					parrot.getX(),
					parrot.getY(),
					parrot.getZ(),
					cookie
			);
			var random = level.getRandom();
			float angle = random.nextFloat() * ((float) Math.PI * 2F);
			float speed = 0.05F + random.nextFloat() * 0.2F;
			itemEntity.setDeltaMovement(
					Mth.sin(angle) * speed,
					0.2 + random.nextDouble() * 0.15,
					Mth.cos(angle) * speed
			);
			level.addFreshEntity(itemEntity);

			return InteractionResult.FAIL;
		});

		LOGGER.info("Safe Parrots loaded — Clever parrots will not eat your cookies!");
	}
}
