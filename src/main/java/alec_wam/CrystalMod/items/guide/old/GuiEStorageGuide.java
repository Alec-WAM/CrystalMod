package alec_wam.CrystalMod.items.guide.old;

import net.minecraft.item.ItemStack;
import alec_wam.CrystalMod.client.util.comp.BaseComponent;
import alec_wam.CrystalMod.client.util.comp.GuiComponentLabel;
import alec_wam.CrystalMod.client.util.comp.book.BlankPage;
import alec_wam.CrystalMod.client.util.comp.book.SectionPage;
import alec_wam.CrystalMod.items.guide.GuidePages;


public class GuiEStorageGuide extends GuiGuideBase {
	private GuiComponentLabel lblBlocks;
	private GuiComponentLabel lblItems;
	
	private int itemsIndex = 0;
	private int blocksIndex = 0;
	
	public GuiEStorageGuide(){
		
	}
	public GuiEStorageGuide(ItemStack hand) {
		super(hand);
	}
	
	public void addCompToFront(BlankPage contentsPage){
		lblBlocks = new GuiComponentLabel(27, 90, "Blocks");
		lblBlocks.addListener(this);
		lblItems = new GuiComponentLabel(27, 105, "Items");
		lblItems.addListener(this);
		

		contentsPage.addComponent(lblBlocks);
		contentsPage.addComponent(lblItems);
	}
	
	public void addBookPages(){
		blocksIndex = book.getNumberOfPages();
		
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("Blocks"));
		book.addPages(GuidePages.eStorageBlockData);
		
		itemsIndex = book.getNumberOfPages();
		if (itemsIndex % 2 == 1) {
			book.addPage(new BlankPage());
			itemsIndex++;
		}
		
		book.addPage(new BlankPage());
		book.addPage(new SectionPage("Items"));
		book.addPages(GuidePages.eStorageItemData);
	}
	
	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		if (component.equals(lblBlocks)) {
			book.gotoIndex(blocksIndex);
		}else if (component.equals(lblItems)) {
			book.gotoIndex(itemsIndex);
		}
	}

}