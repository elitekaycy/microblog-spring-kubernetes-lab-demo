package com.gateway.gatewayservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("")
@RestController
public class GatewayController {

  @Autowired
  private DiscoveryClient discoveryClient;

  @GetMapping
  public String test() {
    return "test";
  }

  @GetMapping("/service")
  public List<String> test2() {
    return this.discoveryClient.getServices();
  }

}
