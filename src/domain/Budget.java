package domain;

import java.math.BigDecimal;

public class Budget {

    private Long id;
    private Long categoryId;
    private Integer month;
    private Integer year;
    private BigDecimal limitAmount;
    private BigDecimal currentSpent;

    public Budget(Long categoryId, Integer month, Integer year, BigDecimal limitAmount) {
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.limitAmount = limitAmount;
        this.currentSpent = BigDecimal.ZERO;
    }

    public Budget(Long id, Long categoryId, Integer month, Integer year, BigDecimal limitAmount, BigDecimal currentSpent) {
        this.id = id;
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.limitAmount = limitAmount;
        this.currentSpent = currentSpent;
    }

    public boolean isOverBudget() {
        return currentSpent.compareTo(limitAmount) > 0;
    }

    public void recordExpense(BigDecimal amount) {
        currentSpent = currentSpent.add(amount);
    }

    public Long getId() { return id; }
    public Long getCategoryId() { return categoryId; }
    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public BigDecimal getCurrentSpent() { return currentSpent; }

}
