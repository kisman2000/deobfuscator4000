package dev.banzetta.droplight.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.*;
import dev.banzetta.droplight.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ EntityRenderer.class })
public abstract class EntityRendererMixin<T extends Entity>
{
    @Inject(method = { "render" }, at = { @At("HEAD") })
    public void render(final T entity, final float f, final float g, final MatrixStack poseStack, final VertexConsumerProvider buffer, final int packedLight, final CallbackInfo info) {
        if (entity instanceof final ItemEntity itemEntity) {
            final MatrixStack newPoseStack = new MatrixStack();
            newPoseStack.loadIdentity();
            newPoseStack.multiplyPositionMatrix(poseStack.peek().getPositionMatrix());
            DroplightClient.VISIBLE_ITEMS.put((Object)itemEntity, (Object)newPoseStack);
        }
    }
}
