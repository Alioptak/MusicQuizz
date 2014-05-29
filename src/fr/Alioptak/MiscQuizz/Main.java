package fr.Alioptak.MiscQuizz;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.xxmicloxx.NoteBlockAPI.NoteBlockSongPlayer;

public class Main extends JavaPlugin implements Listener{
	
	Scoreboard board;
	Scoreboard board2;
	
	int numéro_joueur;
	int total_joueurs = 16;
	int deco;	
	int sec;
	int min_players;
	
	boolean start = true;
	boolean join = true;
	boolean force_start = false;
	
	private ArrayList<Player> Liste_joueurs = new ArrayList<Player>();

	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);	
		getConfig().options().copyDefaults(true);
		saveConfig();
	}



	@SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
		

		if(cmd.getName().equalsIgnoreCase("mquizz")){
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			board = manager.getNewScoreboard();
			final Objective objective = board.registerNewObjective("Test", "Test2");
			if(sender instanceof Player){
				if(args.length < 1){
					
						sender.sendMessage(ChatColor.RED+"Erreur:");
						sender.sendMessage(ChatColor.RED+" - /mquizz join");
						sender.sendMessage(ChatColor.RED+" - /mquizz start");
						sender.sendMessage(ChatColor.RED+" - /mquizz stop");
						sender.sendMessage(ChatColor.RED+" - /mquizz top");
						sender.sendMessage(ChatColor.RED+" - /mquizz leave");
						sender.sendMessage(ChatColor.RED+" - /mquizz setminplayers");
						sender.sendMessage(ChatColor.RED+" - /mquizz setstart");

						
				}else{
				if(args[0].equalsIgnoreCase("join")){
					if(join == false){
						sender.sendMessage(ChatColor.RED+"Erreur : La partie est déjà lancée.");
					}else{
					if((Liste_joueurs.contains((Player)sender))){
						sender.sendMessage(ChatColor.RED+"Erreur : tu es deja en partie");
						}else{
							numéro_joueur++;
							
							//partie scoreboard
							
							objective.setDisplayName(ChatColor.GRAY + "Mquizz commence dans : " +ChatColor.GOLD+"--");
							objective.setDisplaySlot(DisplaySlot.SIDEBAR);
							
							//partie attribution des points au scoreboard
							
							Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
								
								@Override
								public void run() {
									for(Player p : Liste_joueurs){
									Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Joueurs :"));
									score.setScore(numéro_joueur);
									Score score1 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Joueurs min :"));
									score1.setScore(getConfig().getInt("min_players"));
								}}
							}, 0, 10);
					
							//partie attribution de scoreboard au joueur
							((Player) sender).setScoreboard(board);
							Liste_joueurs.add((Player) sender);
							
							min_players = getConfig().getInt("min_players");
							
							if(start == false){
								join = false;
							}
							if(numéro_joueur >= 2){
								force_start = true;
							}else{
								force_start = false;
							}
						}}
				}
				if(args[0].equalsIgnoreCase("start")){
					if(start == true){
					if(force_start != true){
						sender.sendMessage(ChatColor.RED+"Erreur : Le nombre minimum de joueurs requis n'est pas encore atteint.");
					}else{
						sec = getConfig().getInt("Cooldown");
						if(sec < 5){
							sender.sendMessage(ChatColor.RED+"Erreur : Le cooldown n'a pas été défini : /mquizz setstart <numéro>");
						}else{
						start = false;
						for(Player p : Liste_joueurs){	
						p.sendMessage(ChatColor.GREEN+"La partie va commancer dans : "+getConfig().getInt("Cooldown")+" secondes.");
						}
						Bukkit.getScheduler().cancelAllTasks();
							sec = getConfig().getInt("Cooldown");
							if(sec < 5){
								Bukkit.broadcastMessage(ChatColor.RED+"Erreur : Le cooldown qui est affiché dans le fichié de config est inféieur à 5.");
							}else{
							Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
								
								@Override
								public void run() {
										//ici scoreboard
									objective.setDisplayName(ChatColor.GRAY + "Mquizz commence dans : " +ChatColor.GOLD+ sec+"s");
									sec--;
									objective.setDisplaySlot(DisplaySlot.SIDEBAR);
									Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Joueurs :"));
									score.setScore(numéro_joueur);
									for(Player p : Liste_joueurs){
										p.setScoreboard(board);
									}
									if(sec < 0){
										Bukkit.getScheduler().cancelAllTasks();
										for(Player p : Liste_joueurs){
											p.sendMessage(ChatColor.GREEN+"La partie vient de débuter! Bon jeu.");
										}
									}
								}
							}, 0, 20);
						}
						}
					}}else{
						sender.sendMessage(ChatColor.RED+"La partie à déjà été lancée");
					}
				}
				if(args[0].equalsIgnoreCase("setstart")){
					if(start == true){
					
					if(args.length == 2){
					if(Integer.parseInt(args[1]) < 5 || Integer.parseInt(args[1]) > 59){
						
						sender.sendMessage(ChatColor.RED+"Erreur : Le numéro entré n'est pas valide : "+args[1]+" < 5 ou > 59 secondes");
					}else{
						sec = Integer.parseInt(args[1]);
						getConfig().set("Cooldown", sec);
						saveConfig();
						sender.sendMessage(ChatColor.GREEN+"Le cooldown a bien été changé à "+sec+" secondes.");
						
					}}else{
						sender.sendMessage(ChatColor.RED+"Erreur : Veuillez entrer un numéro supérieur à 4.");
					}
				}else{
					sender.sendMessage(ChatColor.RED+"Erreur : Le cooldown ne peut pas être modifié lorsque la partie à déjà débuté.");}
				}
				if(args[0].equalsIgnoreCase("setminplayers")){
					if(args.length == 2){
						if(Integer.parseInt(args[1]) < 2 || Integer.parseInt(args[1]) > 200){
							sender.sendMessage(ChatColor.RED+"Erreur : Le numéro entré n'est pas valide : "+args[1]+" < 2 ou > 200 joueurs");
						}else{
							min_players = Integer.parseInt(args[1]);
							getConfig().set("min_players", min_players);
							saveConfig();
							sender.sendMessage(ChatColor.GREEN+"Le nombre minimum de joueurs a bien été changé à "+min_players+" joueurs.");
						}}else{
							sender.sendMessage(ChatColor.RED+"Erreur : Veuillez entrer un numéro supérieur à 1.");
						}
					
					
				}
			}
		}}
		
		
		return false;
		

}
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		if(Liste_joueurs.contains((Player) e.getPlayer())){
			e.setQuitMessage(ChatColor.YELLOW+""+e.getPlayer().getName()+" a quitté le serveur et la partie MQuizz.");
			numéro_joueur--;
			Liste_joueurs.remove(e.getPlayer());

				}else{
					e.setQuitMessage(ChatColor.YELLOW+""+e.getPlayer().getName()+" a quitté le serveur.");
				}
		
				
		
			
		}
	
	
	
	}
		

		

	