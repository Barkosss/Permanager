package common.repositories;

import common.models.Permissions;
import common.models.Server;
import common.utils.LoggerHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ServerRepository {
    LoggerHandler logger = new LoggerHandler();
    CommandRepository commandRepository;
    Map<Long, Server> servers;

    public ServerRepository() {
        this.servers = new TreeMap<>();
        logger.info("ServerRepository initialized", true);
    }

    public void setCommandRepository(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
        logger.info("CommandRepository has been set", true);
    }

    // Создать сервер в памяти
    public Server create(Server server) {
        servers.put(server.getId(), server);
        logger.info("Created server with ID: " + server.getId());
        return server;
    }

    // Удалить сервер
    public void remove(Long serverId) {
        if (servers.containsKey(serverId)) {
            servers.remove(serverId);
            logger.info("Removed server with ID: " + serverId);
        } else {
            logger.warning("Attempt to remove server with non-existing ID: " + serverId);
        }
    }

    // Найти сервер по ID
    public Server findById(long serverId) {
        Server server;
        if ((server = servers.get(serverId)) != null) {
            logger.debug("Found server with ID: " + serverId);
            return server;
        }

        logger.error(String.format("Server by id(%s) is not found. Try create server by id(%s)", serverId, serverId));
        return create(new Server(serverId, null, new Permissions(), commandRepository));
    }

    // Получить список всех серверов
    public List<Server> getAll() {
        logger.debug("Retrieving list of all servers. Total: " + servers.size());
        return new ArrayList<>(servers.values());
    }

    // Существует ли сервер
    public boolean existsById(long serverId) {
        boolean exists = servers.containsKey(serverId);
        logger.debug("Server with ID " + serverId + " exists: " + exists);
        return exists;
    }
}