package com.posts.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<Post> findAll() {
    String sql = "SELECT * FROM post";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class));
  }

  public Optional<Post> findById(Long id) {
    String sql = "SELECT * FROM post WHERE id = ?";
    List<Post> posts = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), id);
    return posts.stream().findFirst();
  }

  public int save(Post post) {
    String sql = "INSERT INTO post (user_name, title, description, timestamp) VALUES (?, ?, ?, ?)";
    return jdbcTemplate.update(sql, post.getUserName(), post.getTitle(), post.getDescription(), LocalDateTime.now());
  }

  public int update(Post post) {
    String sql = "UPDATE post SET title = ?, description = ?, timestamp = ? WHERE id = ?";
    return jdbcTemplate.update(sql, post.getTitle(), post.getDescription(), LocalDateTime.now(), post.getId());
  }

  public int delete(Long id) {
    String sql = "DELETE FROM post WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }
}
