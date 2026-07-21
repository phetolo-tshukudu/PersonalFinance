package com.phetolo.PersonalFinance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phetolo.PersonalFinance.dto.MonthlyBudgetDTO;
import com.phetolo.PersonalFinance.mapper.MonthlyBudgetMapper;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.services.MonthlyBudgetService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/budget")
public class MonthlyBudgetController {
	private final MonthlyBudgetService budgetService;
	
	@PostMapping
	public ResponseEntity<MonthlyBudgetDTO> createBudget(@AuthenticationPrincipal User user,@RequestBody MonthlyBudgetDTO budgetDto) throws Exception{
		return ResponseEntity.ok(budgetService.createBudget(user.getId(), MonthlyBudgetMapper.mapToEntity(budgetDto)));
	}
	
	@PutMapping("/update")
	public ResponseEntity<MonthlyBudgetDTO> updateBudget(@AuthenticationPrincipal User user,@RequestBody MonthlyBudgetDTO budgetDto) throws Exception{
		return ResponseEntity.ok(budgetService.updateBudget(user.getId(), budgetDto.getAmount()));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<MonthlyBudgetDTO> deleteBudget(@AuthenticationPrincipal User user,@PathVariable Long id) throws Exception{
		MonthlyBudgetDTO dto = budgetService.getBudget(user.getId());
		budgetService.deleteBudget(user.getId(), id);
		return ResponseEntity.ok(dto);
	}
	
	
}
