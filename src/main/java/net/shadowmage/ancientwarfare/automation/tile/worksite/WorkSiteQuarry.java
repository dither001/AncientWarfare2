package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteQuarry extends TileWorksiteBounded
{

boolean finished;
private boolean hasDoneInit = false;

/**
 * Current position within work bounds.
 * Incremented when work is processed.
 */
int currentX, currentY, currentZ;//position within bounds that is the 'active' position
int validateX, validateY, validateZ;

public WorkSiteQuarry()
  {
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 27);
  int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  }

@Override
protected void updateWorksite()
  {
  if(!hasDoneInit)
    {
    initWorkSite();
    hasDoneInit = true;
    }
  incrementalScan();
  }

private void incrementalScan()
  {
  worldObj.theProfiler.startSection("Incremental Scan");
  if(validatePosition(validateX, validateY, validateZ))
    {
    currentX = validateX;
    currentY = validateY;
    currentZ = validateZ;
    finished = false;
    }
  else
    {
    incrementValidationPosition();
    }
  worldObj.theProfiler.endSection();
  }

@Override
protected boolean processWork()
  {
  if(!hasDoneInit)
    {
    initWorkSite();
    hasDoneInit = true;
    }
  if(finished){return false;}
  /**
   * while the current position is invalid, increment to a valid one. generally the incremental scan
   * should have take care of this prior to processWork being called, but just in case...
   */
  while(!validatePosition(currentX, currentY, currentZ))
    {
    if(!incrementPosition())
      {
      /**
       * if no valid position was found, set finished, exit
       */
      finished = true;
      return false;
      }
    }  
  /**
   * if made it this far, a valid position was found, break it and add blocks to inventory
   */
  Block block = worldObj.getBlock(currentX, currentY, currentZ);  
  ArrayList<ItemStack> drops = block.getDrops(worldObj, currentX, currentY, currentZ, worldObj.getBlockMetadata(currentX, currentY, currentZ), 0);
  for(ItemStack stack : drops)
    {
    addStackToInventory(stack, RelativeSide.TOP);    
    }  
  worldObj.setBlockToAir(currentX, currentY, currentZ); 
  return true;
  }

private boolean validatePosition(int x, int y, int z)
  {
  if(!worldObj.isAirBlock(x, y, z) && canHarvest(worldObj.getBlock(x, y, z)))
    {
    return true;
    }
  return false;
  }

private boolean incrementPosition()
  {
  if(finished){return false;}
  currentX++;
  if(currentX>getWorkBoundsMax().x)
    {
    currentX = getWorkBoundsMin().x;
    currentZ++;
    if(currentZ>getWorkBoundsMax().z)
      {
      currentZ = getWorkBoundsMin().z;
      currentY--;
      if(currentY<=0)
        {
        return false;
        }
      }
    }
  return true;
  }

private void incrementValidationPosition()
  {
  validateX++;
  if(validateY>=currentY && validateZ>=currentZ && validateX>=currentX)
    {//dont let validation pass current position    
    validateY = getWorkBoundsMax().y;
    validateX = getWorkBoundsMin().x;
    validateZ = getWorkBoundsMin().z;
    }
  else if(validateX>getWorkBoundsMax().x)
    {
    validateX = getWorkBoundsMin().x;
    validateZ++;
    if(validateZ>getWorkBoundsMax().z)
      {
      validateZ = getWorkBoundsMin().z;
      validateY--;
      if(validateY<=0)
        {        
        validateY = getWorkBoundsMax().y;
        validateX = getWorkBoundsMin().x;
        validateZ = getWorkBoundsMin().z;
        }
      }
    }
  }

private boolean canHarvest(Block block)
  {
  //TODO add block-breaking exclusion list to config
  return block.getMaterial()!=Material.lava && block.getMaterial()!=Material.water && block.getBlockHardness(worldObj, currentX, currentY, currentZ)>=0;
  }

public void initWorkSite()
  {
  this.getWorkBoundsMin().y = 1;
  this.currentY = this.getWorkBoundsMax().y;
  this.currentX = this.getWorkBoundsMin().x;
  this.currentZ = this.getWorkBoundsMin().z;
  this.validateX = this.currentX;
  this.validateY = this.currentY;
  this.validateZ = this.currentZ;
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);//resend work-bounds change
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.MINING;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);  
  currentY = tag.getInteger("currentY");
  currentX = tag.getInteger("currentX");
  currentZ = tag.getInteger("currentZ");  
  validateX = tag.getInteger("validateX");
  validateY = tag.getInteger("validateY");
  validateZ = tag.getInteger("validateZ");  
  finished = tag.getBoolean("finished");
  hasDoneInit = tag.getBoolean("init");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setInteger("currentY", currentY);
  tag.setInteger("currentX", currentX);
  tag.setInteger("currentZ", currentZ);
  tag.setInteger("validateX", validateX);
  tag.setInteger("validateY", validateY);
  tag.setInteger("validateZ", validateZ);
  tag.setBoolean("finished", finished);
  tag.setBoolean("init", hasDoneInit);
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_QUARRY, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@Override
protected boolean hasWorksiteWork()
  {
  return !finished;
  }


}