package net.techcable.sonarpet.nms;

public interface EntityRegistry {
    void registerEntityClass(Class<?> entityClass, String name, int id);
    void unregisterEntityClass(Class<?> entityClass, String name, int id);
    Class<?> getEntityClass(int id);
    int getEntityId(Class<?> entityClass);
    String getEntityName(Class<?> entityClass);
    void registerEntityId(int id, Class<?> entityClass);
    void unregisterEntityId(int id, Class<?> entityClass);
}
