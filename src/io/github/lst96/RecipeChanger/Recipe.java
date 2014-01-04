package io.github.lst96.RecipeChanger;

import io.github.lst96.RecipeChanger.Commands.Recipereload;
import io.github.lst96.RecipeChanger.Listeners.Crafting;
import io.github.lst96.RecipeChanger.Listeners.Permissions;
import io.github.lst96.RecipeChanger.metrics.Metrics;

import java.io.IOException;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Recipe extends JavaPlugin
{
  public final Logger logger = Logger.getLogger("Minecraft");
  public PluginDescriptionFile pdfFile;
  public String PREFIX;
  public boolean autoUpdate = false;
  Updater updater;
  private boolean compatible;

  public void onEnable()
  {
    this.pdfFile = getDescription();
    this.PREFIX = ("[" + this.pdfFile.getName() + "]");
    this.logger.info(this.PREFIX + " Recipe Changer version " + this.pdfFile.getVersion() + " has been enabled.");
    this.logger.info(this.PREFIX + " Developed by: " + this.pdfFile.getAuthors());
    getConfig().options().copyDefaults(true);
    saveConfig();
    Crafting cr = new Crafting(this);
    cr.SetupCrafting();
    getCommand("recipereload").setExecutor(new Recipereload(this));
    getServer().getPluginManager().registerEvents(new Crafting(this), this);
    getServer().getPluginManager().registerEvents(new Permissions(this), this);
    try {
	      Metrics metrics = new Metrics(this);
	      metrics.start();
	    }
	    catch (IOException localIOException) {
	    }
	autoUpdate = this.getConfig().getBoolean("autoupdate");
	if(autoUpdate) {
		setupUpdater();
	String mcVersion = Bukkit.getBukkitVersion();
    this.compatible = mcVersion.startsWith("1.7.2");
    if ((this.getConfig().getBoolean("check_bukkit_compatibility")) && (!this.compatible)) {
      this.logger.info("[Recipe Changer] is not compatible with " + Bukkit.getVersion());
      getServer().getPluginManager().disablePlugin(this);
      return;
	}
  }
}
  public void onDisable() {
    this.logger.info(this.PREFIX + " Recipe Changer Disabled.");
    Bukkit.getServer().clearRecipes();
  }
  private void setupUpdater() {
		
		Updater updater = new Updater(this, 60831, this.getFile(), Updater.UpdateType.DEFAULT, true);
		Updater.UpdateResult result = updater.getResult();
      switch(result)
      {
          case SUCCESS:
              // Success: The updater found an update, and has readied it to be loaded the next time the server restarts/reloads
              break;
          case NO_UPDATE:
              // No Update: The updater did not find an update, and nothing was downloaded.
              break;
          case DISABLED:
              // Won't Update: The updater was disabled in its configuration file.
              break;
          case FAIL_DOWNLOAD:
              // Download Failed: The updater found an update, but was unable to download it.
              break;
          case FAIL_DBO:
              // dev.bukkit.org Failed: For some reason, the updater was unable to contact DBO to download the file.
              break;
          case FAIL_NOVERSION:
              // No version found: When running the version check, the file on DBO did not contain the a version in the format 'vVersion' such as 'v1.0'.
              break;
          case FAIL_BADID:
              // Bad id: The id provided by the plugin running the updater was invalid and doesn't exist on DBO.
              break;
          case FAIL_APIKEY:
              // Bad API key: The user provided an invalid API key for the updater to use.
              break;
          case UPDATE_AVAILABLE:
            // There was an update found, but because you had the UpdateType set to NO_DOWNLOAD, it was not downloaded.
      }
	}
}
