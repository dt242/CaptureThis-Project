package com.project.capture_this.controller;

import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.service.PostService;
import com.project.capture_this.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;
    private final PostService postService;

    public HomeController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    @GetMapping("/home")
    public String home(Model model) {
        List<DisplayPostDTO> allPosts = postService.findAllPosts();
        return "home";
    }
//
//    @GetMapping("/home")
//    @Transactional
//    public String loggedInIndex(Model model) {
//        if (!userSession.isLoggedIn()) {
//            return "redirect:/";
//        }
//
//        List<PaintingInfoDTO> allPaintings = postService.findAll().stream().map(PaintingInfoDTO::new).toList();
//        List<PaintingInfoDTO> myAddedPaintings = postService.findAll().stream().filter(painting -> painting.getOwner().equals(userService.getLoggedUser())).map(PaintingInfoDTO::new).toList();
//        List<PaintingInfoDTO> otherAddedPaintings = postService.findAll().stream().filter(painting -> !painting.getOwner().equals(userService.getLoggedUser())).map(PaintingInfoDTO::new).toList();
//        List<PaintingInfoDTO> myFavouritePaintings = userService.getLoggedUser().getFavouritePaintings().stream().map(PaintingInfoDTO::new).toList();
//        List<PaintingInfoDTO> myRatedPaintings = allPaintings.stream().filter(paintingInfoDTO -> paintingInfoDTO.getVotes() > 0).toList();
//
//        model.addAttribute("myAddedData", myAddedPaintings);
//        model.addAttribute("otherAddedData", otherAddedPaintings);
//        model.addAttribute("myFavouriteData", myFavouritePaintings);
//        model.addAttribute("myRatedData", myRatedPaintings);
//
//        return "home";
//    }
    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<User> results = userService.searchUsers(query);
        model.addAttribute("query", query);
        model.addAttribute("results", results);
        return "search";
    }
}
