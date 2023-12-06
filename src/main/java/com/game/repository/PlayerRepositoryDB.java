package com.game.repository;

import com.game.entity.Player;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.NamedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
private final SessionFactory sessionFactory;
    public PlayerRepositoryDB() {
        Configuration configuration = new Configuration();

        Properties properties = new Properties();

        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        configuration.setProperties(properties);

        configuration.addAnnotatedClass(Player.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        String sqlQuery = "SELECT * FROM rpg.player";

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            NativeQuery<Player> query = session.createNativeQuery(sqlQuery, Player.class);
            query.setFirstResult(pageNumber * pageSize);
            query.setMaxResults(pageSize);
            List<Player> players = query.getResultList();
            session.getTransaction().commit();

            return players;
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<Long> query = session.createNamedQuery("Player.getAllCount",Long.class);
            Long count = query.getSingleResult();
            session.getTransaction().commit();

            return count.intValue();
        }
    }
    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(player);
            session.getTransaction().commit();

            return player;
        }
    }
    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            player = (Player) session.merge(player);
            session.getTransaction().commit();

            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Player player = session.find(Player.class, id);
            session.getTransaction().commit();

            return Optional.ofNullable(player);
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(player);
            session.getTransaction().commit();

        }
    }

    @PreDestroy
    public void beforeStop() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}