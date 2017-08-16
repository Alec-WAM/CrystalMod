package alec_wam.CrystalMod.client.util.comp;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.client.util.SpriteData;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiComponentBook extends BaseComponent implements IComponentListener {

	private GuiComponentSprite imgLeftBackground;
	private GuiComponentSprite imgRightBackground;
	private GuiComponentSpriteButton imgPrev;
	private GuiComponentSpriteButton imgNext;
    private GuiComponentLabel pageNumberLeft;
	private GuiComponentLabel pageNumberRight;

	private String strPageNumberLeft;
	private String strPageNumberRight;

	public static SpriteData iconPageLeft = new SpriteData(-45, 0, 211, 180);
	public static SpriteData iconPageRight = new SpriteData(0, 0, 211, 180);
	public static SpriteData iconPrev = new SpriteData(57, 226, 18, 10);
	public static SpriteData iconNext = new SpriteData(57, 213, 18, 10);
	public static SpriteData iconPrevHover = new SpriteData(80, 226, 18, 10);
	public static SpriteData iconNextHover = new SpriteData(80, 213, 18, 10);

	public static final ResourceLocation texture = new ResourceLocation("crystalmod:textures/gui/guide.png");

	public List<BaseComponent> pages;

	private int index = 0;

	public GuiComponentBook() {
		super(0, 0);
		imgLeftBackground = new GuiComponentSprite(0, 0, iconPageLeft, texture);
		imgRightBackground = new GuiComponentSprite(0, 0, iconPageRight, texture);

		imgPrev = new GuiComponentSpriteButton(24, 158, iconPrev, iconPrevHover, texture);
		imgPrev.addListener(this);
		imgNext = new GuiComponentSpriteButton(380, 158, iconNext, iconNextHover, texture);
		imgNext.addListener(this);

		strPageNumberLeft = new String("");
		strPageNumberRight = new String("");
		pageNumberLeft = new GuiComponentLabel(85, 163, 100, 10, strPageNumberLeft);
		pageNumberLeft.setScale(0.5f);
		pageNumberRight = new GuiComponentLabel(295, 163, 100, 10, strPageNumberRight);
		pageNumberRight.setScale(0.5f);

		addComponent(imgLeftBackground);
		addComponent(imgRightBackground);
		addComponent(imgPrev);
		addComponent(imgNext);
		addComponent(pageNumberLeft);
		addComponent(pageNumberRight);

		pages = Lists.newArrayList();

	}

	public boolean gotoPage(BaseComponent page) {
		int pageIndex = pages.indexOf(page);
		if (pageIndex > -1) {
			index = pageIndex % 2 == 1? pageIndex - 1 : pageIndex;
			enablePages();
			return true;
		}
		return false;
	}

	public int getNumberOfPages() {
		return pages.size();
	}

	@Override
	public int getWidth() {
		return (int)iconPageRight.getHeight() * 2;
	}

	@Override
	public int getHeight() {
		return (int)iconPageRight.getHeight();
	}

	public void addPage(BaseComponent page) {
		if(page==null)return;
		addComponent(page);
		page.setEnabled(false);
		pages.add(page);
	}
	
	public void addPages(List<BaseComponent> pages) {
		if(pages==null)return;
		final List<BaseComponent> pageL = new ArrayList<BaseComponent>(pages);
		for(BaseComponent page : pageL){
			addPage(page);
		}
	}
	
	/*public static GuiComponentCrystalRecipePage getCrystalRecipeComp(String name, String dis, Object item) {
		return getCrystalRecipeComp(name, dis, item, false);
	}
	
	public static GuiComponentCrystalRecipePage getCrystalRecipeComp(String name, String dis, Object item, boolean oreDic) {
		ItemStack stack = null;
		String type = "";
		name = Lang.translateToLocal(name);
		dis = Lang.translateToLocal(dis);
		if (item instanceof ItemStack) {
			stack = (ItemStack)item;
			type = (stack.getItem() instanceof ItemBlock)? "" : "";
		}
		if (item instanceof Item) {
			stack = new ItemStack((Item)item);
			type = "";
		} else if (item instanceof Block) {
			stack = new ItemStack((Block)item);
			type = "";
		}
		if (stack != null) {
			String fullName = String.format("-%s-", name);
			String description = dis; //String.format("%s%s%s", dis);
			String video = String.format("-%s%s-", type, name);
			return new GuiComponentCrystalRecipePage(fullName, description, video, stack, oreDic);
		}
		return null;
	}*/
	public static GuiComponentStandardRecipePage getStandardRecipeComp(String name, String dis, Object item) {
		name = Lang.localize(name);
		dis = Lang.localize(dis);
		List<ItemStack> list = Lists.newArrayList();
		if (item instanceof ItemStack) {
			list.add((ItemStack)item);
		}
		if(item instanceof List){
			@SuppressWarnings("unchecked")
			List<ItemStack> oList = (List<ItemStack>)item;
			list.addAll(oList);
		}
		if (item instanceof Item) {
			list.add(new ItemStack((Item)item));
		} else if (item instanceof Block) {
			list.add(new ItemStack((Block)item));
		}
		String fullName = String.format("-%s-", name);
		String description = dis;
		return new GuiComponentStandardRecipePage(fullName, description, list);
	}

	
	/*public boolean addCrystalRecipePage(String modId, String name, String dis, Object item) {
		ItemStack stack = null;
		String type = "";
		if (item instanceof ItemStack) {
			stack = (ItemStack)item;
			type = (stack.getItem() instanceof ItemBlock)? "" : "";
		}
		if (item instanceof Item) {
			stack = new ItemStack((Item)item);
			type = "";
		} else if (item instanceof Block) {
			stack = new ItemStack((Block)item);
			type = "";
		}
		if (stack != null) {
			String fullName = String.format("-%s%s%s-", type, modId, name);
			String description = dis; //String.format("%s%s%s", dis);
			String video = String.format("-%s%s%s-", type, modId, name);
			addPage(new GuiComponentCrystalRecipe(fullName, description, video, stack));
			return true;
		}
		return false;
	}*/
	
	public void gotoIndex(int i) {
		index = i;
		enablePages();
	}
	
	public int getCurrentIndex(){
		return index;
	}

	public void enablePages() {
		int i = 0;
		for (BaseComponent page : pages) {
			page.setEnabled(i == index || i == index+1);
			i++;
		}

		int totalPageCount = i % 2 == 0? i : i + 1;

		imgNext.setEnabled(index < pages.size() - 2);
		imgPrev.setEnabled(index > 0);
		strPageNumberLeft.contentEquals(String.format("Page %s of %s", index + 1, totalPageCount));
		strPageNumberRight.contentEquals(String.format("Page %s of %s", index + 2, totalPageCount));
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		imgRightBackground.setX((int)iconPageRight.getWidth());
		if (index + 1 < pages.size()) {
			pages.get(index + 1).setX((int)iconPageRight.getWidth());
		}
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
	}

	@Override
	public void componentMouseDown(BaseComponent component, int offsetX, int offsetY, int button) {
		int oldIndex = index;
		if (component == imgPrev) {
			if (index > 0) {
				index -= 2;
			}
		}
		if (component == imgNext) {
			if (index < pages.size() - 2) {
				index += 2;
			}
		}
		if (oldIndex != index) {
			Minecraft.getMinecraft();
		}
		enablePages();
	}

	@Override
	public void componentMouseDrag(BaseComponent component, int offsetX, int offsetY, int button, long time) {}

	@Override
	public void componentMouseMove(BaseComponent component, int offsetX, int offsetY) {}

	@Override
	public void componentMouseUp(BaseComponent component, int offsetX, int offsetY, int button) {}

	@Override
	public void componentKeyTyped(BaseComponent component, char par1, int par2) {}

}
