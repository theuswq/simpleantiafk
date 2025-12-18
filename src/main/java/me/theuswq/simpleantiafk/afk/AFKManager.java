package me.theuswq.simpleantiafk.afk;

import me.theuswq.simpleantiafk.SimpleAntiAFKPlugin;
import me.theuswq.simpleantiafk.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gerenciador principal do sistema Anti-AFK
 * 
 * @author theuswq
 */
public class AFKManager {

    private final SimpleAntiAFKPlugin plugin;
    private final Map<UUID, AFKData> afkDataMap;
    private BukkitTask checkTask;
    
    private int maxTimeSeconds;
    private int warningSeconds;
    private String action;
    private String command;
    private String kickMessage;

    public AFKManager(SimpleAntiAFKPlugin plugin) {
        this.plugin = plugin;
        this.afkDataMap = new HashMap<>();
        loadConfig();
    }

    /**
     * Carrega as configurações do arquivo config.yml
     */
    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        
        this.maxTimeSeconds = config.getInt("afk.max-time-seconds", 300);
        this.warningSeconds = config.getInt("afk.warning-seconds", 30);
        this.action = config.getString("afk.action", "KICK").toUpperCase();
        this.command = config.getString("afk.command", "spawn {player}");
        this.kickMessage = config.getString("afk.kick-message", 
            "&cVocê foi kickado por ficar AFK por muito tempo!");
    }

    /**
     * Recarrega as configurações
     */
    public void reload() {
        loadConfig();
    }

    /**
     * Inicia o scheduler que verifica jogadores AFK
     */
    public void start() {
        if (checkTask != null && !checkTask.isCancelled()) {
            checkTask.cancel();
        }

        // Executa a verificação a cada segundo (20 ticks)
        checkTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkAFKPlayers, 0L, 20L);
    }

    /**
     * Para o scheduler
     */
    public void stop() {
        if (checkTask != null && !checkTask.isCancelled()) {
            checkTask.cancel();
        }
        afkDataMap.clear();
    }

    /**
     * Registra um jogador para monitoramento
     */
    public void registerPlayer(Player player) {
        if (player.hasPermission("simpleantiafk.bypass")) {
            return;
        }
        
        AFKData data = new AFKData();
        data.updateLocation(player.getLocation());
        afkDataMap.put(player.getUniqueId(), data);
    }

    /**
     * Remove um jogador do monitoramento
     */
    public void unregisterPlayer(UUID uuid) {
        afkDataMap.remove(uuid);
    }

    /**
     * Atualiza a atividade de um jogador
     */
    public void updatePlayerActivity(Player player, Location location) {
        if (player.hasPermission("simpleantiafk.bypass")) {
            return;
        }

        UUID uuid = player.getUniqueId();
        AFKData data = afkDataMap.get(uuid);

        if (data == null) {
            registerPlayer(player);
            return;
        }

        // Verifica se o jogador se moveu ou girou
        if (data.hasMoved(location)) {
            data.updateActivity();
            data.updateLocation(location);
        }
    }

    /**
     * Verifica todos os jogadores AFK e aplica ações
     */
    private void checkAFKPlayers() {
        for (Map.Entry<UUID, AFKData> entry : afkDataMap.entrySet()) {
            UUID uuid = entry.getKey();
            AFKData data = entry.getValue();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                afkDataMap.remove(uuid);
                continue;
            }

            // Verifica se o jogador ainda tem permissão de bypass
            if (player.hasPermission("simpleantiafk.bypass")) {
                afkDataMap.remove(uuid);
                continue;
            }

            long inactiveSeconds = data.getInactiveTimeSeconds();
            long timeUntilAction = maxTimeSeconds - inactiveSeconds;

            // Envia aviso se necessário
            if (timeUntilAction <= warningSeconds && timeUntilAction > 0 && !data.isWarned()) {
                sendWarning(player, inactiveSeconds);
                data.setWarned(true);
            }

            // Aplica ação se o tempo máximo foi excedido
            if (inactiveSeconds >= maxTimeSeconds) {
                applyAction(player);
                afkDataMap.remove(uuid);
            }
        }
    }

    /**
     * Envia aviso ao jogador
     */
    private void sendWarning(Player player, long inactiveSeconds) {
        String message = plugin.getConfig().getString("messages.warning",
            "&e⚠ Atenção! Você está AFK há {time} segundos. Você será {action} em breve!");
        
        String actionText = action.equals("KICK") ? "kickado" : "punido";
        message = message.replace("{time}", String.valueOf(inactiveSeconds))
                        .replace("{action}", actionText);
        
        player.sendMessage(message.replace("&", "§"));
    }

    /**
     * Aplica a ação configurada ao jogador AFK
     */
    private void applyAction(Player player) {
        switch (action) {
            case "KICK":
                String kickMsg = kickMessage.replace("&", "§");
                player.kickPlayer(kickMsg);
                plugin.getLogger().info("Jogador " + player.getName() + " foi kickado por AFK.");
                break;

            case "COMMAND":
                String cmd = command.replace("{player}", player.getName());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                });
                plugin.getLogger().info("Comando executado para " + player.getName() + ": " + cmd);
                break;

            default:
                plugin.getLogger().warning("Ação desconhecida: " + action);
                break;
        }
    }

    /**
     * Retorna o número de jogadores sendo monitorados
     */
    public int getTrackedPlayersCount() {
        return afkDataMap.size();
    }
}

