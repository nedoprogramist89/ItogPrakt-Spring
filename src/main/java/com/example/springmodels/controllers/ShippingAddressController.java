package com.example.springmodels.controllers;

import com.example.springmodels.models.ModelUser;
import com.example.springmodels.models.ShippingAddress;
import com.example.springmodels.repos.UserRepository;
import com.example.springmodels.service.ShippingAddressApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/addresses")
@PreAuthorize("hasAuthority('USER') and !hasAnyAuthority('MANAGER', 'ADMIN')")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressApiService shippingAddressApiService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listAddresses(Model model, Authentication authentication) {
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        List<ShippingAddress> addresses = shippingAddressApiService.findByUserId(user.getIdUser());
        model.addAttribute("addresses", addresses);
        return "addresses/list";
    }

    @GetMapping("/{id}")
    public String viewAddress(@PathVariable Long id, Model model, Authentication authentication) {
        ShippingAddress address = shippingAddressApiService.findById(id);
        if (address == null) {
            throw new IllegalArgumentException("Invalid address ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!address.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/addresses";
        }

        model.addAttribute("address", address);
        return "addresses/view";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("address", new ShippingAddress());
        return "addresses/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute ShippingAddress address, BindingResult bindingResult, 
                        Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "addresses/create";
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);
        address.setUser(user);
        shippingAddressApiService.create(address);
        return "redirect:/addresses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        ShippingAddress address = shippingAddressApiService.findById(id);
        if (address == null) {
            throw new IllegalArgumentException("Invalid address ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!address.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/addresses";
        }

        model.addAttribute("address", address);
        return "addresses/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ShippingAddress address, 
                       BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "addresses/edit";
        }
        ShippingAddress existing = shippingAddressApiService.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Invalid address ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!existing.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/addresses";
        }

        address.setId(id);
        address.setUser(user);
        shippingAddressApiService.update(id, address);
        return "redirect:/addresses/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        ShippingAddress address = shippingAddressApiService.findById(id);
        if (address == null) {
            throw new IllegalArgumentException("Invalid address ID: " + id);
        }
        String username = authentication.getName();
        ModelUser user = userRepository.findByUsername(username);

        if (!address.getUser().getIdUser().equals(user.getIdUser())) {
            return "redirect:/addresses";
        }

        shippingAddressApiService.delete(id);
        return "redirect:/addresses";
    }
}

