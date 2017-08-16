package alec_wam.CrystalMod;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.blocks.BlockCrystal.CrystalBlockType;
import alec_wam.CrystalMod.blocks.BlockCrystalLog;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.material.IMaterialCrop;
import alec_wam.CrystalMod.blocks.crops.material.ItemMaterialSeed;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.handler.MissingItemHandler;
import alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.commands.CommandCrystalMod;
import alec_wam.CrystalMod.proxy.CommonProxy;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CrystalMod.MODID, name = CrystalMod.MODNAME, version = CrystalMod.VERSION, guiFactory = "alec_wam.CrystalMod.config.ConfigFactoryCM")
public class CrystalMod {
	public static final String MODID = "crystalmod";
	public static final String MODNAME = "Crystal Mod";
	public static final String VERSION = "@VERSION@";
	
	static {
		ModFluids.registerBucket();
	}
	
	@Instance(MODID)
	public static CrystalMod instance;
	
	@SidedProxy(clientSide="alec_wam.CrystalMod.proxy.ClientProxy", serverSide="alec_wam.CrystalMod.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static abstract class CustomCreativeTab extends CreativeTabs implements Comparator<ItemStack> {

		public CustomCreativeTab(String label) {
			super(label);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(final NonNullList<ItemStack> list) {
            final NonNullList<ItemStack> newList = NonNullList.create();
            super.displayAllRelevantItems(newList);
            Collections.sort(newList, this);
            list.addAll(newList);
        }
		
	}
	
	public static CreativeTabs tabItems = new CustomCreativeTab(MODID.toLowerCase()+".items") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.crystals, 1, CrystalType.BLUE.getMetadata());
        }

		@Override
		public int compare(ItemStack arg0, ItemStack arg1) {
			if(arg0.getItem() !=arg1.getItem()){
				return ItemUtil.compareNames(arg0, arg1);
			}
			return 0;
		}
    };
    
    public static CreativeTabs tabTools = new CustomCreativeTab(MODID.toLowerCase()+".tools") {
    	@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ModItems.wrench);
		}

		@Override
		public int compare(ItemStack arg0, ItemStack arg1) {
			if(arg0.getItem() !=arg1.getItem()){
				return ItemUtil.compareNames(arg0, arg1);
			}
			return 0;
		}
    };
    
    public static CreativeTabs tabBlocks = new CustomCreativeTab(MODID.toLowerCase()+".blocks") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.crystal, 1, CrystalBlockType.BLUE.getMeta());
        }

		@Override
		public int compare(ItemStack arg0, ItemStack arg1) {
			if(arg0.getItem() !=arg1.getItem()){
				return ItemUtil.compareNames(arg0, arg1);
			}
			return 0;
		}
    };
    
    public static CreativeTabs tabCovers = new CustomCreativeTab(MODID.toLowerCase()+".covers") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return ItemPipeCover.getCover(new CoverData(ModBlocks.crystal.getDefaultState()));
        }
        
        @Override
        public boolean hasSearchBar()
        {
            return true;
        }

		@Override
		public int compare(ItemStack arg0, ItemStack arg1) {
			return ItemUtil.compareNames(arg0, arg1);
		}
    };
    
    public static CreativeTabs tabCrops = new CustomCreativeTab(MODID.toLowerCase()+".crops") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.crystalSapling, 1, BlockCrystalLog.WoodType.BLUE.getMeta());
        }
        
        @Override
        public boolean hasSearchBar()
        {
            return true;
        }

		@Override
		public int compare(ItemStack arg0, ItemStack arg1) {
			if(arg0.getItem() instanceof ItemMaterialSeed){
				if(arg1.getItem() instanceof ItemMaterialSeed){
					IMaterialCrop crop0 = ItemMaterialSeed.getCrop(arg0);
					IMaterialCrop crop1 = ItemMaterialSeed.getCrop(arg1);
					if((crop0 !=null && crop0.getSeedInfo() !=null) && (crop1 !=null && crop1.getSeedInfo() !=null)){
						int tier0 = crop0.getSeedInfo().getTier();
						int tier1 = crop1.getSeedInfo().getTier();
						
						if(tier0 == tier1){
							int index0 = -1;
							int index1 = -1;
							int i = 0;
							for(String key : CrystalModAPI.getCropMap().keySet()){
								if(key.equals(crop0.getUnlocalizedName())){
									index0 = i;
								}
								if(key.equals(crop1.getUnlocalizedName())){
									index1 = i;
								}
								if(index0 !=-1 && index1 !=-1){
									break;
								}
								i++;
							}
							return Integer.compare(index0, index1);
						}
						
						return Integer.compare(tier0, tier1);
					}
				}
			}
			return ItemUtil.compareNames(arg0, arg1);
		}
    };
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CrystalModNetwork.instance.setup();
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
	@EventHandler
	public void missingItems(FMLMissingMappingsEvent event){
		MissingItemHandler.missingFix(event);
	}
	
	@EventHandler
	public void severStart(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandCrystalMod());
	}
	
	public static String resource(String res) {
		return String.format("%s:%s", CrystalMod.MODID.toLowerCase(Locale.US), res);
	}
	
	public static String prefix(String name) {
		return String.format("%s.%s", CrystalMod.MODID.toLowerCase(Locale.US), name);
	}

	public static ResourceLocation resourceL(String string) {
		return new ResourceLocation(resource(string));
	}
	
}
