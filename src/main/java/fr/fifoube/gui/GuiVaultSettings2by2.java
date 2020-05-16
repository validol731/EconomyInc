/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import fr.fifoube.blocks.tileentity.TileEntityBlockVault2by2;
import fr.fifoube.main.ModEconomyInc;
import fr.fifoube.packets.PacketVaultSettings;
import fr.fifoube.packets.PacketsRegistery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiVaultSettings2by2  extends Screen
{

	private static final ResourceLocation BACKGROUND = new ResourceLocation(ModEconomyInc.MOD_ID ,"textures/gui/screen/gui_item.png");
	protected int xSize = 256;
	protected int ySize = 124;
	protected int guiLeft;
	protected int guiTop;
	protected TextFieldWidget commandTextField;
	protected TileEntityBlockVault2by2 tile;
		
	List<Button> buttonList = new ArrayList<Button>();
	
	public GuiVaultSettings2by2(TileEntityBlockVault2by2 te) {
		super(new TranslationTextComponent("gui.vaultsettings"));
		this.tile = te;


	}
	

	@Override
	protected void init() {
		
		super.init();
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.commandTextField = new TextFieldWidget(this.font, this.width / 2 - 75, this.height / 2 - 70, 150, 20, I18n.format("gui.vaultsettings"));
	    this.commandTextField.setMaxStringLength(35);
	    this.commandTextField.setText("Add other players.");
	    this.children.add(this.commandTextField);
	}
	
	@Override
	public void tick() {
		
		this.commandTextField.tick();
		if(tile.getMax() == 5)
		{
			this.commandTextField.setEnabled(false);
			this.commandTextField.setText("Max players allowed reached");
		}	
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) 
	{
		this.renderBackground();
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
	    this.minecraft.getTextureManager().bindTexture(BACKGROUND); 
	    int k = (this.width - this.xSize) / 2; 
	    int l = (this.height - this.ySize) / 2;
	    this.blit(k, l, 0, 0, this.xSize, this.ySize); 
	    super.render(mouseX, mouseY, partialTicks);
	    if(!Minecraft.getInstance().isSingleplayer())
	    {
		    if(tile.getOwnerS().equals(Minecraft.getInstance().player.getUniqueID().toString()))
		    {
		    	this.commandTextField.render(mouseX, mouseY, partialTicks);
		    }
	    }
	    if(!tile.getAllowedPlayers().isEmpty())
	    {
	    	buttonList.clear();
	    	for(int i = 0; i < tile.getAllowedPlayers().size(); i++)
	    	{
	    		int id = i;
	    		String playerName = tile.getAllowedPlayers().get(i).substring(0, tile.getAllowedPlayers().get(i).indexOf(","));
	    		this.font.drawString(playerName, ((this.width - this.xSize) / 2) + 52 , ((this.height - this.ySize) / 2) + (20 * (i + 1)), 0x00);
	    		Button button = new Button(((this.width - this.xSize) / 2) + 164, ((this.height - this.ySize) / 2) + (18 * (i + 1)), 40, 13, TextFormatting.DARK_RED + "✖", (press) -> this.actionPerformed(id));
	    		buttonList.add(id, button);
	    		this.addButton(button);
	    	}
	    }
	
	}
	
	
	protected void actionPerformed(int buttonIDScreen)
	{		
		Button actualButton = buttonList.get(buttonIDScreen);
		this.buttons.remove(actualButton);
		this.children.remove(actualButton);
        PacketsRegistery.CHANNEL.sendToServer(new PacketVaultSettings(tile.getPos(), "", true, buttonIDScreen));
	}

	
	
	@Override
	public boolean keyPressed(int keyPressedCode, int p_keyPressed_2_, int p_keyPressed_3_) {

	      if (keyPressedCode == 257 || keyPressedCode == 335)
	      {
	    	  if(tile.getMax() < 5)
	        	{
			        this.addPlayerToTileEntity();
			        this.commandTextField.setText("");	
			        return true;
	        	}
	        	else
	        	{
	        		this.commandTextField.setText("Max players allowed reached ");
	        		return false;
	        	}
	      } 
	      else 
	      {
	          return super.keyPressed(keyPressedCode, p_keyPressed_2_, p_keyPressed_3_);
	      }
	}
	
	private void addPlayerToTileEntity() {
		
		    String s = this.commandTextField.getText();
		    List<AbstractClientPlayerEntity> playerList = Minecraft.getInstance().world.getPlayers();
		    if(playerList != null)
		    {
		    	for(int i = 0; i < playerList.size(); i++)
		    	{
		    		if(playerList.get(i).getName().getString().equals(s))
		    		{
		    			UUID playerUUID = playerList.get(i).getUniqueID();
		    			String playerName = playerList.get(i).getDisplayName().getFormattedText();
				        PacketsRegistery.CHANNEL.sendToServer(new PacketVaultSettings(tile.getPos(), playerName + "," + playerUUID.toString(), false, i));
		    		}
		    	}
		    }
	}
	
	@Override
	public void onClose() {
		super.onClose();
		this.getMinecraft().keyboardListener.enableRepeatEvents(false);
	}

	
}