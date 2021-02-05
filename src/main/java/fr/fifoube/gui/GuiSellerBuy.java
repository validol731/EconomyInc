/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import fr.fifoube.blocks.tileentity.TileEntityBlockSeller;
import fr.fifoube.items.ItemCreditCard;
import fr.fifoube.main.ModEconomyInc;
import fr.fifoube.main.capabilities.CapabilityMoney;
import fr.fifoube.packets.PacketSellerFundsTotal;
import fr.fifoube.packets.PacketsRegistery;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiSellerBuy extends Screen
{
	private TileEntityBlockSeller tile;
	
	public GuiSellerBuy(TileEntityBlockSeller te) {
		super(new TranslationTextComponent("gui.sellerbuy"));
		this.tile = te;
	}
	
	private static final ResourceLocation background = new ResourceLocation(ModEconomyInc.MOD_ID ,"textures/gui/screen/gui_item.png");
	protected int xSize = 256;
	protected int ySize = 124;
	protected int guiLeft;
	protected int guiTop;
	
	private Button slot1;
	private Button takeFunds;
	private String owner = "";
	private String itemName = "";
	private double cost;
	private double count;
	private int amount;
	private double fundsTotalRecovery;
	private String sellerOwner = "";
	private String worldPlayer = "";
	
	@Override
	public void tick() 
	{
		super.tick();
		amount = tile.getAmount();
		fundsTotalRecovery = tile.getFundsTotal();	
		tile.setFundsTotal(fundsTotalRecovery);
		tile.markDirty();
		if(tile.getTime() != 0)
		{
			slot1.active = false;
		}
		else
		{
			slot1.active = true;
		}
	}
	
	@Override
	protected void init() {
		
		this.guiLeft = (this.width - this.xSize) / 2;
	    this.guiTop = (this.height - this.ySize) / 2;
		if(tile != null)
		{
			this.owner = tile.getOwnerName();
			this.itemName = tile.getItem();
			this.cost = tile.getCost();
			this.count = tile.getCount();
			this.slot1 = this.addButton(new Button(width / 2 - 50, height / 2 + 27, 100, 20, new TranslationTextComponent("title.buy"),(press) -> actionPerformed(0))); 
             
			sellerOwner = tile.getOwner();
			worldPlayer = minecraft.player.getUniqueID().toString();
			if(sellerOwner.equals(worldPlayer))
			{
				this.takeFunds = this.addButton(new Button(width / 2 + 20, height / 2 - 75, 100, 13, new TranslationTextComponent("title.recover"),(press) -> actionPerformed(1))); 
			}
			
		}
		super.init();
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	protected void actionPerformed(int buttonId)
	{		
		final int x = tile.getPos().getX(); // GET X COORDINATES
		final int y = tile.getPos().getY(); // GET Y COORDINATES
		final int z = tile.getPos().getZ(); // GET Z COORDINATES
		minecraft.player.getCapability(CapabilityMoney.MONEY_CAPABILITY).ifPresent(data -> {
			if(tile != null) // WE CHECK IF TILE IS NOT NULL TO AVOID CRASH
			{	
				if(buttonId == 0) //IF PLAYER BUY
				{
						for(int i = 0; i < minecraft.player.inventory.getSizeInventory(); i++) //CHECKING INVENTORY TO SEE IF CREDIT CARD IS THERE
						{
							if(minecraft.player.inventory.getStackInSlot(i).getItem() instanceof ItemCreditCard) //IF AN ITEM CREDIT CARD IS FOUND WE ACCEPT THE ACTION PERFORMED
							{
									ItemStack creditCard = minecraft.player.inventory.getStackInSlot(i); //SET THE SLOT FOUND TO BE THE CREDIT CARD
									if(creditCard.hasTag()) //IF IT HAS TAG 
									if(minecraft.player.getUniqueID().toString().equals(creditCard.getTag().getString("OwnerUUID"))) //AND IT'S THE SAME OWNER 
									{
										if(creditCard.getTag().getBoolean("Linked")) //IF IT'S A LINKED CREDIT CARD THEN WE ACCEPT 
										{
											if(data.getMoney() >= tile.getCost()) //IF THE PLAYER HAS ENOUGH MONEY
											{
												if(tile.getAmount() >= tile.getCount())
												{
													boolean admin = tile.getAdmin();
													if(!admin)
													{
														double fundTotal = tile.getFundsTotal(); // WE GET THE TOTAL FUNDS
														int amount = tile.getAmount(); // GET AMOUNT OF THE TILE ENTITY
														PacketsRegistery.CHANNEL.sendToServer(new PacketSellerFundsTotal(fundTotal, tile.getCost(),tile.getCount(), x,y,z, amount, false)); //SENDING PACKET TO LET SERVER KNOW CHANGES WITH TOTAL FUNDS, COORDINATES AND AMOUNT
														tile.markDirty();
													}
													else if(admin)
													{
														double fundTotal = tile.getFundsTotal(); // WE GET THE TOTAL FUNDS
														int amount = tile.getAmount(); // GET AMOUNT OF THE TILE ENTITY
														PacketsRegistery.CHANNEL.sendToServer(new PacketSellerFundsTotal(fundTotal, tile.getCost(),tile.getCount(), x,y,z, amount, false)); //SENDING PACKET TO LET SERVER KNOW CHANGES WITH TOTAL FUNDS, COORDINATES AND AMOUNT
														tile.markDirty();
													}
												}
											}
											else
											{
												minecraft.player.sendMessage(new StringTextComponent(I18n.format("title.noEnoughFunds")), minecraft.player.getUniqueID());
											}
										}
										else
										{
											minecraft.player.sendMessage(new StringTextComponent(I18n.format("title.notLinked")), minecraft.player.getUniqueID());
	
										}
								}
								else
								{
									minecraft.player.sendMessage(new StringTextComponent(I18n.format("title.noSameOwner")), minecraft.player.getUniqueID());
								}
							}
						}
				}
				else if(buttonId == 1)
				{
					tile.setFundsTotal(0);
					tile.markDirty();
					PacketsRegistery.CHANNEL.sendToServer(new PacketSellerFundsTotal(fundsTotalRecovery, 0,0, x,y,z, amount, true)); //SENDING PACKET TO LET SERVER KNOW CHANGES WITH TOTAL FUNDS, COORDINATES AND AMOUNT
				}			
			}
			
		});
		
	}
	 
		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
		{
			this.renderBackground(matrixStack);
			// added
	        this.getMinecraft().getTextureManager().bindTexture(background);
	        int i = this.guiLeft;
	        int j = this.guiTop;
	        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
			this.font.drawString(matrixStack, TextFormatting.BOLD + I18n.format("title.seller") + owner, (this.width / 2) - 120, (this.height / 2)- 55, Color.BLACK.getRGB());
			this.font.drawString(matrixStack, TextFormatting.BOLD + I18n.format("title.item") + itemName, (this.width / 2) - 120, (this.height / 2)- 45, Color.BLACK.getRGB());
			this.font.drawString(matrixStack, TextFormatting.BOLD + I18n.format("title.cost") + cost, (this.width / 2) - 120, (this.height / 2)- 35, Color.BLACK.getRGB());
			this.font.drawString(matrixStack, TextFormatting.BOLD + I18n.format("title.count") + count, (this.width / 2) - 120, (this.height / 2)- 25, Color.BLACK.getRGB());
			this.font.drawString(matrixStack, TextFormatting.BOLD + I18n.format("title.amount") + amount, (this.width / 2) - 120, (this.height / 2)- 15, Color.BLACK.getRGB());
			if(sellerOwner.equals(worldPlayer))
			{
				this.font.drawString(matrixStack, TextFormatting.BOLD + I18n.format("title.fundsToRecover") + fundsTotalRecovery, (this.width / 2) - 120, (this.height / 2)- 5, Color.BLACK.getRGB());
			}

			
			super.render(matrixStack, mouseX, mouseY, partialTicks);
			int posimgX = i + 225;
			int posimgY = j + 22;
			if(mouseX >= (posimgX - 20) && mouseX <= (posimgX + 20)) {
				if (mouseY >= (posimgY - 15) && mouseY <= (posimgY + 20)) {
					this.renderTooltip(matrixStack, tile.getStackInSlot(0), mouseX - 50, mouseY + 30);
				}
			}
	        drawImageInGui();

	    }

		public void drawImageInGui()
		{
	        int i = this.guiLeft;
	        int j = this.guiTop;
	        GL11.glPushMatrix();
			GlStateManager.enableDepthTest();
		    RenderHelper.enableStandardItemLighting();
		    GL11.glScaled(2, 2, 2);
		    ItemStack stack = new ItemStack(Blocks.BARRIER,(int)tile.getCount());
		    if(!(tile.getAmount() < tile.getCount()))
		    {
			    stack = new ItemStack(tile.getStackInSlot(0).getItem(), (int)tile.getCount());
		    }
		    this.itemRenderer.renderItemAndEffectIntoGUI( stack, (i / 2) + 105 , (j /2) + 5);
		    GL11.glPopMatrix();
		}

}