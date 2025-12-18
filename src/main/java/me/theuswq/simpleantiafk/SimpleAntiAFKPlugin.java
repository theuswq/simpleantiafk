package me.theuswq.simpleantiafk;

import me.theuswq.simpleantiafk.afk.AFKManager;
import me.theuswq.simpleantiafk.listeners.PlayerActivityListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * SimpleAntiAFK - Plugin profissional para detectar e punir jogadores AFK
 * 
 * @author theuswq
 * @version 1.0.0
 */
public class SimpleAntiAFKPlugin extends JavaPlugin {

    private AFKManager afkManager;
    private PlayerActivityListener activityListener;

    @Override
    public void onEnable() {
        // Salvar config padrão se não existir
        saveDefaultConfig();
        
        // Inicializar componentes
        this.afkManager = new AFKManager(this);
        this.activityListener = new PlayerActivityListener(afkManager);
        
        // Registrar listener
        getServer().getPluginManager().registerEvents(activityListener, this);
        
        // Iniciar scheduler do AFKManager
        afkManager.start();
        
        getLogger().info("SimpleAntiAFK v" + getDescription().getVersion() + " ativado com sucesso!");
        getLogger().info("Criado por theuswq");
    }

    @Override
    public void onDisable() {
        // Parar scheduler e limpar dados
        if (afkManager != null) {
            afkManager.stop();
        }
        
        getLogger().info("SimpleAntiAFK desativado.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("antiafk")) {
            return false;
        }

        if (!sender.hasPermission("simpleantiafk.admin")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eUso: /antiafk [reload|status]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                try {
                    reloadConfig();
                    afkManager.reload();
                    sender.sendMessage(getConfig().getString("messages.reload-success", 
                        "§aConfiguração recarregada com sucesso!"));
                } catch (Exception e) {
                    getLogger().severe("Erro ao recarregar configuração: " + e.getMessage());
                    e.printStackTrace();
                    sender.sendMessage(getConfig().getString("messages.reload-error", 
                        "§cErro ao recarregar configuração. Verifique o console."));
                }
                break;

            case "status":
                int count = afkManager.getTrackedPlayersCount();
                sender.sendMessage(getConfig().getString("messages.status-online", 
                    "§aPlugin ativo. {count} jogador(es) sendo monitorado(s).")
                    .replace("{count}", String.valueOf(count)));
                break;

            default:
                sender.sendMessage("§eUso: /antiafk [reload|status]");
                break;
        }

        return true;
    }

    public AFKManager getAFKManager() {
        return afkManager;
    }
}

