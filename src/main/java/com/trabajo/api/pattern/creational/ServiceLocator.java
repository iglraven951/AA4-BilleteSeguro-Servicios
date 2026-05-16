package com.trabajo.api.pattern.creational;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================
 * PATRON CREACIONAL: SINGLETON + SERVICE LOCATOR
 * ============================================================================
 *
 * PROPOSITO:
 * - SINGLETON: Garantiza que una clase tenga una unica instancia y proporciona
 *   un punto de acceso global a ella.
 * - SERVICE LOCATOR: Proporciona un registro centralizado para localizar servicios.
 *
 * APLICACION EN ESTE PROYECTO:
 * ServiceLocator actua como punto central para obtener servicios del sistema
 * bancario. Utiliza cache para mejorar el rendimiento en busquedas repetidas.
 *
 * NOTA: En Spring, @Component ya implementa Singleton por defecto.
 * Esta clase demuestra explicitamente el patron y agrega funcionalidad de cache.
 *
 * BENEFICIOS:
 * 1. Punto unico de acceso a servicios
 * 2. Desacopla el cliente de las implementaciones concretas
 * 3. Cache de servicios para mejor rendimiento
 * 4. Facilita el testing (mock de servicios)
 *
 * PRINCIPIO SOLID APLICADO:
 * - Dependency Inversion (D): Depende de abstracciones, no de implementaciones
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class ServiceLocator {

    private static ServiceLocator instance;
    private final ApplicationContext applicationContext;
    private final Map<String, Object> serviceCache;

    @Autowired
    public ServiceLocator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.serviceCache = new ConcurrentHashMap<>();
        instance = this;
    }

    /**
     * Obtiene la instancia unica del ServiceLocator.
     * Patron Singleton explicito para acceso estatico.
     */
    public static ServiceLocator getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ServiceLocator no ha sido inicializado por Spring");
        }
        return instance;
    }

    /**
     * Obtiene un servicio por su tipo de clase.
     * Utiliza cache para mejorar el rendimiento en busquedas repetidas.
     *
     * @param serviceClass Clase del servicio a obtener
     * @return Instancia del servicio
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        String cacheKey = serviceClass.getName();

        // Verificar cache primero
        if (serviceCache.containsKey(cacheKey)) {
            return (T) serviceCache.get(cacheKey);
        }

        // Obtener del contexto de Spring
        T service = applicationContext.getBean(serviceClass);

        // Guardar en cache
        serviceCache.put(cacheKey, service);

        return service;
    }

    /**
     * Obtiene un servicio por su nombre de bean.
     *
     * @param beanName Nombre del bean en Spring
     * @return Instancia del servicio
     */
    public Object getService(String beanName) {
        if (serviceCache.containsKey(beanName)) {
            return serviceCache.get(beanName);
        }

        Object service = applicationContext.getBean(beanName);
        serviceCache.put(beanName, service);

        return service;
    }

    /**
     * Obtiene un servicio por nombre y tipo.
     *
     * @param beanName Nombre del bean
     * @param serviceClass Tipo del servicio
     * @return Instancia del servicio
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(String beanName, Class<T> serviceClass) {
        String cacheKey = beanName + ":" + serviceClass.getName();

        if (serviceCache.containsKey(cacheKey)) {
            return (T) serviceCache.get(cacheKey);
        }

        T service = applicationContext.getBean(beanName, serviceClass);
        serviceCache.put(cacheKey, service);

        return service;
    }

    /**
     * Limpia la cache de servicios.
     * Util para testing o cuando se necesita refrescar servicios.
     */
    public void clearCache() {
        serviceCache.clear();
    }

    /**
     * Verifica si un servicio existe en el contexto.
     *
     * @param serviceClass Clase del servicio
     * @return true si existe, false si no
     */
    public <T> boolean hasService(Class<T> serviceClass) {
        try {
            applicationContext.getBean(serviceClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el numero de servicios en cache.
     * Util para monitoreo y debugging.
     */
    public int getCacheSize() {
        return serviceCache.size();
    }
}
