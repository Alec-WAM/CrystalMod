package alec_wam.CrystalMod;

import java.util.Locale;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.fluids.Fluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.commands.CommandCrystalMod;
import alec_wam.CrystalMod.proxy.CommonProxy;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CrystalMod.MODID, name = CrystalMod.MODNAME, version = CrystalMod.VERSION, guiFactory = "alec_wam.CrystalMod.config.ConfigFactoryCM")
public class CrystalMod {
	public static final String MODID = "CrystalMod";
	public static final String MODNAME = "Crystal Mod";
	public static final String VERSION = "1.0.0dev1";
	
	static {
		Fluids.registerBucket();
	}
	
	@Instance(MODID)
	public static CrystalMod instance;
	
	@SidedProxy(clientSide="alec_wam.CrystalMod.proxy.ClientProxy", serverSide="alec_wam.CrystalMod.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabItems = new CreativeTabs(MODID.toLowerCase()+".items") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.crystals;
        }
    };
    
    public static CreativeTabs tabTools = new CreativeTabs(MODID.toLowerCase()+".tools") {
    	@Override
		public Item getTabIconItem() {
			return ModItems.wrench;
		}
    };
    
    public static CreativeTabs tabBlocks = new CreativeTabs(MODID.toLowerCase()+".blocks") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(ModBlocks.crystal);
        }
    };
    
    public static CreativeTabs tabCovers = new CreativeTabs(MODID.toLowerCase()+".covers") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.pipeCover;
        }
        
        @SideOnly(Side.CLIENT)
        public ItemStack getIconItemStack()
        {
            return ItemPipeCover.getCover(new CoverData(ModBlocks.crystal.getDefaultState()));
        }
        
        @Override
        public boolean hasSearchBar()
        {
            return true;
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
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
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
	
}
