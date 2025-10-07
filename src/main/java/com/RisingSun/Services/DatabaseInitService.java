package com.RisingSun.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
public class DatabaseInitService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitService.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        createDatabaseIfNotExists();
    }

    private void createDatabaseIfNotExists() {
        try
        {
            // Проверяем существование базы данных
            Boolean dbExists;
            try
            {
                dbExists = new JdbcTemplate(dataSource).queryForObject("SELECT 1 FROM pg_database WHERE datname = 'messenger_db'", Boolean.class);
            }
            catch (Exception e)
            {
                dbExists = false;
            }

            if (dbExists == null || !dbExists)
            {
                logger.info("📦 Creating database 'messenger_db'...");

                try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement())
                {
                    statement.execute("CREATE DATABASE messenger_db");
                    logger.info("✅ Database 'messenger_db' created successfully");
                }
            }
            else logger.info("✅ Database 'messenger_db' already exists");
        }
        catch (Exception e)
        {
            logger.error("❌ Failed to create database: {}", e.getMessage());
        }
    }
}