package com.posts.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

  @Autowired
  private PostRepository postRepository;

  @GetMapping
  public ResponseEntity<List<Post>> getAllPosts() {
    List<Post> posts = postRepository.findAll();
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Post> getPostById(@PathVariable Long id) {
    Optional<Post> post = postRepository.findById(id);
    return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Post> createPost(@RequestBody Post post) {
    postRepository.save(post);
    return ResponseEntity.ok(post);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
    return postRepository.findById(id)
        .map(post -> {
          post.setTitle(updatedPost.getTitle());
          post.setDescription(updatedPost.getDescription());
          postRepository.update(post);
          return ResponseEntity.ok(post);
        }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePost(@PathVariable Long id) {
    if (postRepository.delete(id) > 0) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
