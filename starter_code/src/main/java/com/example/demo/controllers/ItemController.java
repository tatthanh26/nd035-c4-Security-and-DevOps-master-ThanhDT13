package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	@Autowired
	private ItemRepository itemRepository;

	private Logger logger = LoggerFactory.getLogger(ItemController.class);

	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		List<Item> itemList = itemRepository.findAll();
		if (itemList.isEmpty()) {
			logger.info("List item is empty.");
		}
		return ResponseEntity.ok(itemList);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		try {
			Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Can not found item ID: " + id));
			return ResponseEntity.ok(item);
		} catch (EntityNotFoundException e) {
			logger.error(e.getMessage(), new EntityNotFoundException("Can not found item ID: " + id));
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			logger.error("Can not found name: " + name);
			return  ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(items);
	}
	
}
