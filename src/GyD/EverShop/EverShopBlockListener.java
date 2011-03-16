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
import org.bukkit.event.block.BlockRightClickEvent;
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
    
    public void onBlockRightClick(BlockRightClickEvent e)
    {
    	if(e.getBlock().getType().equals(Material.SIGN_POST) || e.getBlock().getType().equals(Material.WALL_SIGN)){
    		
    		// get chest
    		ContainerBlock chest = (ContainerBlock)e.getBlock().getFace(BlockFace.valueOf("DOWN"), 1).getState();
    		
	    	// get player
	    	Player player = e.getPlayer();
	    	
	    	//get sign
	    	Sign sign = (Sign) e.getBlock().getState();
	    	
	    	String l1 = sign.getLines()[1];
	    	String l2 = sign.getLines()[2];
	    	
	    	// get price
	    	int price = Integer.parseInt(l2.substring(2, l2.length()-7));

	    	int amount = Integer.parseInt(l1.substring(2, 4));
	    	
	    	String[] vals = l1.substring(9).split("[ :]+");
	    	
	    	short color = new Short("0");
	    	
	    	if( vals.length == 2 ){
	    		color = Short.valueOf(vals[1]);
	    	}
	    	
	    	//ItemStack prix = new ItemStack(341);
	    	//prix.setAmount(price);
	    	
	    	ItemStack recompense = new ItemStack(getItemId(vals[0]));
	    	recompense.setAmount(amount);
	    	recompense.setDurability(color);
	    	
	    	int slimes = 0;
	    	
	    	for (int i = player.getInventory().first(Material.getMaterial(341)); i < player.getInventory().getSize(); i++) {
	    		ItemStack current = player.getInventory().getItem(i);
	    		
	    		slimes += current.getAmount();
	    	}

	    	player.sendMessage(slimes+"");
	    	
	    	if( slimes >= price )
	    	{
	    		int first = player.getInventory().first(Material.getMaterial(341));
	    		ItemStack playeritem = player.getInventory().getItem(first);
	    		
	    		player.sendMessage(playeritem.getAmount()+"");
	    		
	    		if( playeritem.getAmount() > price )
	    		{
	    			int amount1 = playeritem.getAmount();
	    			playeritem.setAmount(price);
	    			chest.getInventory().addItem(new ItemStack[] { playeritem });
	    			playeritem.setAmount(amount1 - price);
	    		}
	    		else
	    		{
	    			int left = price;
	    			for (int i = player.getInventory().first(Material.getMaterial(341)); i < player.getInventory().getSize(); i++) {
	    				player.sendMessage(left+"");
	    				if (left == 0) {
	                        break;
	                    }
	    				ItemStack current = player.getInventory().getItem(i);
	    				
	    				if( current.getAmount() > left )
	    				{
	                        int amount1 = current.getAmount();
	                        current.setAmount(left);
	                        chest.getInventory().addItem(new ItemStack[] { current });
	                        current.setAmount(amount1 - left);
	                        break;
	    				}
	    					left -= current.getAmount();
	                        chest.getInventory().addItem(new ItemStack[] { current });
	                        player.getInventory().clear(i);
	    			}
	    		}
	    	}
	    	
	    	//player.getInventory().addItem(new ItemStack[] { objet });
	    	
	    	//player.sendMessage(""+objet);
    	}
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
    	if ((blockAgainstMaterial.equals(Material.SIGN_POST)) || (blockAgainstMaterial.equals(Material.WALL_SIGN))){
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
    	if( underblock.getType() == Material.CHEST && text[0].equalsIgnoreCase("§a[shop]") ){
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
		    		
		    		int prix = Integer.parseInt(text[1]);
	                
		    		String color = "";
	                
	                if( item.getType() == Material.WOOL ||  item.getType() == Material.INK_SACK )
	                {
	                	color = ":"+(byte)item.getDurability();
	                	
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
	                
	                String nbr = ""+item.getAmount();;
	                
	                // on convertit à deux chiffres
	                if( item.getAmount() < 10 )
	                {
	                	nbr = "0"+item.getAmount();
	                }
	                
		    		e.setLine(0, "§a[Shop]");
		    		e.setLine(1, "§f"+ nbr + "§ax§f" + item.getType() + color );
		    		e.setLine(2, "§a"+ prix + "§fSlime");
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
    
    public int getItemId(String name) {
        Material[] mat = Material.values();
        for (Material m : mat) {
          if (m.name().toLowerCase().startsWith(name.toLowerCase())) {
            return m.getId();
          }
        }
        return -1;
      }
}
