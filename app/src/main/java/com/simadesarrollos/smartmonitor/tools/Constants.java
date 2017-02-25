package com.simadesarrollos.smartmonitor.tools;

/**
 * Created by C POLANIA on 19/01/2017.
 */

/**
 * Clase que contiene los códigos usados para
 * mantener la integridad en las interacciones entre actividades
 * y fragmentos
 */

public class Constants {
    /**
     * Transición Home -> Detalle
     */
    public static final int CODIGO_DETALLE = 100;

    /**
     * Transición Detalle -> Actualización
     */
    public static final int CODIGO_ACTUALIZACION = 101;

    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta carácteristica.
     */
    private static final String PUERTO_HOST = "";

    /**
     * Dominio de SimaDesarrollos
     */
    private static final String IP = "http://www.simadesarrollos.com";
    /**
     * URLs del Web Service
     */
    //public static final String GET = IP + "/Monitoreo/obtener_metas.php";
    public static final String GET_BY_ID_DATE = IP + "/Monitoreo/getLog.php";
    //public static final String UPDATE = IP + "/Monitoreo/actualizar_meta.php";
    //public static final String DELETE = IP + "/Monitoreo/borrar_meta.php";
    public static final String INSERT = IP + "/Monitoreo/setLog.php";

    /**
     * Clave para el valor extra que representa al identificador de una meta
     */
    public static final String EXTRA_ID = "IDEXTRA";

}