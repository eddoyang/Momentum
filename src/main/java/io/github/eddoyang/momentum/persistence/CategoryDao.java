package io.github.eddoyang.momentum.persistence;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CategoryDao {

    private final JdbcClient db;

    public CategoryDao(JdbcClient db) {
        this.db = db;
    }

    @Transactional
    public void insert(String name) {
        Integer next = db.sql("SELECT COALESCE(MAX(position), -1) + 1 FROM categories")
                        .query(Integer.class)
                        .single();

        db.sql("""
                INSERT INTO categories (name, position) VALUES (:name, :position)
                ON DUPLICATE KEY UPDATE name = name
                """)
            .param("name", name)
            .param("position", next)
            .update();
    }

    public List<String> findAllOrdered() {
        return db.sql("SELECT name FROM categories ORDER BY position")
                .query(String.class)
                .list();
    }

    public void delete(String name) {
        db.sql("DELETE FROM categories WHERE name = :name")
            .param("name", name)
            .update();
    }

    @Transactional
    public void reorder(List<String> order) {
        for (int i = 0; i < order.size(); i++) {
            db.sql("UPDATE categories SET position = :p WHERE name = :n")
                .param("p", i)
                .param("n", order.get(i))
                .update();
        }
    }
}