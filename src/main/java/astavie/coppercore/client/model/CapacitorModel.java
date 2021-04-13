package astavie.coppercore.client.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;

import astavie.coppercore.CopperCore;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidizationLevel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class CapacitorModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private static final SpriteIdentifier COPPER_TOP = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier("minecraft:block/copper_block"));
    private static final SpriteIdentifier EXPOSED_TOP = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier("minecraft:block/exposed_copper"));
    private static final SpriteIdentifier WEATHERED_TOP = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier("minecraft:block/weathered_copper"));
    private static final SpriteIdentifier OXIDIZED_TOP = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier("minecraft:block/oxidized_copper"));

    private static final SpriteIdentifier COPPER_SIDE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier(CopperCore.MOD_ID, "block/copper_capacitor"));
    private static final SpriteIdentifier EXPOSED_SIDE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier(CopperCore.MOD_ID, "block/exposed_copper_capacitor"));
    private static final SpriteIdentifier WEATHERED_SIDE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier(CopperCore.MOD_ID, "block/weathered_copper_capacitor"));
    private static final SpriteIdentifier OXIDIZED_SIDE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier(CopperCore.MOD_ID, "block/oxidized_copper_capacitor"));

    private static final SpriteIdentifier[] TOP = new SpriteIdentifier[] { COPPER_TOP, EXPOSED_TOP, WEATHERED_TOP,
            OXIDIZED_TOP };
    private static final SpriteIdentifier[] SIDE = new SpriteIdentifier[] { COPPER_SIDE, EXPOSED_SIDE, WEATHERED_SIDE,
            OXIDIZED_SIDE };
    private static final SpriteIdentifier LIGHT = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            new Identifier(CopperCore.MOD_ID, "block/capacitor_light"));

    private SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[3];
    private Sprite[] SPRITES = new Sprite[3];

    private static final RenderMaterial NORMAL = RendererAccess.INSTANCE.getRenderer().materialFinder().find();
    private static final RenderMaterial EMISSIVE = RendererAccess.INSTANCE.getRenderer().materialFinder()
            .emissive(0, true).find();

    private Mesh mesh;

    public CapacitorModel(OxidizationLevel level) {
        SPRITE_IDS[0] = TOP[level.ordinal()];
        SPRITE_IDS[1] = SIDE[level.ordinal()];
        SPRITE_IDS[2] = LIGHT;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
            Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        return null;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return SPRITES[0];
    }

    @Override
    public ModelTransformation getTransformation() {
        return null;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return null;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter,
            Set<Pair<String, String>> unresolvedTextureReferences) {
        return Arrays.asList(SPRITE_IDS);
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings rotationContainer, Identifier modelId) {
        // Get the sprites
        for (int i = 0; i < 3; ++i) {
            SPRITES[i] = textureGetter.apply(SPRITE_IDS[i]);
        }

        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        for (Direction direction : Direction.values()) {
            int spriteIdx = direction == Direction.UP || direction == Direction.DOWN ? 0 : 1;
            // Add a new face to the mesh
            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            // Set the sprite of the face, must be called after .square()
            // We haven't specified any UV coordinates, so we want to use the whole texture.
            // BAKE_LOCK_UV does exactly that.
            emitter.spriteBake(0, SPRITES[spriteIdx], MutableQuadView.BAKE_LOCK_UV);
            // Enable texture usage
            emitter.spriteColor(0, -1, -1, -1, -1);
            emitter.material(NORMAL);
            // Add the quad to the mesh
            emitter.emit();

            if (spriteIdx == 1) {
                // light
                emitter.square(direction, 0.375f, 0.375f, 0.625f, 0.625f, -0.0001f);
                emitter.spriteBake(0, SPRITES[2], MutableQuadView.BAKE_LOCK_UV);
                emitter.spriteColor(0, -1, -1, -1, -1);
                emitter.material(EMISSIVE);
                emitter.emit();
            }
        }
        mesh = builder.build();

        return this;
    }

}
