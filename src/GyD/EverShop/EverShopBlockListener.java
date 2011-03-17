package GyD.EverShop;

import java.io.File;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
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
    	if(isShop(e.getBlock())){
    		
    		// get player
	    	Player player = e.getPlayer();
	    	
	    	// get chest
    		ContainerBlock chest = (ContainerBlock)e.getBlock().getFace(BlockFace.valueOf("DOWN"), 1).getState();
	    	
	    	//get sign
	    	Sign sign = (Sign) e.getBlock().getState();
	    	
	    	String l1 = sign.getLines()[1];
	    	String l2 = sign.getLines()[2];
	    	
	    	// get price
	    	int price = Integer.parseInt(l2.substring(2, l2.length()-7));

	    	// get amount of items sold 
	    	int sellamount = Integer.parseInt(l1.substring(2, 4));
	    	
	    	String[] vals = l1.substring(9).split("[ :]+");
	    	
	    	short color = new Short("0");
	    	
	    	if( vals.length == 2 ){
	    		color = Short.valueOf(vals[1]);
	    	}
	    	
	    	// get user slimes
	    	int userslimes = 0;
	    	// get shop amount
	    	int count = 0;
	    	
	    	int first = player.getInventory().first(Material.getMaterial(341));
    		
    		// check if the chest is empty or not!
    		if( first == -1 )
    		{
    			player.sendMessage("Vous n'avez pas de Slime");
    			return ;
    		}
	    	
	    	// count userslimes
	    	for (int i = player.getInventory().first(Material.getMaterial(341)); i < player.getInventory().getSize(); i++) {
	    		ItemStack current = player.getInventory().getItem(i);
	    		
	    		userslimes += current.getAmount();
	    	}

	    	// enouth slimes?
	    	if( userslimes >= price )
	    	{
	    		// check if shop have material
	    		first = chest.getInventory().first(Material.getMaterial(vals[0]));
	    		
	    		// check if the chest is empty or not!
	    		if( first == -1 )
	    		{
	    			player.sendMessage("Il n'y a plus rien a vendre dans ce coffre! (1)");
	    			return ;
	    		}
	    		
	    		// get material amount
	    		for (int i = chest.getInventory().first(Material.getMaterial(vals[0])); i < chest.getInventory().getSize(); i++) {
		    		ItemStack current = chest.getInventory().getItem(i);
		    		
		    		count += current.getAmount();
		    	}
	    		
	    		// check if the chest have enouth
	    		if( count < sellamount )
	    		{
	    			player.sendMessage("Il n'y a plus rien a vendre dans ce coffre!");
	    			return ;
	    		}
	    		
	    		// get the first item
	    		ItemStack item = chest.getInventory().getItem(first);
	    		
	    		if( item.getAmount() > sellamount )
	    		{
	    			int amount1 = item.getAmount();
	    			item.setAmount(price);
	    			player.getInventory().addItem(new ItemStack[] { item });
	    			item.setAmount(amount1 - price);
	    		}
	    		
	    		// check color
	    		if( item.getDurability() != color )
	    		{
	    			player.sendMessage("Il y a une erreur avec ce coffre");
	    			return ;
	    		}
	    		first = player.getInventory().first(Material.getMaterial(341));
	    		item = player.getInventory().getItem(first);
	    		
	    		// 
	    		if( item.getAmount() > price )
	    		{
	    			int amount1 = item.getAmount();
	    			item.setAmount(price);
	    			chest.getInventory().addItem(new ItemStack[] { item });
	    			item.setAmount(amount1 - price);
	    		}
	    		else
	    		{
	    			int left = price;
	    			for (int i = player.getInventory().first(Material.getMaterial(341)); i < player.getInventory().getSize(); i++) {
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
	    	else
	    	{
	    		player.sendMessage("Vous n'avez pas assez de slime");
	    	}
	    	
	    	//player.getInventory().addItem(new ItemStack[] { objet });
	    	
	    	//player.sendMessage(""+objet);
	    	CraftPlayer cPlayer = ((CraftPlayer)e.getPlayer());
	    	cPlayer.getHandle().l();
    	}
    }*/
    
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
    	if ((block.getType().equals(Material.SIGN_POST)) || (block.getType().equals(Material.WALL_SIGN)))
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
