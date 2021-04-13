package astavie.coppercore.client;

import org.jetbrains.annotations.Nullable;

import astavie.coppercore.CopperCore;
import astavie.coppercore.client.model.CapacitorModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.block.Oxidizable.OxidizationLevel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public class CopperCoreModelProvider implements ModelResourceProvider {

    public static final Identifier COPPER_CAPACITOR = new Identifier(CopperCore.MOD_ID, "block/copper_capacitor");
    public static final Identifier EXPOSED_COPPER_CAPACITOR = new Identifier(CopperCore.MOD_ID,
            "block/exposed_copper_capacitor");
    public static final Identifier WEATHERED_COPPER_CAPACITOR = new Identifier(CopperCore.MOD_ID,
            "block/weathered_copper_capacitor");
    public static final Identifier OXIDIZED_COPPER_CAPACITOR = new Identifier(CopperCore.MOD_ID,
            "block/oxidized_copper_capacitor");

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context)
            throws ModelProviderException {
        if (resourceId.equals(COPPER_CAPACITOR)) {
            return new CapacitorModel(OxidizationLevel.UNAFFECTED);
        } else if (resourceId.equals(EXPOSED_COPPER_CAPACITOR)) {
            return new CapacitorModel(OxidizationLevel.EXPOSED);
        } else if (resourceId.equals(WEATHERED_COPPER_CAPACITOR)) {
            return new CapacitorModel(OxidizationLevel.WEATHERED);
        } else if (resourceId.equals(OXIDIZED_COPPER_CAPACITOR)) {
            return new CapacitorModel(OxidizationLevel.OXIDIZED);
        }

        return null;
    }

}
