package me.theuswq.simpleantiafk.utils;

/**
 * Utilitários para formatação de tempo
 * 
 * @author theuswq
 */
public class TimeUtils {

    /**
     * Formata segundos em uma string legível (ex: "5m 30s")
     */
    public static String formatSeconds(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        if (minutes < 60) {
            if (remainingSeconds > 0) {
                return minutes + "m " + remainingSeconds + "s";
            }
            return minutes + "m";
        }

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (remainingMinutes > 0) {
            return hours + "h " + remainingMinutes + "m";
        }
        return hours + "h";
    }
}

