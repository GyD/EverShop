package GyD.EverShop;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Sign;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

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
    
    @Override
	public void onBlockDamage(BlockDamageEvent e)
    {
		// get player
    	Player player = e.getPlayer();
    	
    	if (isBank(e.getBlock()))
    	{
    		if( !player.getName().equalsIgnoreCase("GyD") )
    		{
    			player.sendMessage("§cPas touche tit con!");
    			e.setCancelled(true);
    		}
    	}
    	else
    	{
    		if (isShop(e.getBlock()))
        	{
    			//get sign
    	    	Sign sign = (Sign) e.getBlock().getState();
    	    	
    	    	// get sign owner
    	    	String owner = sign.getLines()[3].substring(2);
    	    	
        		if( !player.getName().equalsIgnoreCase(owner) && !player.getName().equalsIgnoreCase("GyD") )
        		{
        			player.sendMessage("§cC'est le Shop de §f"+owner);
        			e.setCancelled(true);
        		}
        	}
    	}
    }
    
    
    @Override
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
    
    
    public static boolean isShop(Block block)
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
    
    public static boolean isBank(Block block)
    {
    	if ((block.getType().equals(Material.SIGN_POST)) || (block.getType().equals(Material.WALL_SIGN)))
    	{
	    	// get the sign
	    	Sign sign = (Sign)block.getState();
	    	// get the sign content
	    	String[] text = sign.getLines();
	    	
	    	// is this a Bank ?
	    	if( text[0].equalsIgnoreCase("§a[bank]") ){
	        	// yes it is
	    		return true;
	    	}
    	}
    	
    	// no
    	return false;
    }

    @Override
	public void onSignChange(SignChangeEvent e)
    {
    	// get underblock
    	Block underblock = e.getBlock().getFace(BlockFace.valueOf("DOWN"), 1);
    	
    	// get seller
    	Player seller = e.getPlayer();
    	
    	// get sign text
    	String[] text = e.getLines();
    	
    	// does the sign contain [Shop] ?
    	if (
    			text[0].equalsIgnoreCase("[Bank]")
    	) {
    		
    		if( seller.getName().toString().equalsIgnoreCase("GyD") )
    		{
    			e.setLine(0, "§a[Bank]");
    		}
    		return;
    	}
    	
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
	    			boolean empty = true;
	    			
	    			// what do you sell? (what's inside the chest)
		    		ItemStack item = null;
	                for (int i = 0; i < chest.getInventory().getSize(); i++) {
	                  if (chest.getInventory().getItem(i) == null) {
	                    continue;
	                  }
	                  if (chest.getInventory().getItem(i).getAmount() > 0) {
	                	empty = false;
	                    item = chest.getInventory().getItem(i);
	                    break;
	                  }
	                }
	                
	                if( empty )
	                {
	                	seller.sendMessage("§cLe coffre ne peut pas être vide");
	                	return;
	                }
		    		
		    		int prix = Integer.parseInt(text[1]);
	                
		    		String color = "";
	                
	                if( item.getType() == Material.WOOL ||  item.getType() == Material.INK_SACK )
	                {
	                	color = ":"+(byte)item.getDurability();
	                }
	                else if((byte)item.getDurability() != 0)
	                {
	                	seller.sendMessage("§cVous ne pouvez pas vendre un objet usagé");
	                	return;
	                }
	                
	                String nbr = ""+item.getAmount();;
	                
	                /* on convertit à deux chiffres
	                if( item.getAmount() < 10 )
	                {
	                	nbr = "0"+item.getAmount();
	                }*/

	                String name = rename(item.getType() + color);
	                
	                e.setLine(0, "§a[Shop]");
		    		e.setLine(1, name );
		    		e.setLine(2, nbr + "/" +  prix + " Slime");
		    		e.setLine(3, "§8" + seller.getName());
		    		//e.setLine(3, "§8" + "dante231");
		    	}
		    }
    	}
    }
    
    public static String rename(String name)
    {
    	
    	
    	String[] vals = new String[] {
    			"WOOL:1|WOOL:ORANGE",
    			"WOOL:2|WOOL:MAGENTA",
    			"WOOL:3|WOOL:L_BLUE",
    			"WOOL:4|WOOL:YELLOW",
    			"WOOL:5|WOOL:LIME",
    			"WOOL:6|WOOL:PINK",
    			"WOOL:7|WOOL:GRAY",
    			"WOOL:8|WOOL:SILVER",
    			"WOOL:9|WOOL:CYAN",
    			"WOOL:10|WOOL:PURPLE",
    			"WOOL:11|WOOL:BLUE",
    			"WOOL:12|WOOL:BROWN",
    			"WOOL:13|WOOL:GREEN",
    			"WOOL:14|WOOL:RED",
    			"WOOL:15|WOOL:BLACK",
    			"INK_SACK:0|INK_SAC",
    			"INK_SACK:1|ROSE_RED",
    			"INK_SACK:2|CACTUS_GREEN",
    			"INK_SACK:3|COCOA_BEANS",
    			"INK_SACK:4|LAPIS_LAZULI",
    			"INK_SACK:5|PURPLE_DYE",
    			"INK_SACK:6|CYAN_DYE",
    			"INK_SACK:7|L_GRAY_DYE",
    			"INK_SACK:8|GRAY_DYE",
    			"INK_SACK:9|PINK_DYE",
    			"INK_SACK:10|LIME_DYE",
    			"INK_SACK:11|DANDE_YELLOW",
    			"INK_SACK:12|L_BLUE_DYE",
    			"INK_SACK:13|MAGENTA_DYE",
    			"INK_SACK:14|ORANGE_DYE",
    			"INK_SACK:15|BONE_MEAL" };
    	
    	String originalName = name;
    	
    	for(int i = vals.length-1; i >= 0; i-- ){
    		String[] valeurs = vals[i].split("[|]");
    		
    		name = name.replace(valeurs[0], valeurs[1]);
    		if( name == originalName)
    		{
    			name = name.replace(valeurs[1], valeurs[0]);
    		}
    		
    		if( name != originalName )
    			return name;
    	}
    	
    	return name;
    }
    
    // is this string an int?
    public static boolean isInt(String i)
    {
      try
      {
        Integer.parseInt(i);
        return true; } catch (NumberFormatException nfe) {
      }
      return false;
    }
    
    public static int getItemId(String name) {
        Material[] mat = Material.values();
        for (Material m : mat) {
          if (m.name().toLowerCase().startsWith(name.toLowerCase())) {
            return m.getId();
          }
        }
        return -1;
      }
}
