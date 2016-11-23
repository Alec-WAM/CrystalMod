package alec_wam.CrystalMod.items.guide.old;

import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.client.util.comp.BaseComponent;
import alec_wam.CrystalMod.client.util.comp.GuiComponentLabel;
import alec_wam.CrystalMod.client.util.comp.book.BlankPage;
import alec_wam.CrystalMod.client.util.comp.book.SectionPage;


public class GuiCrystalGuide extends GuiGuideBase {
	public GuiCrystalGuide(){
		
	}
	public GuiCrystalGuide(ItemStack hand) {
		super(hand);
	}
	
	public void addCompsToFront(BlankPage contentsPage){
		GuiComponentLabel lblBlocks = new GuiComponentLabel(27, 90, "Blocks");
		lblBlocks.addListener(this);
		GuiComponentLabel lblItems = new GuiComponentLabel(27, 105, "Items");
		lblItems.addListener(this);
		GuiComponentLabel lblEnt = new GuiComponentLabel(27, 120, "Entities");
		lblEnt.addListener(this);
		GuiComponentLabel lblWorkbench = new GuiComponentLabel(27, 135, "Workbench");
		lblWorkbench.addListener(this);
		

		contentsPage.addComponent(lblBlocks);
		contentsPage.addComponent(lblItems);
		contentsPage.addComponent(lblEnt);
		contentsPage.addComponent(lblWorkbench);
	}
	
	public void addBookPages(){
		/*blocksIndex = book.getNumberOfPages();
		
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("Blocks"));
		book.addPages(GuidePages.blockData);
		
		itemsIndex = book.getNumberOfPages();
		if (itemsIndex % 2 == 1) {
			book.addPage(new BlankPage());
			itemsIndex++;
		}
		
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("Items"));
		book.addPages(GuidePages.itemData);
		
		entIndex = book.getNumberOfPages();
		if (entIndex % 2 == 1) {
			book.addPage(new BlankPage());
			entIndex++;
		}
		
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("Entities"));
		book.addPages(GuidePages.entityData);
		
		workbenchIndex = book.getNumberOfPages();
		if (workbenchIndex % 2 == 1) {
			book.addPage(new BlankPage());
			workbenchIndex++;
		}
		
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("Workbench"));
		book.addPages(GuidePages.workbenchData);*/
	}
	
	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		/*if (component.equals(lblBlocks)) {
			book.gotoIndex(blocksIndex);
		}else if (component.equals(lblItems)) {
			book.gotoIndex(itemsIndex);
		} else if (component.equals(lblEnt)) {
			book.gotoIndex(entIndex);
		} else if (component.equals(lblWorkbench)) {
			book.gotoIndex(workbenchIndex);
		} */
	}

}