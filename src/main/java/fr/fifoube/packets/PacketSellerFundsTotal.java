/*******************************************************************************
 *******************************************************************************/
package fr.fifoube.packets;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.fifoube.blocks.BlockSeller;
import fr.fifoube.blocks.tileentity.TileEntityBlockSeller;
import fr.fifoube.main.ModEconomyInc;
import fr.fifoube.main.capabilities.CapabilityMoney;
import fr.fifoube.main.config.ConfigFile;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSellerFundsTotal {

	private double funds;
	private double cost;
	private double count;
	private double fundstotal;
	private int x;
	private int y;
	private int z;
	private int amount;
	private boolean recovery;
	public PacketSellerFundsTotal() 
	{
		
	}
	
	public PacketSellerFundsTotal(double funds,  double cost, double count, int xS, int yS, int zS, int amountS, boolean recoveryS)
	{
		this.funds= funds;
		this.cost = cost;
		this.count = count;
		this.x = xS;
		this.y = yS;
		this.z = zS;
		this.amount = amountS;
		this.recovery = recoveryS;
		
		this.fundstotal = funds + cost;

	}
	
	public static PacketSellerFundsTotal decode(PacketBuffer buf) 
	{
		double funds = buf.readDouble();
		double cost = buf.readDouble();
		double count = buf.readDouble();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		int amount = buf.readInt();
		boolean recovery = buf.readBoolean();
		return new PacketSellerFundsTotal(funds, cost,count, x, y, z, amount, recovery);

	}
	
	public static void encode(PacketSellerFundsTotal packet, PacketBuffer buf) 
	{
		buf.writeDouble(packet.funds);
		buf.writeDouble(packet.cost);
		buf.writeDouble(packet.count);
		buf.writeInt(packet.x);
		buf.writeInt(packet.y);
		buf.writeInt(packet.z);
		buf.writeInt(packet.amount);
		buf.writeBoolean(packet.recovery);
	}
	
	public static void handle(PacketSellerFundsTotal packet, Supplier<NetworkEvent.Context> ctx)
	{
			ctx.get().enqueueWork(() -> {
				
				ServerPlayerEntity player = ctx.get().getSender(); // GET PLAYER
				World worldIn = player.world; // GET WORLD
				BlockPos pos = new BlockPos(packet.x, packet.y, packet.z); // NEW BLOCK POS FOR TILE ENTITY COORDINATES
				TileEntity tileentity = worldIn.getTileEntity(pos); // GET THE TILE ENTITY IN WORLD THANKS TO COORDINATES
				if(!worldIn.isRemote)
				if(tileentity instanceof TileEntityBlockSeller)
				{
					TileEntityBlockSeller te = (TileEntityBlockSeller)tileentity;
					if(te != null) // IF TILE ENTITY EXIST
					{
						if(packet.recovery == false)
						{
							if(!te.getStackInSlot(0).isEmpty()) // IF THE SLOT IS NOT EMPTY
							{
								boolean admin = te.getAdmin();
								if(admin == false) // NOT UNLIMITED STACK
								{
									CompoundNBT nbt = te.getStackInSlot(0).getTag();
									ItemStack stack = new ItemStack(te.getStackInSlot(0).getItem(), (int) packet.count);
									if(nbt != null)
									{
										stack.getOrCreateTag().merge(nbt);
									}
									boolean flag = player.inventory.addItemStackToInventory(stack);
									if(flag)
									{
										te.getStackInSlot(0).split((int) packet.count);
										te.setFundsTotal(packet.fundstotal); // SERVER SET THE FUNDS TOTAL FROM WHAT WE SENT
										te.markDirty();
										//
										player.getCapability(CapabilityMoney.MONEY_CAPABILITY, null)
										.ifPresent(data -> {
											ModEconomyInc.LOGGER.info(player.getDisplayName().getString() + " has bought " + te.getItem() + " for " + packet.cost + "." + " Balance was " + data.getMoney() + ", balance is now " + (data.getMoney() - packet.cost) + "." + "[UUID: " + player.getUniqueID() + "," + te.getPos() + "]");
											data.setMoney(data.getMoney() - packet.cost);
										});
										te.setTime(ConfigFile.cooldownSeller);
										BlockState state = worldIn.getBlockState(pos);
										if(state.getBlock() instanceof BlockSeller)
										{
											BlockSeller seller = (BlockSeller) state.getBlock();
											seller.scheduleTick(state, worldIn, pos);
										}
									}
									else
									{
										player.sendMessage(new StringTextComponent(I18n.format("title.noInventoryPlace")), player.getUniqueID());
									}
								}
								else if(admin == true) // UNLIMITED STACK
								{
									CompoundNBT nbt = te.getStackInSlot(0).getTag();
									ItemStack stack = new ItemStack(te.getStackInSlot(0).getItem(), (int) packet.count);
									if(nbt != null)
									{
										stack.getOrCreateTag().merge(nbt);
									}
									boolean flag = player.inventory.addItemStackToInventory(stack);
									if(flag)
									{
										te.setFundsTotal(packet.fundstotal); // SERVER SET THE FUNDS TOTAL FROM WHAT WE SENT
										te.markDirty();	
										//
										player.getCapability(CapabilityMoney.MONEY_CAPABILITY, null)
										.ifPresent(data -> {
											ModEconomyInc.LOGGER.info(player.getDisplayName().getString() + " has bought " + te.getItem() + " for " + packet.cost + "." + " Balance was " + data.getMoney() + ", balance is now " + (data.getMoney() - packet.cost) + "." + "[UUID: " + player.getUniqueID() + "," + te.getPos() + "]");
											data.setMoney(data.getMoney() - packet.cost);
										});
										te.setTime(ConfigFile.cooldownSeller);
										BlockState state = worldIn.getBlockState(pos);
										if(state.getBlock() instanceof BlockSeller)
										{
											BlockSeller seller = (BlockSeller) state.getBlock();
											seller.scheduleTick(state, worldIn, pos);
										}
									}
									else
									{
										player.sendMessage(new StringTextComponent(I18n.format("title.noInventoryPlace")), player.getUniqueID());
									}
								}
							}
						}
						else if(packet.recovery == true)
						{
							player.getCapability(CapabilityMoney.MONEY_CAPABILITY, null)
								.ifPresent(data -> { 			
										   ModEconomyInc.LOGGER.info(player.getDisplayName().getString() + " has recovered funds for a total of " + packet.fundstotal + ". Balance was at " + data.getMoney() + ", balance is now " + (data.getMoney() + packet.fundstotal) + "." + "[UUID: " + player.getUniqueID() + "," + te.getPos() + "]");
										   data.setMoney(data.getMoney() + packet.fundstotal);	
										   te.setFundsTotal(0);
										   te.markDirty();		
								
								});
			
						}
					}
				}
			});
			ctx.get().setPacketHandled(true);
	}

}
