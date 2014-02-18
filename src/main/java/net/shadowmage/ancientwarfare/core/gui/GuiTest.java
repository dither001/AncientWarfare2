package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeTabbed;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.ProgressBar;

public class GuiTest extends GuiContainerBase
{

public GuiTest(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, "guiBackgroundLarge.png");
  }

@Override
public void setupElements()
  {
  // TODO Auto-generated method stub  
  }

@Override
public void initElements()
  {
  CompositeScrolled com = new CompositeScrolled(0, 0, 50, 50);  
  this.addGuiElement(com);
  com.setAreaSize(200);  
  com.addGuiElement(new Button(5, 5, 100, 20, "TestButton2"));
  
  CompositeTabbed tab = new CompositeTabbed(50, 0, 100, 100);  
  tab.addTab("Test1", true);
  tab.addTab("Test2", true);
  tab.addTab("Test3", false);
  tab.addTab("Test4", false);
  this.addGuiElement(tab);  
  tab.addGuiElement("Test1", new Button(10, 10, 24, 16, "test"));
  
  Checkbox box = new Checkbox(10, 10, 16, 16, "TestBox");
  tab.addGuiElement("Test2", box);
  
  
  ProgressBar bar1 = new ProgressBar(150, 0, 90, 20);
  bar1.setProgress(.5f);
  this.addGuiElement(bar1);
  
  ItemSlot slot = new ItemSlot(0, 50, new ItemStack(Items.stick));
  this.addGuiElement(slot);
  }

}
