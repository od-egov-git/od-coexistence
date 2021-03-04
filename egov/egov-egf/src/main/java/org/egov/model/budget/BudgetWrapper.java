package org.egov.model.budget;

import java.util.List;

import org.egov.infra.workflow.entity.StateAware;

public class BudgetWrapper extends StateAware {

	  private Long id = null;
	private List<BudgetDetail> budgetDetails;
	 private Budget reBudget;
	public List<BudgetDetail> getBudgetDetails() {
		return budgetDetails;
	}

	public void setBudgetDetails(List<BudgetDetail> budgetDetails) {
		this.budgetDetails = budgetDetails;
	}

	@Override
	public String getStateDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	protected void setId(Long id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	public Budget getReBudget() {
		return reBudget;
	}

	public void setReBudget(Budget reBudget) {
		this.reBudget = reBudget;
	}
}
