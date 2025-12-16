package de.othr.event_hub.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.othr.event_hub.dto.FriendshipDTO;
import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.FriendshipService;
import de.othr.event_hub.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/friendships")
public class FriendshipApiController {
    
    @Autowired
    private FriendshipService friendshipService;
    
    @Autowired
    private UserService userService;

    // CRUDs
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<FriendshipDTO>>> getAllFriendships() {
        List<Friendship> friendships = friendshipService.getAllFriendships();
        List<EntityModel<FriendshipDTO>> friendshipModels = toEntityModel(friendships);

        return ResponseEntity.ok(CollectionModel.of(friendshipModels));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FriendshipDTO>> getFriendshipById(@PathVariable("id") Long id) {
        Optional<Friendship> friendshipOpt = friendshipService.getFriendshipById(id);
        
        if (!friendshipOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        FriendshipDTO dto = toDTO(friendshipOpt.get());

        EntityModel<FriendshipDTO> entityModel = EntityModel.of(dto,
            linkTo(methodOn(FriendshipApiController.class).getFriendshipById(id)).withSelfRel(),
            linkTo(methodOn(FriendshipApiController.class).getAllFriendships()).withRel("friendships"));

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<FriendshipDTO>> createFriendship(@RequestBody FriendshipDTO friendship) {
        User requestor;
        User addressee;
        try {
            requestor = userService.getUserById(friendship.getRequestorId());
            addressee = userService.getUserById(friendship.getAddresseeId());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        Friendship newFriendship = new Friendship(
            null, // created automatically by the DB
            requestor, 
            addressee, 
            friendship.getStatus(), 
            friendship.getCreatedAt(), 
            friendship.getAcceptedAt()
        );
        newFriendship = friendshipService.createFriendship(newFriendship);
        return this.getFriendshipById(newFriendship.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<FriendshipDTO>> updateFriendship(@PathVariable("id") Long id, @RequestBody FriendshipDTO friendship) {
        User requestor;
        User addressee;
        try {
            requestor = userService.getUserById(friendship.getRequestorId());
            addressee = userService.getUserById(friendship.getAddresseeId());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        Friendship updatedFriendship = new Friendship(
            id,
            requestor, 
            addressee, 
            friendship.getStatus(), 
            friendship.getCreatedAt(), 
            friendship.getAcceptedAt()
        );
        updatedFriendship = friendshipService.updateFriendship(updatedFriendship);
        return this.getFriendshipById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<FriendshipDTO>> deleteFriendship(@PathVariable("id") Long id) {
        Optional<Friendship> friendship = friendshipService.getFriendshipById(id);
        if (!friendship.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        friendshipService.deleteFriendship(friendship.get());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<EntityModel<FriendshipDTO>> deleteAllFriendships() {
        friendshipService.deleteAllFriendships();
        return ResponseEntity.noContent().build();
    }

    // Custom queries
    @GetMapping("/active/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<FriendshipDTO>>> findActiveFriendshipsByUser(@PathVariable("userId") Long userId) {
        User user;
        try {
            user = userService.getUserById(userId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        List<Friendship> friendships = friendshipService.findActiveFriendshipsByUser(user);
        List<EntityModel<FriendshipDTO>> friendshipModels = toEntityModel(friendships);

        return ResponseEntity.ok(CollectionModel.of(friendshipModels));
    }

    @GetMapping("/requested/by/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<FriendshipDTO>>> findPendingFriendshipsByUser(@PathVariable("userId") Long userId) {
        User user;
        try {
            user = userService.getUserById(userId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        List<Friendship> friendships = friendshipService.findPendingFriendshipsRequestedByUser(user);
        List<EntityModel<FriendshipDTO>> friendshipModels = toEntityModel(friendships);

        return ResponseEntity.ok(CollectionModel.of(friendshipModels));
    }

    @GetMapping("/requested/to/{userId}")
    public ResponseEntity<CollectionModel<EntityModel<FriendshipDTO>>> findPendingFriendshipsToUser(@PathVariable("userId") Long userId) {
        User user;
        try {
            user = userService.getUserById(userId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        List<Friendship> friendships = friendshipService.findPendingFriendshipsRequestedToUser(user);
        List<EntityModel<FriendshipDTO>> friendshipModels = toEntityModel(friendships);

        return ResponseEntity.ok(CollectionModel.of(friendshipModels));
    }
    
    // Helpers
    private FriendshipDTO toDTO(Friendship friendship) {
        return new FriendshipDTO(
            friendship.getId(),
            friendship.getRequestor().getId(),
            friendship.getRequestor().getUsername(),
            friendship.getAddressee().getId(),
            friendship.getAddressee().getUsername(),
            friendship.getStatus(),
            friendship.getCreatedAt(),
            friendship.getAcceptedAt()
        );
    }

    private List<EntityModel<FriendshipDTO>> toEntityModel(List<Friendship> friendships) {
        List<EntityModel<FriendshipDTO>> friendshipModels = friendships.stream()
            .map(friendship -> {
                FriendshipDTO dto = toDTO(friendship);
                return EntityModel.of(dto,
                    linkTo(methodOn(FriendshipApiController.class).getFriendshipById(friendship.getId())).withSelfRel(),
                    linkTo(methodOn(FriendshipApiController.class).getAllFriendships()).withRel("friendships"));
            })
            .collect(Collectors.toList());

        return friendshipModels;
    }
}
