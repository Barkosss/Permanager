package common.repositories;

import common.models.Permissions;
import common.models.Server;
import common.utils.LoggerHandler;

import java.util.Map;
import java.util.TreeMap;

public class ServerRepository {
    LoggerHandler logger = new LoggerHandler();
    Map<Long, Server> servers;

    public ServerRepository() {
        this.servers = new TreeMap<>();
    }

    // Создать сервер в памяти
    public Server create(Server server) {
        servers.put(server.getId(), server);
        return server;
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
        return new Server(serverId, null, new Permissions());
    }

    // Существует ли сервер
    public boolean existsById(long serverId) {
        return servers.get(serverId) != null;
    }
}