package chanceCubes.blocks;

import java.util.Random;

import chanceCubes.tileentities.TileCubeDispenser;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockCubeDispenser extends BaseChanceBlock implements ITileEntityProvider
{
	public static final EnumProperty<BlockCubeDispenser.DispenseType> DISPENSING = EnumProperty.<BlockCubeDispenser.DispenseType> create("dispensing", BlockCubeDispenser.DispenseType.class);

	public BlockCubeDispenser()
	{
		super(getBuilder().hardnessAndResistance(2f, Integer.MAX_VALUE), "cube_dispenser");
		this.setDefaultState(this.stateContainer.getBaseState().with(DISPENSING, DispenseType.CHANCE_CUBE));
		// this.setLightOpacity(0);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new TileCubeDispenser();
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		if(!(world.getTileEntity(pos) instanceof TileCubeDispenser))
			return true;

		TileCubeDispenser te = (TileCubeDispenser) world.getTileEntity(pos);
		if(player.isSneaking())
		{
			state = this.cycleProperty(state, DISPENSING);
			world.setBlockState(pos, state, 3);
		}
		else
		{
			if(player.inventory.getCurrentItem() != null)
			{
				Block block = Block.getBlockFromItem(player.inventory.getCurrentItem().getItem());
				if(block != null && block.equals(te.getCurrentBlock(BlockCubeDispenser.getCurrentState(state))))
					player.inventory.decrStackSize(player.inventory.currentItem, 1);
			}
		}
		return true;
	}

	@Override
	public void onBlockClicked(IBlockState state, World world, BlockPos pos, EntityPlayer player)
	{
		if(world.isRemote)
			return;
		if(!(world.getTileEntity(pos) instanceof TileCubeDispenser))
			return;
		TileCubeDispenser te = (TileCubeDispenser) world.getTileEntity(pos);

		EntityItem entitem = te.getNewEntityItem(BlockCubeDispenser.getCurrentState(world.getBlockState(pos)));
		entitem.setLocationAndAngles(player.posX, player.posY, player.posZ, 0, 0);
		if(player.isSneaking())
		{
			entitem.getItem().setCount(1);
			world.spawnEntity(entitem);
		}
		else
		{
			entitem.getItem().setCount(64);
			world.spawnEntity(entitem);
		}
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return null;
	}

	public int getMetaFromState(IBlockState state)
	{
		DispenseType type = getCurrentState(state);
		if(type == DispenseType.CHANCE_CUBE)
			return 0;
		else if(type == DispenseType.CHANCE_ICOSAHEDRON)
			return 1;
		else if(type == DispenseType.COMPACT_GAINTCUBE)
			return 2;
		else
			return 0;
	}

	public IBlockState getStateFromMeta(int meta)
	{
		if(meta == 0)
			return this.getDefaultState().with(DISPENSING, DispenseType.CHANCE_CUBE);
		else if(meta == 1)
			return this.getDefaultState().with(DISPENSING, DispenseType.CHANCE_ICOSAHEDRON);
		else if(meta == 2)
			return this.getDefaultState().with(DISPENSING, DispenseType.COMPACT_GAINTCUBE);
		else
			return this.getDefaultState().with(DISPENSING, DispenseType.CHANCE_CUBE);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(DISPENSING);
	}

	public static enum DispenseType implements IStringSerializable
	{
		CHANCE_CUBE("chance_cube"), CHANCE_ICOSAHEDRON("chance_icosahedron"), COMPACT_GAINTCUBE("compact_gaint_cube");

		private String type;

		private DispenseType(String name)
		{
			this.type = name;
		}

		@Override
		public String getName()
		{
			return this.type;
		}

		public DispenseType getNextState()
		{
			switch(this)
			{
				case CHANCE_CUBE:
					return CHANCE_ICOSAHEDRON;
				case CHANCE_ICOSAHEDRON:
					return COMPACT_GAINTCUBE;
				case COMPACT_GAINTCUBE:
					return CHANCE_CUBE;
				default:
					return CHANCE_CUBE;

			}
		}
	}

	public static DispenseType getNextState(IBlockState state)
	{
		return state.get(DISPENSING).getNextState();
	}

	public static DispenseType getCurrentState(IBlockState state)
	{
		return state.get(DISPENSING);
	}
}