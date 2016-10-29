package alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import alec_wam.CrystalMod.api.estorage.ICraftingTask;
import alec_wam.CrystalMod.tiles.ISynchronizedContainer;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;

public class TileEntityPanelMonitor extends TileEntityPanel implements ISynchronizedContainer {
	private List<ClientSideCraftingTask> tasks = new ArrayList<ClientSideCraftingTask>();
	
	@Override
    public void writeContainerData(ByteBuf buf) {

        if (connected && getNetwork() !=null && getNetwork().craftingController !=null) {
            buf.writeInt(getNetwork().craftingController.getCraftingTasks().size());

            for (ICraftingTask task : getNetwork().craftingController.getCraftingTasks()) {
                ByteBufUtils.writeUTF8String(buf, task.getInfo());

                buf.writeInt(task.getPattern().getOutputs().size());

                for (ItemStack output : task.getPattern().getOutputs()) {
                    ByteBufUtils.writeItemStack(buf, output);
                }
            }
        } else {
            buf.writeInt(0);
        }
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        int size = buf.readInt();

        List<ClientSideCraftingTask> newTasks = new ArrayList<ClientSideCraftingTask>();

        for (int i = 0; i < size; ++i) {
            String info = ByteBufUtils.readUTF8String(buf);

            int outputs = buf.readInt();

            for (int j = 0; j < outputs; ++j) {
                newTasks.add(new ClientSideCraftingTask(ByteBufUtils.readItemStack(buf), i, info));
            }
        }

        Collections.reverse(newTasks);

        tasks = newTasks;
    }

    public List<ClientSideCraftingTask> getTasks() {
        return tasks;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPanelMonitor.class;
    }
	
	public class ClientSideCraftingTask {
        public ItemStack output;
        public int id;
        public String info;

        public ClientSideCraftingTask(ItemStack output, int id, String info) {
            this.output = output;
            this.id = id;
            this.info = info;
        }
    }

}
