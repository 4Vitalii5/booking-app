package org.cyberrealm.tech.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.springframework.beans.BeanWrapperImpl;

public class FieldsDatesValidator implements ConstraintValidator<FieldsDatesValid, Object> {
    public static final int MIN_DAYS_QUANTITY = 1;
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsDatesValid constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalDate fieldValue = (LocalDate) new BeanWrapperImpl((value)).getPropertyValue(field);
        LocalDate fieldMatchValue = (LocalDate) new BeanWrapperImpl((value))
                .getPropertyValue(fieldMatch);
        if (fieldValue == null || fieldMatchValue == null) {
            return false;
        }
        return fieldValue.plusDays(MIN_DAYS_QUANTITY).isEqual(fieldMatchValue)
                || fieldValue.isBefore(fieldMatchValue);
    }
}
