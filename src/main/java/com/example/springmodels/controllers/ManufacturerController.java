package com.example.springmodels.controllers;

import com.example.springmodels.models.Manufacturer;
import com.example.springmodels.service.ManufacturerApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/manufacturers")
public class ManufacturerController {

    @Autowired
    private ManufacturerApiService manufacturerApiService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String listManufacturers(Model model) {
        List<Manufacturer> manufacturers = manufacturerApiService.findAll();
        model.addAttribute("manufacturers", manufacturers);
        return "manufacturers/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'MANAGER', 'ADMIN')")
    public String viewManufacturer(@PathVariable Long id, Model model) {
        Manufacturer manufacturer = manufacturerApiService.findById(id);
        if (manufacturer == null) {
            throw new IllegalArgumentException("Invalid manufacturer ID: " + id);
        }
        model.addAttribute("manufacturer", manufacturer);
        return "manufacturers/view";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("manufacturer", new Manufacturer());
        return "manufacturers/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String create(@Valid @ModelAttribute Manufacturer manufacturer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "manufacturers/create";
        }
        List<Manufacturer> existing = manufacturerApiService.findAll();
        for (Manufacturer m : existing) {
            if (m.getName().equals(manufacturer.getName())) {
                bindingResult.rejectValue("name", "error.name", "Производитель с таким названием уже существует");
                return "manufacturers/create";
            }
        }
        manufacturerApiService.create(manufacturer);
        return "redirect:/manufacturers";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Manufacturer manufacturer = manufacturerApiService.findById(id);
        if (manufacturer == null) {
            throw new IllegalArgumentException("Invalid manufacturer ID: " + id);
        }
        model.addAttribute("manufacturer", manufacturer);
        return "manufacturers/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Manufacturer manufacturer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "manufacturers/edit";
        }
        Manufacturer existing = manufacturerApiService.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Invalid manufacturer ID: " + id);
        }
        if (!existing.getName().equals(manufacturer.getName())) {
            List<Manufacturer> all = manufacturerApiService.findAll();
            for (Manufacturer m : all) {
                if (m.getName().equals(manufacturer.getName())) {
                    bindingResult.rejectValue("name", "error.name", "Производитель с таким названием уже существует");
                    return "manufacturers/edit";
                }
            }
        }
        manufacturerApiService.update(id, manufacturer);
        return "redirect:/manufacturers/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String delete(@PathVariable Long id) {
        manufacturerApiService.delete(id);
        return "redirect:/manufacturers";
    }
}

