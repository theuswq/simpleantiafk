package me.theuswq.simpleantiafk.listeners;

import me.theuswq.simpleantiafk.afk.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener responsável por monitorar atividades dos jogadores
 * 
 * @author theuswq
 */
public class PlayerActivityListener implements Listener {

    private final AFKManager afkManager;

    public PlayerActivityListener(AFKManager afkManager) {
        this.afkManager = afkManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Registra o jogador quando ele entra no servidor
        afkManager.registerPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove o jogador quando ele sai do servidor
        afkManager.unregisterPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Atualiza a atividade do jogador quando ele se move ou gira a câmera
        afkManager.updatePlayerActivity(event.getPlayer(), event.getTo());
    }
}

