package net.bleaktorium.black_tongue.block.entity.client;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.block.entity.EmbalmingTableBlockEntity;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class EmbalmingTableModel extends GeoModel<EmbalmingTableBlockEntity> {
    private static final ResourceLocation FRESH = texture("et_fresh");
    private static final ResourceLocation ROTTEN = texture("et_rotten");
    private static final ResourceLocation CRUMBLING = texture("et_crumbling");

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "textures/block/" + name + ".png");
    }

    @Override
    public ResourceLocation getModelResource(EmbalmingTableBlockEntity table) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "geo/embalming_table.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmbalmingTableBlockEntity table) {
        RemainsData data = table.getRemainsData();
        Level level = table.getLevel();
        if (data == null || level == null) return FRESH;
        return switch (data.tier(level)) {
            case FRESH -> FRESH;
            case ROTTEN -> ROTTEN;
            case CRUMBLING, DUST -> CRUMBLING;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(EmbalmingTableBlockEntity table) {
        return ResourceLocation.fromNamespaceAndPath(Black_Tongue.MOD_ID, "animations/embalming.animation.json");
    }

    @Override
    public void setCustomAnimations(EmbalmingTableBlockEntity table, long instanceId,
                                    AnimationState<EmbalmingTableBlockEntity> animationState) {
        super.setCustomAnimations(table, instanceId, animationState);
        RemainsData data = table.getRemainsData();
        show("ancestor", data != null && data.origin() == RemainsData.Origin.ANCESTOR);
        show("witch", data != null && data.origin() == RemainsData.Origin.WITCH);

        int age = table.pourAge();
        show("liquid", age >= 0 && age < EmbalmingTableBlockEntity.LIQUID_GONE_TICK);

        var pourPoint = getAnimationProcessor().getBone("pour_point");
        if (pourPoint != null) {
            pourPoint.setTrackingMatrices(true);
            if (age >= EmbalmingTableBlockEntity.DRIP_START && age < EmbalmingTableBlockEntity.DRIP_END) {
                table.dripOil(pourPoint.getLocalPosition());
            }
        }

        show("armor_crafting", false); // the armor half of the station
    }

    private void show(String boneName, boolean visible) {
        var bone = getAnimationProcessor().getBone(boneName);
        if (bone != null) bone.setHidden(!visible);
    }
}