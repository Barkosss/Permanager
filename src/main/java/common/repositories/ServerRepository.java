package common.repositories;

import common.models.Server;
import common.utils.LoggerHandler;

import java.util.Map;
import java.util.TreeMap;

public class ServerRepository {
    LoggerHandler logger = new LoggerHandler();
    public Map<Long, Server> servers;

    public ServerRepository() {
        this.servers = new TreeMap<>();
    }

    // Создать сервер в памяти
    public void create(Server server) {
        servers.put(server.getId(), server);
    }

    // Удалить сервер
    public void remove(Long serverId) {
        servers.remove(serverId);
    }

    // Найти сервер по ID
    public Server findById(long serverId) {
        Server server;
        if ((server = servers.get(serverId)) != null) {
            return server;
        }
        logger.error("Server by id(" + serverId + ") is not found");
        return null;
    }

    // Существует ли сервер
    public boolean existsById(long serverId) {
        return servers.get(serverId) != null;
    }
}