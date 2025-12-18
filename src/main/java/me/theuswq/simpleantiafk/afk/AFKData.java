package me.theuswq.simpleantiafk.afk;

import org.bukkit.Location;

/**
 * Classe que armazena os dados de AFK de um jogador
 * 
 * @author theuswq
 */
public class AFKData {
    
    private long lastActivityTime;
    private Location lastLocation;
    private float lastYaw;
    private float lastPitch;
    private boolean warned;

    public AFKData() {
        this.lastActivityTime = System.currentTimeMillis();
        this.warned = false;
    }

    /**
     * Atualiza o tempo da última atividade do jogador
     */
    public void updateActivity() {
        this.lastActivityTime = System.currentTimeMillis();
        this.warned = false;
    }

    /**
     * Atualiza a localização e rotação do jogador
     */
    public void updateLocation(Location location) {
        this.lastLocation = location.clone();
        this.lastYaw = location.getYaw();
        this.lastPitch = location.getPitch();
    }

    /**
     * Verifica se o jogador se moveu ou girou a câmera
     */
    public boolean hasMoved(Location currentLocation) {
        if (lastLocation == null) {
            updateLocation(currentLocation);
            return false;
        }

        // Verifica movimento
        if (lastLocation.getX() != currentLocation.getX() ||
            lastLocation.getY() != currentLocation.getY() ||
            lastLocation.getZ() != currentLocation.getZ()) {
            return true;
        }

        // Verifica rotação (yaw ou pitch mudaram significativamente)
        float yawDiff = Math.abs(currentLocation.getYaw() - lastYaw);
        float pitchDiff = Math.abs(currentLocation.getPitch() - lastPitch);
        
        // Considera movimento se a rotação mudou mais de 0.1 graus
        return yawDiff > 0.1f || pitchDiff > 0.1f;
    }

    /**
     * Retorna o tempo em milissegundos desde a última atividade
     */
    public long getInactiveTime() {
        return System.currentTimeMillis() - lastActivityTime;
    }

    /**
     * Retorna o tempo em segundos desde a última atividade
     */
    public long getInactiveTimeSeconds() {
        return getInactiveTime() / 1000;
    }

    public boolean isWarned() {
        return warned;
    }

    public void setWarned(boolean warned) {
        this.warned = warned;
    }
}

