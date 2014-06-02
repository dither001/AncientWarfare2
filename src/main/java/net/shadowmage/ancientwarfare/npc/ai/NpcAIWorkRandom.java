package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;

public class NpcAIWorkRandom extends NpcAI
{

private int ticksAtSite = 0;
private int moveRetryDelay = 0;
NpcWorker worker;
public NpcAIWorkRandom(NpcBase npc)
  {
  super(npc);
  worker = (NpcWorker)npc;
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  if(npc.getFoodRemaining()<=0){return false;} 
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  return npc.ordersStack==null && worker.autoWorkTarget!=null;
  }

@Override
public boolean continueExecuting()
  {
  if(npc.getFoodRemaining()<=0){return false;} 
  if(!npc.worldObj.provider.hasNoSky && !npc.worldObj.isDaytime() && npc.hasHome())//dont work at night if has home point
    {
    return false;
    }
  return npc.ordersStack==null && worker.autoWorkTarget!=null;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_WORK);
  ticksAtSite=0;  
  }

@Override
public void updateTask()
  {
  BlockPosition pos = worker.autoWorkTarget;
  double dist = npc.getDistance(pos.x, pos.y, pos.z);
  if(dist>5.d*5.d)
    {
    npc.addAITask(TASK_MOVE);
    ticksAtSite=0;
    moveToWork(dist);
    }
  else
    {
    npc.getNavigator().clearPathEntity();
    npc.removeAITask(TASK_MOVE);
    workAtSite();
    }
  }

@Override
public void resetTask()
  {
  npc.removeAITask(TASK_WORK+TASK_MOVE);
  }

private void moveToWork(double dist)
  {
  moveRetryDelay--;
  if(moveRetryDelay<=0)
    {
    BlockPosition pos = worker.autoWorkTarget;
    npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, 1.d);
    moveRetryDelay=10;//base .5 second retry delay
    if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
    if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32    
    }
  }

private void workAtSite()
  {
  ticksAtSite++;
  if(npc.ticksExisted%10==0){npc.swingItem();}
  if(ticksAtSite>=AWNPCStatics.npcWorkTicks)
    { 
    ticksAtSite = 0;   
    BlockPosition pos = worker.autoWorkTarget;
    TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof IWorkSite)
      {
      IWorkSite site = (IWorkSite)te;
      if(worker.canWorkAt(site.getWorkType()))
        {      
        if(site.hasWork())
          {        
          npc.addExperience(AWNPCStatics.npcXpFromWork);
          site.addEnergyFromWorker(worker);
          }
        else
          {
          worker.autoWorkTarget=null;
          }  
        }
      else
        {
        worker.autoWorkTarget=null;
        }      
      }
    else
      {
      worker.autoWorkTarget=null;
      }
    }  
  }

public void readFromNBT(NBTTagCompound tag)
  {
  ticksAtSite = tag.getInteger("ticksAtSite");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("ticksAtSite", ticksAtSite);
  return tag;
  }

}