/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import fr.fifoube.blocks.tileentity.TileEntityBlockSeller;
import fr.fifoube.gui.container.ContainerSeller;
import fr.fifoube.main.ModEconomyInc;
import fr.fifoube.packets.PacketSellerCreated;
import fr.fifoube.packets.PacketsRegistery;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiSeller extends ContainerScreen<ContainerSeller> {

	private TileEntityBlockSeller tile;
	
	public GuiSeller(ContainerSeller container, PlayerInventory playerInventory, ITextComponent name) 
	{
		super(container, playerInventory, name);
		this.tile = getContainer().getTile();
	}
	
	private static final ResourceLocation background = new ResourceLocation(ModEconomyInc.MOD_ID ,"textures/gui/container/gui_seller.png");
	protected int xSize = 176;
	protected int ySize = 168;
	protected int guiLeft;
	protected int guiTop;
	
	private Button validate;
	private Button one;
	private Button five;
	private Button ten;
	private Button twenty;
	private Button fifty;
	private Button hundreed;
	private Button twoHundreed;
	private Button fiveHundreed;
	private Button unlimitedStack;


	private Button one_count;
	private Button two_count;
	private Button four_count;
	private Button eight_count;
	private Button sixteen_count;
	private Button thirtyTwo_count;
	private Button sixtyFour_count;

	private double cost = 1;
	private double count = 1;
	private boolean admin = false;
	
	
	
	@Override
	public void tick() 
	{
		super.tick();
	}
	
	@Override
	protected void init() {
		super.init();
		PlayerEntity player = minecraft.player;
		if(tile.getCreated() == false)
		{
			tile.setCost(1);
			tile.setCount(1);
			this.validate = this.addButton(new Button(width / 2 + 26, height / 2 + 83, 55, 20, new TranslationTextComponent("title.validate"), (onPress) -> { actionPerformed(this.validate); }));
			this.one = this.addButton(new Button(width / 2 - 121, height / 2 - 75, 35, 20, new StringTextComponent("1"), (onPress) -> { actionPerformed(this.one); }));
			this.five = this.addButton(new Button(width / 2 - 121, height / 2 - 56, 35, 20, new StringTextComponent("5"), (onPress) -> { actionPerformed(this.five); }));
			this.ten = this.addButton(new Button(width / 2- 121, height / 2 - 37, 35, 20, new StringTextComponent("10"), (onPress) -> { actionPerformed(this.ten); }));
			this.twenty = this.addButton(new Button(width / 2 - 121, height / 2 - 18, 35, 20, new StringTextComponent("20"), (onPress) -> { actionPerformed(this.twenty); }));
			this.fifty = this.addButton(new Button(width / 2 - 121, height / 2 + 1, 35, 20, new StringTextComponent("50"), (onPress) -> { actionPerformed(this.fifty); }));
			this.hundreed = this.addButton(new Button(width / 2 - 121, height / 2 + 20, 35, 20, new StringTextComponent("100"), (onPress) -> { actionPerformed(this.hundreed); }));
			this.twoHundreed = this.addButton(new Button(width / 2 - 121, height / 2 + 39, 35, 20, new StringTextComponent("200"), (onPress) -> { actionPerformed(this.twoHundreed); }));
			this.fiveHundreed = this.addButton(new Button(width / 2 - 121, height / 2 + 58, 35, 20, new StringTextComponent("500"), (onPress) -> { actionPerformed(this.fiveHundreed); }));

			this.one_count = this.addButton(new Button(width / 2 - 161, height / 2 - 75, 35, 20, new StringTextComponent("1"), (onPress) -> { actionPerformed(this.one_count); }));
			this.two_count = this.addButton(new Button(width / 2 - 161, height / 2 - 56, 35, 20, new StringTextComponent("2"), (onPress) -> { actionPerformed(this.two_count); }));
			this.four_count = this.addButton(new Button(width / 2 - 161, height / 2 - 37, 35, 20, new StringTextComponent("4"), (onPress) -> { actionPerformed(this.four_count); }));
			this.eight_count = this.addButton(new Button(width / 2 - 161, height / 2 - 18, 35, 20, new StringTextComponent("8"), (onPress) -> { actionPerformed(this.eight_count); }));
			this.sixteen_count = this.addButton(new Button(width / 2 - 161, height / 2 + 1, 35, 20, new StringTextComponent("16"), (onPress) -> { actionPerformed(this.sixteen_count); }));
			this.thirtyTwo_count = this.addButton(new Button(width / 2 - 161, height / 2 + 20, 35, 20, new StringTextComponent("32"), (onPress) -> { actionPerformed(this.thirtyTwo_count); }));
			this.sixtyFour_count = this.addButton(new Button(width / 2 - 161, height / 2 + 39, 35, 20, new StringTextComponent("64"), (onPress) -> { actionPerformed(this.sixtyFour_count); }));

			if(player.isCreative() == true)
			{
				this.unlimitedStack = this.addButton(new Button(width /2 + 2, height / 2 - 96, 80, 13, new TranslationTextComponent("title.unlimited"), (onPress) -> { actionPerformed(this.unlimitedStack); }));
			}
		}
	}
	
	protected void actionPerformed(Button button)
	{
		PlayerEntity playerIn = minecraft.player;
		if(tile != null)
		{
			if(button == this.unlimitedStack)
			{
				if(this.admin == false)
				{
					this.admin = true;
					tile.setAdmin(true);
				}
				else if(this.admin == true)
				{
					this.admin = false;
					tile.setAdmin(false);
				}
			}
			if(button == this.one)
			{
				tile.setCost(1);
				this.cost = 1;
			}
			else if(button == this.five)
			{
				tile.setCost(5);
				this.cost = 5;
			}
			else if(button == this.ten)
			{
				tile.setCost(10);
				this.cost = 10;
			}
			else if(button == this.twenty)
			{
				tile.setCost(20);
				this.cost = 20;
			}
			else if(button == this.fifty)
			{
				tile.setCost(50);
				this.cost = 50;
			}
			else if(button == this.hundreed)
			{
				tile.setCost(100);
				this.cost = 100;
			}
			else if(button == this.twoHundreed)
			{
				tile.setCost(200);
				this.cost = 200;
			}
			else if(button == this.fiveHundreed)
			{
				tile.setCost(500);
				this.cost = 500;
			}
			else if(button == this.one_count)
			{
				tile.setCount(1);
				this.count = 1;
			}
			else if(button == this.two_count)
			{
				tile.setCount(2);
				this.count = 2;
			}
			else if(button == this.four_count)
			{
				tile.setCount(4);
				this.count = 4;
			}
			else if(button == this.eight_count)
			{
				tile.setCount(8);
				this.count = 8;
			}
			else if(button == this.sixteen_count)
			{
				tile.setCount(16);
				this.count = 16;
			}
			else if(button == this.thirtyTwo_count)
			{
				tile.setCount(32);
				this.count = 32;
			}
			else if(button == this.sixtyFour_count)
			{
				tile.setCount(64);
				this.count = 64;
			}
			else if(button == this.validate)
			{
				if(!(tile.getCost() == 0)) // IF TILE HAS NOT A COST OF 0 THEN WE PASS TO THE OTHER
				{
					if(tile.getStackInSlot(0).getItem() != Items.AIR) // IF SLOT 0 IS NOT BLOCKS.AIR, WE PASS
					{
						if(!this.admin) //ADMIN HASN'T SET : UNLIMITED STACK
						{
							tile.setAdmin(false);
							tile.setCreated(true); // CLIENT SET CREATED AT TRUE
							final int x = tile.getPos().getX(); // GET X
							final int y = tile.getPos().getY(); // GET Y
							final int z = tile.getPos().getZ(); // GET Z
							int amount = tile.getStackInSlot(0).getCount(); // GET COUNT IN TILE THANKS TO STACK IN SLOT
							String name = tile.getStackInSlot(0).getDisplayName().getString(); // GET ITEM NAME IN TILE THANKS TO STACK IN SLOT
							tile.setItem(name); // CLIENT SET ITEM NAME
							tile.markDirty();
							PacketsRegistery.CHANNEL.sendToServer(new PacketSellerCreated(true, this.cost,this.count, name, amount, x, y, z, false)); // SEND SERVER PACKET FOR CREATED, COST, NAME, AMOUNT, X,Y,Z ARE TILE COORDINATES
							playerIn.closeScreen(); // CLOSE SCREEN
						}
						else if(this.admin) //ADMIN HAS SET : UNLIMITED STACK
						{
							tile.setAdmin(true);
							tile.setCreated(true); // CLIENT SET CREATED AT TRUE
							final int x = tile.getPos().getX(); // GET X
							final int y = tile.getPos().getY(); // GET Y
							final int z = tile.getPos().getZ(); // GET Z
							int amount = tile.getStackInSlot(0).getCount(); // GET COUNT IN TILE THANKS TO STACK IN SLOT
							String name = tile.getStackInSlot(0).getDisplayName().getString(); // GET ITEM NAME IN TILE THANKS TO STACK IN SLOT
							tile.setItem(name); // CLIENT SET ITEM NAME
							tile.markDirty();
							PacketsRegistery.CHANNEL.sendToServer(new PacketSellerCreated(true, this.cost,this.count, name, amount, x, y, z, true)); // SEND SERVER PACKET FOR CREATED, COST, NAME, AMOUNT, X,Y,Z ARE TILE COORDINATES
							playerIn.closeScreen(); // CLOSE SCREEN
						}
						
					}
					else // PROVIDE PLAYER TO SELL AIR
					{
						playerIn.sendMessage(new StringTextComponent(I18n.format("title.sellAir")), playerIn.getUniqueID());	
					}
				}
				else // IT MEANS THAT PLAYER HAS NOT SELECTED A COST
				{
					playerIn.sendMessage(new StringTextComponent(I18n.format("title.noCost")), playerIn.getUniqueID());
				}
			}
		}
	}
	
	@Override
		public boolean isPauseScreen() {
			return false;
		}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) 
	{
		String s = "";
		if(this.admin){ 
			s = I18n.format("title.unlimitedStack");
		} 
		else { 
			s = I18n.format("title.limitedStack");
		}

		this.font.drawString(matrixStack, I18n.format("title.costTitle"), -30, -10, Color.WHITE.getRGB());
		this.font.drawString(matrixStack, I18n.format("title.countTitle"), -70, -10, Color.WHITE.getRGB());

		this.font.drawString(matrixStack, I18n.format("title.cost") + cost, 98, 34, Color.DARK_GRAY.getRGB());
		this.font.drawString(matrixStack, I18n.format("title.count") + count, 98, 44, Color.DARK_GRAY.getRGB());
		this.font.drawString(matrixStack, I18n.format("title.mode") + s, 98, 54, Color.DARK_GRAY.getRGB());
		this.font.drawString(matrixStack, I18n.format("title.block_seller"), 8.0F, 5, Color.DARK_GRAY.getRGB());
	    this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8.0F, (float)(this.ySize - 94), 4210752);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) 
	{
	       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
	       this.minecraft.getTextureManager().bindTexture(background); 
	       int k = (this.width - this.xSize) / 2; 
	       int l = (this.height - this.ySize) / 2;
	       this.blit(matrixStack, k, l, 0, 0, this.xSize, this.ySize); 
	}
}
