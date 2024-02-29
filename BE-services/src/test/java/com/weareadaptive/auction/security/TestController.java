package com.weareadaptive.auction.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test/")
public class TestController {

  @GetMapping
  public String test() {
    return "hour";
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("adminOnly")
  public String adminOnly() {
    return "super";
  }

}

