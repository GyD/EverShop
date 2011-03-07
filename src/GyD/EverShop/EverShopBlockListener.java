package GyD.EverShop;

import java.io.File;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Sign;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.util.config.Configuration;

/**
 * EverShop block listener
 * @author GyD
 */
public class EverShopBlockListener extends BlockListener {
    private final EverShop plugin;
    //public Configuration EverShopConfig = new Configuration(new File("plugins/EverShop/config.yml"));

    public EverShopBlockListener(final EverShop plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockPlace(BlockPlaceEvent e)
    {
    	// get seller
    	Player seller = e.getPlayer();
    	// get block
    	Block block = e.getBlock();
    	// get block type
    	Material blockMat = block.getType();
    	// get block agains
    	Block blockAgainst = e.getBlockAgainst();
    	// get block agains type
    	Material blockAgainstMaterial = blockAgainst.getType();
    	
    	// please don't place block on signs!
    	if ((blockAgainstMaterial.equals(Material.SIGN)) || (blockAgainstMaterial.equals(Material.SIGN_POST)) || (blockAgainstMaterial.equals(Material.WALL_SIGN))){
    		if( isShop(blockAgainst) )
    			e.setCancelled(true);
    	}
    	else
    	if (block.equals(Material.CHEST))
    	{
    		seller.sendMessage("En placant un panneau au dessus du coffre vous pouvez créer un magasin.");
    	}
    }
    
    public boolean isShop(Block block)
    {
    	// get the sign
    	Sign sign = (Sign)block.getState();
    	// get the sign content
    	String[] text = sign.getLines();
    	// get the underblock
    	Block underblock = block.getFace(BlockFace.valueOf("DOWN"), 1);
    	
    	// is this a Shop ?
    	if( underblock.getType() == Material.CHEST && text[0].equalsIgnoreCase("[shop]") ){
        	// yes it is
    		return true;
    	}
    	
    	// no
    	return false;
    }

    public void onSignChange(SignChangeEvent e)
    {
    	// get underblock
    	Block underblock = e.getBlock().getFace(BlockFace.valueOf("DOWN"), 1);
    	
    	// get seller
    	Player seller = e.getPlayer();
    	
    	// get sign text
    	String[] text = e.getLines();
    	
    	// chest under?
    	if( underblock.getType() == Material.CHEST )
    	{
    		// get the chest
    		ContainerBlock chest = (ContainerBlock)underblock.getState();
    		
    		// does the sign contain [Shop] ?
	    	if (
	    			text[0].equalsIgnoreCase("[Shop]")
	    	) {
	    		// is the second line a int (price)
	    		if( isInt(text[1]) )
	    		{
	    			// what do you sell? (what's inside the chest)
		    		ItemStack item = null;
	                for (int i = 0; i < chest.getInventory().getSize(); i++) {
	                  if (chest.getInventory().getItem(i) == null) {
	                    continue;
	                  }
	                  if (chest.getInventory().getItem(i).getAmount() > 0) {
	                    item = chest.getInventory().getItem(i);
	                    break;
	                  }
	                }
		    		
		    		//String[] l1 = text[1].split("[ :-]+");
	                int prix = Integer.parseInt(text[1]);
	                
	                //String woolcolor = "0";
	                
	                if( item.getType() == Material.WOOL )
	                {
	                	//MaterialData data = item.getData();
	                	 
	                	/*if (data instanceof Wool) {
	                	    Wool wool = (Wool)data;
	                	    DyeColor color = (DyeColor)wool.getColor();
	                	    seller.sendMessage("You have a " + color + " wool!");
	                	}
	                	*/
	                    
	                    /*WHITE 	
	                    Represents white dye. 
	                    ORANGE 	
	                    Represents orange dye. 
	                    MAGENTA 	
	                    Represents magenta dye. 
	                    LIGHT_BLUE 	
	                    Represents light blue dye. 
	                    YELLOW 	
	                    Represents yellow dye. 
	                    LIME 	
	                    Represents lime dye. 
	                    PINK 	
	                    Represents pink dye. 
	                    GRAY 	
	                    Represents gray dye. 
	                    SILVER 	
	                    Represents silver dye. 
	                    CYAN 	
	                    Represents cyan dye. 
	                    PURPLE 	
	                    Represents purple dye. 
	                    BLUE 	
	                    Represents blue dye. 
	                    BROWN 	
	                    Represents brown dye. 
	                    GREEN 	
	                    Represents green dye. 
	                    RED 	
	                    Represents red dye. 
	                    BLACK*/
	                }
	                
		    		e.setLine(0, "§a[Shop]");
		    		e.setLine(1, "§f"+ item.getAmount() + "§ax§f" + item.getType() );
		    		e.setLine(2, "§a"+ prix + "§fz");
		    		e.setLine(3, "§8" + seller.getName());
		    	}
		    }
    	}
    }
    
    // is this string an int?
    public boolean isInt(String i)
    {
      try
      {
        Integer.parseInt(i);
        return true; } catch (NumberFormatException nfe) {
      }
      return false;
    }
}
