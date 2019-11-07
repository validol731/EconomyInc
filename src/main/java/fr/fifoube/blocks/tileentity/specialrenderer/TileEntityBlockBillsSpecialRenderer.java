package fr.fifoube.blocks.tileentity.specialrenderer;

import com.mojang.blaze3d.platform.GlStateManager;

import fr.fifoube.blocks.models.ModelBills;
import fr.fifoube.blocks.tileentity.TileEntityBlockBills;
import fr.fifoube.main.ModEconomyInc;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;

public class TileEntityBlockBillsSpecialRenderer extends TileEntityRenderer<TileEntityBlockBills> {

	private static ModelBills modelBlock = new ModelBills();
	public static ResourceLocation texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_0.png");
	
	public void renderTileEntityBillsAt(TileEntityBlockBills tile, double posX, double posY, double posZ, float partialTicks, int damageCount) 
	{
		checkBillRef(tile);
		GlStateManager.pushMatrix();
		GlStateManager.translated(posX + 0.125F, posY + 0.531F, posZ + 0.250F);
        GlStateManager.rotatef(180F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scaled(0.333, 0.333, 0.333);   
        bindTexture(texture);
        modelBlock.renderAll(tile);;
        GlStateManager.popMatrix();
	}
	
	@Override
	public void render(TileEntityBlockBills tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		this.renderTileEntityBillsAt(((TileEntityBlockBills) tileEntityIn), x, y, z, partialTicks, destroyStage);
	}
	
	public void checkBillRef(TileEntityBlockBills tile)
	{		
		switch (tile.getBillRef()) {
		case "item.economyinc.item_oneb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_1.png");
			break;
		case "item.economyinc.item_fiveb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_5.png");
			break;
		case "item.economyinc.item_tenb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_10.png");
			break;
		case "item.economyinc.item_twentyb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_20.png");
			break;
		case "item.economyinc.item_fiftybe":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_50.png");
			break;
		case "item.economyinc.item_hundreedb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_100.png");
			break;
		case "item.economyinc.item_twohundreedb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_200.png");
			break;
		case "item.economyinc.item_fivehundreedb":
			texture = new ResourceLocation(ModEconomyInc.MOD_ID, "textures/blocks_models/block_bills_500.png");
			break;
		default:
			break;
		}
	}
}
