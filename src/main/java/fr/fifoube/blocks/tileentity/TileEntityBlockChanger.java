/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.blocks.tileentity;


import fr.fifoube.gui.container.ContainerChanger;
import fr.fifoube.items.ItemsRegistery;
import fr.fifoube.main.ModEconomyInc;
import fr.fifoube.main.capabilities.CapabilityMoney;
import fr.fifoube.main.config.ConfigFile;
import fr.fifoube.stats.StatsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityBlockChanger extends TileEntity implements INamedContainerProvider, ITickableTileEntity
{
	private static final TranslationTextComponent NAME = new TranslationTextComponent("container.changer");
	ItemStackHandler inventory = new ItemStackHandler(3);
	private byte direction;
	public int numbUse;
	public PlayerEntity user;
	public String name;
	public int timeProcess = ConfigFile.goldChangerDuration;
	public int timePassed = 0;
	public boolean isProcessing;
    private ITextComponent customName;

	public TileEntityBlockChanger()
	{
		this(TileEntityRegistery.TILE_CHANGER);
	}
	
	public TileEntityBlockChanger(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) 
    {
    	deserializeNBT(pkt.getNbtCompound());
    }
    
	public ItemStack setStackInSlot(int slot, ItemStack stack, boolean simulate)
	{
		return inventory.insertItem(slot, stack, simulate);
	}
	
	public ItemStackHandler getHandler()
	{
		return inventory;
	}
		
	public int getNumbUse()
	{
		return this.numbUse;
	}
	
	public void setNumbUse(int numbUse)
	{
		this.numbUse = numbUse;
	}
	
	public PlayerEntity getEntityPlayer()
	{
		return this.user;
	}
	
	public void setEntityPlayer(PlayerEntity currentUser)
	{
		this.user = currentUser;
	}

    public byte getDirection()
    {
    	return this.direction;
    }

	public void setDirection(byte direction) 
	{
		this.direction = direction;
	}
	
	public int getTimePassed()
	{
		return this.timePassed;
	}
	
	public boolean getIsProcessing()
	{
		return this.isProcessing;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) 
	{
		compound.put("inventory", inventory.serializeNBT());
		compound.putInt("numbUse", this.numbUse);
		compound.putBoolean("isProcessing", this.isProcessing);
		compound.putInt("timePassed", this.timePassed);
		if (this.getDisplayName() != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.getDisplayName()));
       }
		return super.write(compound);
	}
		
	@Override
	public void read(BlockState state, CompoundNBT compound) {

		super.read(state, compound);
		this.numbUse = compound.getInt("numbUse");
		this.isProcessing = compound.getBoolean("isProcessing");
		this.timePassed = compound.getInt("timePassed");
		inventory.deserializeNBT((CompoundNBT)compound.get("inventory"));
	    if (compound.contains("CustomName", Constants.NBT.TAG_STRING)) {
	    this.customName = ITextComponent.Serializer.func_240643_a_(compound.getString("CustomName"));
	    }
	}

	
		@Override
		public void markDirty() 
		{
			 BlockState state = this.world.getBlockState(getPos());
		     this.world.notifyBlockUpdate(getPos(), state, state, 3);
		}
		
		@Override
		public void tick() 
		{
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityBlockChanger)
			{
				TileEntityBlockChanger tile = (TileEntityBlockChanger)te;
				ItemStack slot0_Nugget = inventory.getStackInSlot(0);
				ItemStack slot1_Card = inventory.getStackInSlot(1);
				ItemStack slot2_OUT = inventory.getStackInSlot(2);
				if(slot0_Nugget != null && slot1_Card != null && slot2_OUT != null)
				if(!world.isRemote && slot0_Nugget.getItem() == ItemsRegistery.ITEM_GOLDNUGGET)
				{
						if(slot1_Card.getItem() == ItemsRegistery.ITEM_CREDITCARD)
						{
							 if(slot1_Card.hasTag() && tile.getEntityPlayer() != null)
							    {
						        	String nameCard = slot1_Card.getTag().getString("OwnerUUID");
									String nameGame =  tile.getEntityPlayer().getUniqueID().toString();
									if(nameCard.equals(nameGame))
									{
										if(slot2_OUT.isEmpty())
										{
											if(timePassed == 0)
											{
												//String w =  String.valueOf(world.getRandom().nextDouble()).substring(0,4);
												double weight = 0;
												for( int i = slot0_Nugget.getCount(); i>0; i--){

													weight +=  world.getRandom().nextDouble() * 0.9;
												}
												weight *= ConfigFile.multiplierGoldNuggetWeight;
												String w =  String.valueOf(weight).substring(0,4);
												slot0_Nugget.setCount(1);
												if(slot0_Nugget.hasTag())
												{
													if(!slot0_Nugget.getTag().contains("weight"))
													{
														slot0_Nugget.getTag().putString("weight", w);
													}
												}
												else
												{
													slot0_Nugget.getOrCreateTag().putString("weight", w);
												}
											}
											if(timePassed == timeProcess)
											{
												PlayerEntity playerIn = getEntityPlayer();
												playerIn.getCapability(CapabilityMoney.MONEY_CAPABILITY, null).ifPresent(data -> {
													double fundsPrev = data.getMoney();
													String weight = slot0_Nugget.getTag().getString("weight");
													double fundsNow = (fundsPrev + (Double.parseDouble(weight)));
													data.setMoney(fundsNow);
													slot0_Nugget.split(1);
													slot0_Nugget.removeChildTag("weight");
													ItemStack copyOfCard = slot1_Card.copy();
													slot1_Card.split(1);
													tile.setStackInSlot(2, copyOfCard, false);
													timePassed = 0;
													isProcessing = false;
													this.markDirty();
													playerIn.addStat(StatsRegistry.CHANGED_GOLD_TO_MONEY);
													ModEconomyInc.LOGGER.info(playerIn.getDisplayName().getString() + " has changed gold with the weight ("+ weight +"), the change was at " + (Double.parseDouble(weight) * ConfigFile.multiplierGoldNuggetWeight) + ". Balance was at " + fundsPrev + ", balance is now " + data.getMoney() + "." + "[UUID: " + playerIn.getUniqueID() + "," + te.getPos() + "]");
												});
	
											}
											else
											{
												++timePassed;
												isProcessing = true;
												this.markDirty();
											}
										}
									}
							    }
							}
				}
			
				if(slot0_Nugget.getItem() == Items.AIR || slot1_Card.getItem() == Items.AIR)
				{
						timePassed = 0;	
						isProcessing = false;
						this.markDirty();
				}
			}
		}
		

		public ItemStack removeStackFromSlot(int index) 
		{
			return inventory.getStackInSlot(index).split(1);
		}

		public ItemStack getStackInSlot(int index) 
		{
			return inventory.getStackInSlot(index);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
			return new ContainerChanger(id, inv, getPos());
		}

		@Override
		public ITextComponent getDisplayName() {
			return NAME;
		}	
		
		
}
